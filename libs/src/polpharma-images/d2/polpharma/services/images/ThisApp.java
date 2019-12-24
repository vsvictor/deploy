package d2.polpharma.services.images;


import ic.Service;
import ic.ServiceApp;
import ic.annotations.Necessary;
import ic.annotations.Repeat;
import ic.service.monitoring.monitor.MonitorService;
import ic.service.monitoring.TelegramMonitorService;
import ic.stream.ByteSequence;
import ic.text.Text;
import ic.throwables.NotSupported;

import static ic.throwables.NotSupported.NOT_SUPPORTED;


public class ThisApp extends ServiceApp {


	@Override public String getPackageName() { return "polpharma-images"; }

	@Necessary @Repeat public ThisApp(Text args) { super(args); }


	@Override protected MonitorService initMonitorService() {
		return new TelegramMonitorService() {
			@Override protected String getTelegramToken() 	{ return "837158622:AAHDpKJr-5VhnPG9_Zi5TGijU5RBSTx6yPM"; 	}
			@Override public String getHost() 				{ return "99.80.94.209"; 									}
			@Override public String getDomainName() throws NotSupported { throw NOT_SUPPORTED; }
			@Override public String getCertEmail() throws NotSupported { throw NOT_SUPPORTED; }
			@Override public String getMonitorKey() 		{ return "polpharma"; 										}
		};
	}

	@Override protected Object getStatus() { return null; }

	@Override protected ByteSequence getBackup() { return null; }


	@Override protected Service initService(Text args) {

		return new Service() {

			private final ImagesHttpsServer httpServer = new ImagesHttpsServer();

			@Override protected boolean isReusable() { return false; }

			@Override protected void implementStart() {
				httpServer.start();
			}

			@Override protected void implementStop() {
				httpServer.stop();
			}

		};

	}


}
