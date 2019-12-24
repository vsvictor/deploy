package ic.service.commands.monitor;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.ServiceActions;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;


public class RestartMonitorCommand extends Command.BaseCommand {

	@Override public String getName() { return "restart"; }

	@Override public String getSyntax() { return "restart"; }

	@Override public String getDescription() { return "Restart monitor service"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		console.writeLine("Restarting monitor...");

		ServiceActions.restart("service-monitor", "monitor run");

		console.writeLine("Monitor restarted.");

	}

}
