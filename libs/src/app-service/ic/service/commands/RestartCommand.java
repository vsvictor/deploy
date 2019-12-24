package ic.service.commands;


import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.ServiceActions;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.ServiceAppKt.getServiceName;
import static ic.TextResources.getResText;


public class RestartCommand extends Command.BaseCommand {

	@Override public String getName() { return "restart"; }

	@Override public String getSyntax() { return "restart [<user_name>:<user_password>]"; }

	@Override public String getDescription() { return "Restart service"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		console.writeLine(getResText("app-service/service-restarting"));

		ServiceActions.restart(getServiceName(), "run " + args);

		console.writeLine(getResText("app-service/service-restarted"));

	}

}
