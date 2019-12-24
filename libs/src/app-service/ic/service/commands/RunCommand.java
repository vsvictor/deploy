package ic.service.commands;


import ic.Service;
import ic.cmd.SystemConsole;
import kotlin.Pair;
import org.jetbrains.annotations.Nullable;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.interfaces.stoppable.Stoppable;
import ic.service.monitoring.client.MonitorClientService;
import ic.service.monitoring.monitor.MonitorService;
import ic.stream.ByteSequence;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.ServiceAppKt.getTier;
import static ic.parallel.SleepKt.sleepWhile;


public abstract class RunCommand extends Command.BaseCommand implements Stoppable {


	@Override public String getName() { return "run"; }

	@Override public String getSyntax() { return "run [<user_name>:<user_password>]"; }

	@Override public String getDescription() { return "Run as ordinary app"; }


	protected abstract Service initService(Text args);

	protected abstract MonitorService initMonitorService();

	protected abstract Object getStatus() throws Fatal;
	protected abstract ByteSequence getBackup() throws Fatal;


	private volatile Service service;


	private @Nullable MonitorClientService monitorClientService;


	@Override protected void implementRun(final Console console, Text args) throws InvalidSyntax, Fatal {

		final @Nullable Pair<String, String> sshAuth; {
			if (args.isEmpty()) {
				sshAuth = null;
			} else {
				final String[] argsSplit = args.toString().split(":");
				sshAuth = new Pair<>(argsSplit[0], argsSplit[1]);
			}
		}
		final Service service = initService(args);
		assert service != null;
		this.service = service;
		console.writeLine("Starting service...");
		try {
			service.start();
			console.writeLine("Service started.");
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		} finally {
			if (sshAuth != null && getTier() != null) {
				final MonitorService monitorService = initMonitorService();
				if (monitorService != null) {
					monitorClientService = new MonitorClientService() {
						@Override protected String getMonitorHost() 				{ return monitorService.getHost(); 			}
						@Override protected String getMonitorKey() 					{ return monitorService.getMonitorKey(); 	}
						@Override protected Pair<String, String> getSshAuth() 		{ return sshAuth; 							}
						@Override protected Object getStatus() throws Fatal 		{ return RunCommand.this.getStatus(); 		}
						@Override protected ByteSequence getBackup() throws Fatal 	{ return RunCommand.this.getBackup(); 		}
					};
					console.writeLine("Starting monitored service...");
					monitorClientService.start();
					console.writeLine("Monitored service started.");
				}
			}
			sleepWhile(service::isRunning);
		}
	}


	@Override public void stop() {
		if (monitorClientService != null) {
			monitorClientService.stop();
		}
		service.stop();
	}


}
