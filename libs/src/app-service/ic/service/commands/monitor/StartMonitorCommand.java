package ic.service.commands.monitor;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.ServiceActions;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.WrongState;

import static ic.TextResources.getResText;


public class StartMonitorCommand extends Command.BaseCommand {

	@Override public String getName() { return "start"; }

	@Override public String getSyntax() { return "start"; }

	@Override public String getDescription() { return "Start monitor service"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		try {
			ServiceActions.start("service-monitor", "monitor run");
		} catch (WrongState wrongState) {
			console.writeLine(getResText("app-service/service-already-started"));
		}

	}

}
