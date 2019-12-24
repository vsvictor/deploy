package ic.chatbot;


import ic.Service;
import ic.struct.list.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.annotations.Same;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.throwables.NotExists;
import ic.throwables.Skip;


public abstract class ChatBotService extends Service {


	@Override protected boolean isReusable() { return false; }


	protected abstract Chat createChat(ChatBotApi api, String id);


	protected abstract @Nullable Storage initStorage();

	private final Storage storage = initStorage(); {
		assert storage != null;
	}

	private final Storage chatsStorage = storage.createFolderIfNotExists("chats");

	private Chat getChatOrCreateAndThrow(ChatBotApi api, String chatId, String chatName) throws NotExists {
		final Storage apiChatsStorage = chatsStorage.createFolderIfNotExists(api.getName());
		final Chat chat;
		final NotExists notExists; synchronized (apiChatsStorage) {
			Chat chatValue;
			NotExists notExistsValue;
			try {
				chatValue = apiChatsStorage.getOrThrow(
					chatId,
					new List.Default<>(ChatBotApi.class, String.class),
					new List.Default<>(api, chatId)
				);
				notExistsValue = null;
			} catch (NotExists notExistsThrown) {
				chatValue = createChat(api, chatId);
				chatValue.onChatStarted();
				apiChatsStorage.set(chatId, chatValue);
				notExistsValue = notExistsThrown;
			}
			chat 		= chatValue;
			notExists 	= notExistsValue;
		}
		chat.setName(chatName);
		if (notExists != null) throw notExists;
		return chat;
	}

	public Collection<Chat> getChats(@NotNull final ChatBotApi api) {
		assert api != null;
		final Storage apiChatsStorage = chatsStorage.createFolderIfNotExists(api.getName());
		synchronized (apiChatsStorage) {
			return new Collection.Default<>(
				new ConvertCollection<>(
					apiChatsStorage.getKeys(),
					chatId -> apiChatsStorage.getNotNull(
						chatId,
						new List.Default<>(ChatBotApi.class, String.class),
						new List.Default<>(api, chatId)
					)
				)
			);
		}
	}

	public Collection<Chat> getChats() {
		return new Collection.Default<>(
			new JoinCollection<>(
				new ConvertCollection<>(
					apis,
					this::getChats
				)
			)
		);
	}


	final synchronized void onChatStarted(final ChatBotApi api, final String chatId, final String chatName) {
		try {
			final Chat chat = getChatOrCreateAndThrow(api, chatId, chatName);
			synchronized (chat) {
				chat.onChatStarted();
			}
		} catch (NotExists ignored) {}
	}

	final void onMessageReceived(final ChatBotApi api, final String chatId, final String chatName, String message) {
		try {
			final Chat chat = getChatOrCreateAndThrow(api, chatId, chatName);
			synchronized (chat) {
				chat.onMessageReceived(message);
			}
		} catch (NotExists ignored) {}
	}

	final void onCommandReceived(final ChatBotApi api, final String chatId, final String chatName, String command) {
		try {
			final Chat chat = getChatOrCreateAndThrow(api, chatId, chatName);
			synchronized (chat) {
				chat.onCommandReceived(command);
			}
		} catch (NotExists ignored) {}
	}

	final void onPhoneReceived(final ChatBotApi api, final String chatId, final String chatName, String command, final String phone) {
		try {
			final Chat chat = getChatOrCreateAndThrow(api, chatId, chatName);
			synchronized (chat) {
				chat.onPhoneReceived(command, phone);
			}
		} catch (NotExists ignored) {}
	}


	// Apis:

	@Same
	protected abstract Collection<ChatBotApi> initApis();

	private final Collection<ChatBotApi> apis = initApis();


	// StoppableAction implementation:

	@Override public synchronized void implementStart() {
		apis.forEach(Service::start);
	}

	@Override public synchronized void implementStop() {
		apis.forEach(Service::stop);
	}


}
