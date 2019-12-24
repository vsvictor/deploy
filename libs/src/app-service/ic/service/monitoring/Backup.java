package ic.service.monitoring;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.storage.StreamStorage;

import static ic.service.monitoring.Monitoring.getMonitorDataStorage;


public @Degenerate class Backup { @Hide private Backup() {}


	public static StreamStorage getBackupStorage(MonitoredService monitoredService) {
		return getMonitorDataStorage().createFolderIfNotExists("backup").createFolderIfNotExists(monitoredService.name);
	}


}
