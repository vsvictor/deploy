package d2.polpharma.services.ophtik;


import d2.polpharma.services.ophtik.api.OphtikApiServer;
import ic.Service;
import ic.ServiceApp;
import ic.annotations.Necessary;
import ic.annotations.Repeat;
import ic.service.monitoring.TelegramMonitorService;
import ic.service.monitoring.monitor.MonitorService;
import ic.stream.ByteSequence;
import ic.text.Text;
import ic.throwables.NotSupported;

import static ic.throwables.NotSupported.NOT_SUPPORTED;


public class OphtikServiceApp extends ServiceApp { @Necessary @Repeat public OphtikServiceApp(Text args) { super(args); }


	@Override public String getPackageName() { return "polpharma-ophtik"; }


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

			private final OphtikApiServer apiServer = new OphtikApiServer();

			@Override protected void implementStart() {
				apiServer.start();
			}

			@Override protected void implementStop() {
				apiServer.stop();
			}

		};

	}


}
