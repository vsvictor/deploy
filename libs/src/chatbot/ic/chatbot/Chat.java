package ic.chatbot;


import ic.struct.list.List;
import ic.struct.map.EditableMap;


public abstract class Chat {

	private final ChatBotApi api;

	private final String id;

	private String name = "";

	public String getName() { return name; }

	protected void setName(String name) { this.name = name; }

	public final void sendMessage(String message) {
		api.sendMessage(id, message);
	}

	public final void sendCode(String code) {
		api.sendCode(id, code);
	}

	private final EditableMap<Button.CommandButton, String> buttonsByCommand = new EditableMap.Default<Button.CommandButton, String>();

	public final void sendButtons(String text, List<Button> buttonList) {
		for (Button button : buttonList) {
			if (button instanceof Button.CommandButton) { final Button.CommandButton commandButton = (Button.CommandButton) button;
				buttonsByCommand.set(commandButton.command, commandButton);
			}
		}
		api.sendButtons(id, text, buttonList);
	}

	public final void sendCarousel(List<CarouselItem> carouselItems) {
		api.sendCarousel(id, carouselItems);
		for (CarouselItem carouselItem : carouselItems) {
			if (carouselItem.button == null) continue;
			if (carouselItem.button instanceof Button.CommandButton) { final Button.CommandButton commandButton = (Button.CommandButton) carouselItem.button;
				buttonsByCommand.set(commandButton.command, commandButton);
			}
		}
	}

	public final void sendImage(String url, String description) {
		api.sendImage(id, url, description);
	}

	protected abstract void onChatStarted();

	protected abstract void onMessageReceived(String message);

	protected void onCommandReceived(String command) {
		final Button.CommandButton button = buttonsByCommand.get(command);
		if (button == null) {
			onChatStarted();
			return;
		}
		button.run();
	}

	protected void onPhoneReceived(String command, String phone) {
		final Button button = buttonsByCommand.get(command);
		if (button == null) {
			onChatStarted();
			return;
		}
		if (button instanceof Button.RequestPhoneButton) { final Button.RequestPhoneButton requestPhoneButton = (Button.RequestPhoneButton) button;
			requestPhoneButton.run(phone);
		}
	}

	public Chat(ChatBotApi api, String id) {
		this.api = api;
		this.id = id;
	}

}
