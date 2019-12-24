package ic.service.commands;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.ServiceActions;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.WrongState;

import static ic.ServiceAppKt.getServiceName;
import static ic.TextResources.getResText;


public class StartCommand extends Command.BaseCommand {

	@Override public String getName() { return "start"; }

	@Override public String getSyntax() { return "start [<user_name>:<user_password>]"; }

	@Override public String getDescription() { return "Start service"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		console.writeLine("Starting service...");

		try {
			ServiceActions.start(getServiceName(), "run " + args);
			console.writeLine(getResText("app-service/service-started"));
		} catch (WrongState wrongState) {
			console.writeLine(getResText("app-service/service-already-started"));
		}

	}

}
