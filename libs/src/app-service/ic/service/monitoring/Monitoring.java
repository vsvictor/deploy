package ic.service.monitoring;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.storage.StreamStorage;

import static ic.Storages.getCommonDataStorage;


public @Degenerate class Monitoring { @Hide private Monitoring() {}


	public static int PORT_MONITOR = 4096;

	public static int PORT_MONITOR_CLIENT = 4100;


	public static StreamStorage getMonitorDataStorage() {
		return getCommonDataStorage().createFolderIfNotExists("service-monitor");
	}


}
