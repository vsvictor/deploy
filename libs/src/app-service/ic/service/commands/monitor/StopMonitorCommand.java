package ic.service.commands.monitor;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.ServiceActions;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.WrongState;

import static ic.TextResources.getResText;


public class StopMonitorCommand extends Command.BaseCommand {

	@Override public String getName() { return "stop"; }

	@Override public String getSyntax() { return "stop"; }

	@Override public String getDescription() { return "Stop monitor"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		try {
			ServiceActions.stop("service-monitor");
		} catch (WrongState wrongState) {
			console.writeLine(getResText("app-service/service-not-running"));
		}

	}

}
