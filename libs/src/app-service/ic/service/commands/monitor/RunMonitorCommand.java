package ic.service.commands.monitor;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.interfaces.stoppable.Stoppable;
import ic.service.monitoring.monitor.MonitorService;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.parallel.SleepKt.sleepWhile;


public abstract class RunMonitorCommand extends Command.BaseCommand implements Stoppable {


	@Override public String getName() { return "run"; }

	@Override public String getSyntax() { return "run"; }

	@Override public String getDescription() { return "Run monitor as ordinary app"; }


	protected abstract MonitorService initMonitorService();

	private MonitorService monitorService;


	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		console.writeLine("Starting monitor service...");

		monitorService = initMonitorService();

		monitorService.start();

		console.writeLine("Monitor service started.");

		sleepWhile(() -> monitorService.isRunning());

	}

	@Override public void stop() {

		monitorService.stop();

	}


}
