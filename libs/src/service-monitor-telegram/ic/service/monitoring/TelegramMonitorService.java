package ic.service.monitoring;


import ic.chatbot.Chat;
import ic.chatbot.ChatBotApi;
import ic.chatbot.ChatBotService;
import ic.chatbot.telegram.TelegramChatBotApi;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.service.monitoring.monitor.MonitorService;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.throwables.IOException;
import java.util.Objects;

import static ic.Storages.getCommonDataStorage;


public abstract class TelegramMonitorService extends MonitorService {


	protected abstract String getTelegramToken();


	private ChatBotService chatBotService;


	@Override protected void onServiceIn(final MonitoredService monitoredService) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage(
			"Service " + monitoredService.name + " is in."
		));
	}

	@Override protected void onServiceOut(final String serviceName) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage(
			"Service " + serviceName + " is out."
		));
	}

	@Override protected void onServiceStatus(final MonitoredService monitoredService, final Object status) {

		if (status == null) return;

		if (status instanceof String) { final String statusString = (String) status;

			chatBotService.getChats().forEach(chat -> chat.sendMessage(
				monitoredService.name + " has reported status message:\n\n" + statusString
			));

		}

		else {

			chatBotService.getChats().forEach(chat -> chat.sendMessage(
				monitoredService.name + " has returned object as status:\n\n" + status
			));

		}

	}


	@Override protected void onServiceError(final MonitoredService monitoredService, final Throwable error) {

		if (error instanceof IOException.Runtime) { final IOException.Runtime ioException = (IOException.Runtime) error;

			chatBotService.getChats().forEach(chat -> chat.sendMessage(
				"FATAL: " + monitoredService.name + " is not responsive!"
			));

		}

		else {

			chatBotService.getChats().forEach(chat -> chat.sendMessage(
				"FATAL: " + monitoredService.name + " throws an error:\n\n" + error.toString()
			));

		}

	}


	@Override protected void onRestartStarted(final MonitoredService monitoredService) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage("Restarting " + monitoredService.name + "..."));
	}

	@Override protected void onRestartFinished(final MonitoredService monitoredService) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage("Service " + monitoredService.name + " has been restarted."));
	}

	@Override protected void onRestartError(final MonitoredService monitoredService, final Throwable throwable) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage(
			"Error restarting " + monitoredService.name + ":" + "\n" +
			throwable.getMessage()
		));
	}

	@Override protected void onRebootStarted(final MonitoredService monitoredService) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage("Trying to reboot " + monitoredService.name + "..."));
	}

	@Override protected void onBackupError(final MonitoredService monitoredService, final Throwable throwable) {
		chatBotService.getChats().forEach(chat -> chat.sendMessage(
			"Can't backup " + monitoredService.name + ":" + "\n" +
			throwable.getClass().getName() + " " + Objects.requireNonNullElse(throwable.getMessage(), "")
		));
	}

	@Override protected void implementStart() {
		super.implementStart();
		chatBotService = new ChatBotService() {

			@Override protected Chat createChat(ChatBotApi api, String id) { return new MonitorChat(api, id); }

			@Override protected Storage initStorage() {
				return getCommonDataStorage().createFolderIfNotExists("service-monitor").createFolderIfNotExists("chatbot");
			}

			@Override protected Collection<ChatBotApi> initApis() { return new List.Default<>(

				new TelegramChatBotApi(this) {
					@Override protected String getToken() { return TelegramMonitorService.this.getTelegramToken(); }
				}

			); }

		};
		chatBotService.start();
	}

	@Override protected void implementStop() {
		super.implementStop();
		chatBotService.stop();
	}


}
