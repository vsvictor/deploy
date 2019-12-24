package ic.service.monitoring.monitor;


import ic.cmd.Sudo;
import ic.cmd.ssh.SshSession;
import ic.network.SocketAddress;
import ic.network.icotp.IcotpClient;
import ic.network.icotp.IcotpMode;
import ic.parallel.Thread;
import ic.service.monitoring.Monitoring;
import ic.service.monitoring.api.GetStatus;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.throwables.Fatal;

import static ic.ExportKt.getExportedLaunchPath;
import static ic.LoopKt.loop;
import static ic.parallel.SleepKt.sleep;
import static ic.service.monitoring.monitor.model.MonitoredService.getMonitoredServices;
import static ic.throwables.Break.BREAK;


public abstract class CheckerThread extends Thread {

	protected abstract void onServiceStatus(MonitoredService monitoredService, Object status);
	protected abstract void onServiceError(MonitoredService monitoredService, Throwable throwable);

	protected abstract void onRestartStarted(MonitoredService monitoredService);
	protected abstract void onRestartFinished(MonitoredService monitoredService);
	protected abstract void onRestartError(MonitoredService monitoredService, Throwable throwable);

	protected abstract void onRebootStarted(MonitoredService monitoredService);

	@Override protected void doInParallel() {

		loop(() -> {

			if (toStop()) throw BREAK;
			sleep(1000L * 60 * 10);
			getMonitoredServices().forEach(monitoredService -> {

				try {

					final String response = IcotpClient.request(
						new SocketAddress(
							monitoredService.host,
							Monitoring.PORT_MONITOR_CLIENT
						),
						IcotpMode.PLAIN,
						new GetStatus()
					);

					try {
						onServiceStatus(monitoredService, response);
					} catch (Throwable throwableFromOnServiceStarted) { throwableFromOnServiceStarted.printStackTrace(); }

				} catch (Throwable throwableFromRequest) {

					try {
						onServiceError(monitoredService, throwableFromRequest);
					} catch (Throwable throwableFromOnServiceError) { throwableFromOnServiceError.printStackTrace(); }

					try {
						onRestartStarted(monitoredService);
					} catch (Throwable throwableFromOnRestartStarted) { throwableFromOnRestartStarted.printStackTrace(); }

					try {

						final SshSession sshSession = new SshSession(monitoredService.host, monitoredService.sshAuth); try {

							sshSession.writeLine("cd /deploy");

							sshSession.writeLine("cd " + monitoredService.name);

							new Sudo(
								sshSession,
								() -> monitoredService.sshAuth.getSecond()
							).writeLine(
								getExportedLaunchPath() + "/" + monitoredService.appPackageName + " restart " + monitoredService.sshAuth.getFirst() + ":" + monitoredService.sshAuth.getSecond()
							);

							final String response = sshSession.read().toString();

							if (!response.isEmpty()) {
								throw new Fatal(response);
							}

						} finally {

							sshSession.close();

						}

						try {
							onRestartFinished(monitoredService);
						} catch (Throwable throwableFromOnRestartFinished) { throwableFromOnRestartFinished.printStackTrace(); }

					} catch (Throwable throwableWhileRestarting) {

						try {
							onRestartError(monitoredService, throwableWhileRestarting);
						} catch (Throwable throwableFromOnRestartError) { throwableFromOnRestartError.printStackTrace(); }

						try {
							onRebootStarted(monitoredService);
						} catch (Throwable throwableFromOnRestartStarted) { throwableFromOnRestartStarted.printStackTrace(); }

						try {

							final SshSession sshSession = new SshSession(monitoredService.host, monitoredService.sshAuth);

							new Sudo(
								sshSession,
								() -> monitoredService.sshAuth.getSecond()
							).writeLine("reboot");

						} catch (Throwable throwableFromReboot) {}

					}

				}

			});

			if (toStop()) throw BREAK;

		});

	}

}
