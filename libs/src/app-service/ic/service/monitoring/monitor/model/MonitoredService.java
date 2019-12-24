package ic.service.monitoring.monitor.model;


import ic.annotations.Necessary;
import ic.struct.list.List;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.serial.stream.StreamSerializable;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;

import static ic.Storages.getCommonDataStorage;
import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;
import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.Skip.SKIP;


public class MonitoredService implements StreamSerializable {


	public final @NotNull String name;
	public final @NotNull String appPackageName;

	public final @NotNull String host;
	public final @NotNull Pair<String, String> sshAuth;


	@NotNull @Override public Class getClassToDeclare() { return MonitoredService.class; }

	@Override public void serialize(ByteOutput output) {
		serializeToStream(output, host);
		serializeToStream(output, sshAuth);
		serializeToStream(output, appPackageName);
	}

	@Necessary public MonitoredService(ByteInput input, @NotNull String name) throws UnableToParse {
		this.name = name;
		this.host 			= parseFromStream(input);
		this.sshAuth 		= parseFromStream(input);
		this.appPackageName = parseFromStream(input);
	}


	public MonitoredService(@NotNull String name, @NotNull String host, @NotNull Pair<String, String> sshAuth, @NotNull String appPackageName) throws AlreadyExists {
		this.name 			= name;
		this.host 			= host;
		this.sshAuth 		= sshAuth;
		this.appPackageName = appPackageName;
		final Storage monitoredServicesStorage = getMonitoredServicesStorage();
		synchronized (MonitoredService.class) {
			if (monitoredServicesStorage.getKeys().contains(name)) throw AlreadyExists.ALREADY_EXISTS;
			monitoredServicesStorage.set(name, this);
		}
	}


	private static volatile Storage monitoredServicesStorage;

	private static synchronized Storage getMonitoredServicesStorage() {
		if (monitoredServicesStorage == null) {
			monitoredServicesStorage = new CacheStorage(
				getCommonDataStorage().createFolderIfNotExists("service-monitor").createFolderIfNotExists("monitored-services")
			);
		}
		return monitoredServicesStorage;
	}


	private static synchronized MonitoredService byName(@NotNull String name) throws NotExists {
		return getMonitoredServicesStorage().getOrThrow(name, new List.Default<>(String.class), new List.Default<>(name));
	}


	public static synchronized Collection<MonitoredService> getMonitoredServices() {
		return new ConvertCollection<>(
			getMonitoredServicesStorage().getKeys(),
			serviceName -> {
				try {
					return byName(serviceName);
				} catch (NotExists notFound) { throw SKIP; }
			}
		);
	}


	public static synchronized void removeMonitoredService(String name) throws NotExists {
		if (!monitoredServicesStorage.getKeys().contains(name)) throw NOT_EXISTS;
		monitoredServicesStorage.set(name, null);
	}


}
