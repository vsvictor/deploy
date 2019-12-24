package d2.polpharma.services.db;


import d2.polpharma.services.db.api.HttpServer;
import ic.Service;
import ic.ServiceApp;
import ic.annotations.Necessary;
import ic.annotations.Repeat;
import ic.service.monitoring.monitor.MonitorService;
import ic.service.monitoring.TelegramMonitorService;
import ic.stream.ByteSequence;
import ic.text.Text;
import ic.throwables.NotSupported;

import static d2.polpharma.services.PolpharmaBackendKt.*;
import static ic.throwables.NotSupported.NOT_SUPPORTED;


public class ThisApp extends ServiceApp {


	@Override public String getPackageName() { return "polpharma-db"; }

	@Necessary @Repeat public ThisApp(Text args) { super(args); }


	@Override protected MonitorService initMonitorService() {
		return null; /*new TelegramMonitorService() {
			@Override protected String getTelegramToken() 	{ return polpharmaMonitorTelegramToken; }
			@Override public String getHost() 				{ return polpharmaMonitorHost; 			}
			@Override public String getMonitorKey() 		{ return polpharmaMonitorKey; 			}
			@Override public String getDomainName() throws NotSupported { throw NOT_SUPPORTED; }
			@Override public String getCertEmail() throws NotSupported { throw NOT_SUPPORTED; }
		};
		*/
	}

	@Override protected Object getStatus() { return null; }

	@Override protected ByteSequence getBackup() { return null; }


	@Override protected Service initService(Text args) {

		return new Service() {

			private final HttpServer httpServer = new HttpServer();

			//private final PharmaciesLocationService pharmaciesLocationService = new PharmaciesLocationService();

			@Override protected boolean isReusable() { return false; }

			@Override protected void implementStart() {

				httpServer.start();
				//pharmaciesLocationService.start();
			}

			@Override protected void implementStop() {

				httpServer.stop();
				//pharmaciesLocationService.stop();

			}

		};

	}


}
