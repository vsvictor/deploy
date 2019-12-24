package ic.service.monitoring.client;


import ic.Service;
import ic.network.SocketAddress;
import ic.network.icotp.IcotpClient;
import ic.network.icotp.IcotpMode;
import ic.service.monitoring.Monitoring;
import ic.service.monitoring.api.ImIn;
import ic.service.monitoring.api.ImOut;
import ic.stream.ByteSequence;
import ic.throwables.Fatal;
import kotlin.Pair;
import static ic.AppKt.getApp;
import static ic.ServiceAppKt.getServiceName;
import static ic.ServiceAppKt.getTier;
import static ic.parallel.DoInParallelKt.doInParallel;


public abstract class MonitorClientService extends Service {


	@Override protected boolean isReusable() { return false; }

	protected abstract String getMonitorHost();

	protected abstract String getMonitorKey();


	protected abstract Pair<String, String> getSshAuth();

	protected abstract Object getStatus() 		throws Fatal;
	protected abstract ByteSequence getBackup() throws Fatal;


	private final MonitorClientApiServer apiServer = new MonitorClientApiServer() {
		@Override protected Object getStatus() throws Fatal 		{ return MonitorClientService.this.getStatus(); }
		@Override protected ByteSequence getBackup() throws Fatal 	{ return MonitorClientService.this.getBackup(); }
	};


	@Override protected void implementStart() {

		apiServer.start();

		doInParallel(() -> {

			try {

				final Object response = IcotpClient.request(
					new SocketAddress(
						getMonitorHost(),
						Monitoring.PORT_MONITOR
					),
					IcotpMode.PLAIN,
					new ImIn(
						getMonitorKey(),
						getServiceName(),
						getSshAuth(),
						getApp().getPackageName()
					)
				);

				assert response == null;

			} catch (Throwable throwable) { throw new RuntimeException(throwable); }

		});

	}


	@Override protected void implementStop() {

		final Object response; try {

			response = IcotpClient.request(
				new SocketAddress(
					getMonitorHost(),
					Monitoring.PORT_MONITOR
				),
				IcotpMode.PLAIN,
				new ImOut(
					getMonitorKey(),
					getTier() + "." + getApp().getPackageName()
				)
			);

		} catch (Throwable throwable) { throw new RuntimeException(throwable); }

		assert response == null;

		apiServer.stop();

	}


}
