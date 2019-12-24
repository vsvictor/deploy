package ic.service.monitoring;


import ic.annotations.Necessary;
import org.jetbrains.annotations.Nullable;
import ic.chatbot.Chat;
import ic.chatbot.ChatBotApi;
import ic.serial.stream.StreamSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.util.Hex;
import ic.throwables.Skip;

import java.util.Date;

import static ic.date.DateFormat.formatDate;
import static ic.service.monitoring.Backup.getBackupStorage;
import static ic.service.monitoring.monitor.model.MonitoredService.getMonitoredServices;
import static ic.struct.collection.Collections.maxLong;
import static ic.util.Hex.hexStringToLong;


public class MonitorChat extends Chat implements StreamSerializable {


	@Override protected void onChatStarted() {
		sendMessage("You have subscribed to monitor service. Hello!");
	}

	@Override protected void onMessageReceived(String message) {
		sendMessage(
			"You just sent a message \"" + message + "\". " +
			"If you meant to check if monitor is still running - yes, it is. " +
			"Write /list to show currently monitored services."
		);
	}

	@Override protected void onCommandReceived(String command) {
		if (command.equals("/list")) {
			sendMessage("This services are currently monitored:");
			getMonitoredServices().forEach(monitoredService -> {
				final @Nullable Date lastBackupDate; {
					final Collection<String> backupKeys = getBackupStorage(monitoredService).getKeys();
					if (backupKeys.isEmpty()) {
						lastBackupDate = null;
					} else {
						lastBackupDate = new Date(maxLong(
							new ConvertCollection<>(
								backupKeys,
								Hex::hexStringToLong
							)
						));
					}
				}
				sendMessage(
					monitoredService.name + ":" + "\n" +
					"Host: " + monitoredService.host + "\n" +
					"Last backup: " + (
						lastBackupDate == null ? "never" : formatDate(lastBackupDate, "yyyy.MM.dd HH:mm")
					)
				);
			});
		}
	}


	@Override public Class getClassToDeclare() { return MonitorChat.class; }

	@Override public void serialize(ByteOutput output) {

	}

	@Necessary public MonitorChat(ByteInput input, ChatBotApi api, String id) {
		super(api, id);
	}


	public MonitorChat(ChatBotApi api, String id) {
		super(api, id);
	}


}
