package ic.chatbot.telegram;


import ic.annotations.Repeat;
import ic.annotations.Same;
import ic.annotations.ToOverride;
import ic.chatbot.Button;
import ic.chatbot.CarouselItem;
import ic.chatbot.ChatBotApi;
import ic.chatbot.ChatBotService;
import ic.mimetypes.MimeType;
import ic.network.http.HttpClient;
import ic.network.http.HttpException;
import ic.network.http.HttpRequest;
import ic.parallel.Thread;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.text.Charset;
import ic.throwables.IOException;
import ic.throwables.NotImplementedYet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.parallel.SleepKt.sleep;


public abstract class TelegramChatBotApi extends ChatBotApi { @Repeat public TelegramChatBotApi(ChatBotService chatBotService) { super(chatBotService); }


	@Override protected boolean isReusable() { return false; }

	@ToOverride
	@Override protected String getName() { return "telegram"; }

	@Same
	protected abstract String getToken();


	private final Thread workerThread = new Thread() { @Override protected void doInParallel() {

		Integer lastUpdateId = null;

		while (true) {

			if (toStop()) break;

			final String url; {
				String value = "https://api.telegram.org/bot" + getToken() + "/getUpdates";
				if (lastUpdateId != null) value += "?offset=" + lastUpdateId;
				url = value;
			}

			final String response; try {

				response = Charset.DEFAULT_HTTP.bytesToString(
					HttpClient.request(url).getBody()
				);

			} catch (IOException e) {
				e.printStackTrace();
				continue;
			} catch (HttpException e) {
				e.printStackTrace();
				continue;
			}

			try {

				final JSONObject responseJson = new JSONObject(response);

				final JSONArray updatesJson = responseJson.getJSONArray("result");

				final int updatesCount = updatesJson.length();

				if (updatesCount == 0) {
					sleep(4000);
					continue;
				}

				for (int i = 0; i < updatesCount; i++) { final JSONObject updateJson = updatesJson.getJSONObject(i);

					final JSONObject messageJson = updateJson.optJSONObject("message");

					if (messageJson == null) continue;

					final JSONObject chatJson = messageJson.getJSONObject("chat");

					final int chatId = chatJson.getInt("id");

					final String chatName = chatJson.optString("username", "");
					final String messageText = messageJson.getString("text");

					if (messageText.startsWith("/")) {
						onCommandReceived(
							Integer.toString(chatId),
							chatName,
							messageText
						);
					} else {
						onMessageReceived(
							Integer.toString(chatId),
							chatName,
							messageText
						);
					}

				}

				final JSONObject lastUpdateJson = updatesJson.getJSONObject(updatesCount - 1);

				lastUpdateId = lastUpdateJson.getInt("update_id") + 1;

			} catch (JSONException e) {
				e.printStackTrace();
				System.out.println("RESPONSE:");
				System.out.println(response);
			}

		}

	} };


	@Override public void implementStart() {
		workerThread.start();
	}

	@Override public void implementStop() {
		workerThread.stop();
	}

	@Override public void implementSendMessage(String chatId, String message) {

		try {

			final String url = (
				"https://api.telegram.org/bot" + getToken() + "/sendMessage?" +
				"chat_id=" + chatId + "&" +
				"text=" + Charset.DEFAULT_HTTP.encodeUrl(message)
			);

			HttpClient.request(url);

		} catch (IOException e) 	{ e.printStackTrace();
		} catch (HttpException e) 	{ e.printStackTrace(); }

	}

	@Override protected void implementSendCode(String chatId, String code) {

		code = code.replace("&", "&amp;");
		code = code.replace("<", "&lt;");
		code = code.replace(">", "&gt;");

		try {

			final JSONObject requestJson = new JSONObject(); {
				requestJson.put("chat_id", chatId);
				requestJson.put("parse_mode", "html");
				requestJson.put("text", "<pre>" + code + "</pre>");
			}

			HttpClient.request(new HttpRequest(
				"https://api.telegram.org/bot" + getToken() + "/sendMessage", "POST",
				new CountableMap.Default<String, String>(
					"Content-Type", MimeType.JSON.name
				),
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			));

		} catch (IOException e) 	{ e.printStackTrace();
		} catch (HttpException e) 	{ e.printStackTrace(); }

	}

	@Override public void implementSendButtons(String chatId, String text, List<Button> buttonList) {
		throw new NotImplementedYet();
		/*sendMessage(chatId, new JoinStrings('\n', new JoinList<String>(
			new List.Default<String>(
				text
			),
			new ConvertList<String, Button>(buttonList) { @Override protected String convert(Button button) {
				return button.command + " " + button.getText();
			} }
		)).toString());*/
	}

	@Override public void implementSendCarousel(String chatId, List<CarouselItem> carouselItemsList) {
		throw new NotImplementedYet();
		/*sendMessage(chatId, new JoinStrings('\n', new JoinList<String>(
			new List.Default<String>(
				"Carousel:"
			),
			new ConvertList.FromSourceList<String, CarouselItem>(carouselItemsList) {
				@Override protected String convert(CarouselItem carouselItem) {
					return carouselItem.getTitle() + " " + carouselItem.getImageUrl() + " carousel button: " + carouselItem.button.command;
				}
			}
		)).toString());*/
	}

	@Override protected void implementSendImage(String chatId, String url, String description) {
		throw new NotImplementedYet();
	}

}
