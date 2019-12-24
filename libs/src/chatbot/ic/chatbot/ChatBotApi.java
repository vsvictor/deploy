package ic.chatbot;


import ic.Service;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Same;
import ic.struct.list.List;


public abstract class ChatBotApi extends Service {


	@Same
	protected abstract String getName();


	private final ChatBotService chatBotService;


	protected final void onChatStarted(String chatId, String chatName) {
		chatBotService.onChatStarted(this, chatId, chatName);
	}

	protected final void onMessageReceived(String chatId, String chatName, String message) {
		chatBotService.onMessageReceived(this, chatId, chatName, message);
	}

	protected final void onCommandReceived(String chatId, String chatName, String command) {
		chatBotService.onCommandReceived(this, chatId, chatName, command);
	}

	protected final void onPhoneReceived(String chatId, String chatName, String command, String phone) {
		chatBotService.onPhoneReceived(this, chatId, chatName, command, phone);
	}


	protected abstract void implementSendMessage(String chatId, String message);

	public final void sendMessage(String chatId, String message) {
		implementSendMessage(chatId, message);
	}


	protected abstract void implementSendCode(String chatId, String code);

	public final void sendCode(String chatId, String code) {
		implementSendCode(chatId, code);
	}


	protected abstract void implementSendButtons(String chatId, String text, List<Button> buttons);

	public final void sendButtons(String chatId, String text, List<Button> buttons) {
		implementSendButtons(chatId, text, buttons);
	}


	protected abstract void implementSendCarousel(String chatId, List<CarouselItem> carousel);

	public final void sendCarousel(String chatId, List<CarouselItem> carousel) {
		implementSendCarousel(chatId, carousel);
	}

	protected abstract void implementSendImage(String chatId, String url, String description);

	public final void sendImage(String chatId, String url, String description) {
		implementSendImage(chatId, url, description);
	}


	public ChatBotApi(@NotNull ChatBotService chatBotService) {
		assert chatBotService != null;
		this.chatBotService = chatBotService;
	}


}
