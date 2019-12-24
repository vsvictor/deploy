package ic.service.commands.monitor;


import ic.cmd.Command;
import ic.cmd.SelectorCommand;
import ic.service.monitoring.monitor.MonitorService;
import ic.struct.list.List;
import ic.throwables.Fatal;


public abstract class MonitorCommand extends SelectorCommand {


	@Override public String getName() { return "monitor"; }

	@Override public String getSyntax() { return "monitor"; }

	@Override public String getDescription() { return "Monitor commands"; }

	@Override protected String getShellWelcome() { return null; }

	@Override protected String getShellTitle() { return "Monitor shell:"; }


	protected abstract MonitorService initMonitorService();


	@Override protected List<Command> initChildren() throws Fatal { return new List.Default<Command>(

		new RunMonitorCommand() {
			@Override protected MonitorService initMonitorService() { return MonitorCommand.this.initMonitorService(); }
		},

		new StartMonitorCommand(),
		new StopMonitorCommand(),
		new RestartMonitorCommand()

	); }


}
