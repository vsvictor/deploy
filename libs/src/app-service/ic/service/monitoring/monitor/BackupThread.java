package ic.service.monitoring.monitor;


import ic.network.SocketAddress;
import ic.network.icotp.IcotpClient;
import ic.network.icotp.IcotpMode;
import ic.parallel.Thread;
import ic.service.monitoring.Monitoring;
import ic.service.monitoring.api.GetBackup;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.stream.ByteSequence;

import static ic.LoopKt.loop;
import static ic.parallel.SleepKt.sleep;
import static ic.service.monitoring.Backup.getBackupStorage;
import static ic.service.monitoring.monitor.model.MonitoredService.getMonitoredServices;
import static ic.util.Hex.longToFixedSizeHexString;
import static ic.throwables.Break.BREAK;
import static java.lang.System.currentTimeMillis;


public abstract class BackupThread extends Thread {

	protected abstract void onBackupError(MonitoredService monitoredService, Throwable throwable);

	@Override protected void doInParallel() {

		loop(() -> {

			sleep(1000L * 60 * 60 * 16);

			if (toStop()) throw BREAK;

			getMonitoredServices().forEach(monitoredService -> {

				try {

					final ByteSequence backup = IcotpClient.request(
						new SocketAddress(
							monitoredService.host,
							Monitoring.PORT_MONITOR_CLIENT
						),
						IcotpMode.PLAIN,
						new GetBackup()
					);

					if (backup == null) return;

					getBackupStorage(monitoredService).write(
						longToFixedSizeHexString(currentTimeMillis()),
						backup
					);

				} catch (Throwable throwableFromRequest) {

					try {
						onBackupError(monitoredService, throwableFromRequest);
					} catch (Throwable throwableFromOnBackupError) { throwableFromOnBackupError.printStackTrace(); }
				}

			});

			if (toStop()) throw BREAK;

		});

	}

}
