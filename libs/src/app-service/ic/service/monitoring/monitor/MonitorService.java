package ic.service.monitoring.monitor;


import ic.Service;
import ic.network.http.HttpRequest;
import ic.service.monitoring.monitor.model.MonitoredService;
import ic.throwables.NotSupported;


public abstract class MonitorService extends Service {


	@Override protected boolean isReusable() { return false; }


	public abstract String getHost();

	public abstract String getDomainName() throws NotSupported;
	public abstract String getCertEmail() throws NotSupported;

	public abstract String getMonitorKey();


	protected abstract void onServiceIn(MonitoredService monitoredService);
	protected abstract void onServiceOut(String serviceName);

	protected abstract void onServiceStatus(MonitoredService monitoredService, Object status);

	protected abstract void onServiceError(MonitoredService monitoredService, Throwable throwable);

	protected abstract void onRestartStarted(MonitoredService monitoredService);
	protected abstract void onRestartFinished(MonitoredService monitoredService);
	protected abstract void onRestartError(MonitoredService monitoredService, Throwable throwable);

	protected abstract void onRebootStarted(MonitoredService monitoredService);

	protected abstract void onBackupError(MonitoredService monitoredService, Throwable throwable);


	private final CheckerThread checkerThread = new CheckerThread() {
		@Override protected void onServiceStatus(MonitoredService monitoredService, Object status) 		{ MonitorService.this.onServiceStatus(monitoredService, status); 	}
		@Override protected void onServiceError(MonitoredService monitoredService, Throwable throwable) { MonitorService.this.onServiceError(monitoredService, throwable); 	}
		@Override protected void onRestartStarted(MonitoredService monitoredService) 					{ MonitorService.this.onRestartStarted(monitoredService); 			}
		@Override protected void onRestartFinished(MonitoredService monitoredService) 					{ MonitorService.this.onRestartFinished(monitoredService); 			}
		@Override protected void onRestartError(MonitoredService monitoredService, Throwable throwable) { MonitorService.this.onRestartError(monitoredService, throwable); 	}
		@Override protected void onRebootStarted(MonitoredService monitoredService) 					{ MonitorService.this.onRebootStarted(monitoredService); 			}
	};

	private final BackupThread backupThread = new BackupThread() {
		@Override protected void onBackupError(MonitoredService monitoredService, Throwable throwable) { MonitorService.this.onBackupError(monitoredService, throwable); }
	};


	private final MonitorApiIcotpServer monitorApiIcotpServer = new MonitorApiIcotpServer() {
		@Override protected String getMonitorKey() 								{ return MonitorService.this.getMonitorKey(); 			}
		@Override protected void onServiceIn(MonitoredService monitoredService) { MonitorService.this.onServiceIn(monitoredService); 	}
		@Override protected void onServiceOut(String serviceName) 				{ MonitorService.this.onServiceOut(serviceName); 		}
	};

	private final MonitorApiHttpsServer monitorApiHttpsServer = new MonitorApiHttpsServer() {
		@Override protected String getCertEmail() throws NotSupported { return MonitorService.this.getCertEmail(); }
		@Override protected String getDomainName(HttpRequest request) throws NotSupported { return MonitorService.this.getDomainName(); }
	};


	@Override protected void implementStart() {
		monitorApiIcotpServer.start();
		checkerThread.start();
		backupThread.start();
		monitorApiHttpsServer.start();
	}


	@Override protected void implementStop() {
		monitorApiHttpsServer.stop();
		checkerThread.stop();
		backupThread.stop();
		monitorApiIcotpServer.stop();
	}


}
