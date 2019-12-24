package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.NotExists;

import static ic.Branch.branch;


public abstract class BranchCommand extends Command.BaseCommand {


	@Override public String getName() { return "branch"; }

	@Override public String getSyntax() { return "branch <package_name> <branch_name>"; }

	@Override public String getDescription() { return "Create new branch"; }


	protected abstract Distribution getDistribution();


	@Override public void implementRun(Console console, Text args) {

		final CharInput argsIterator = args.getIterator();

		final String packageName; try {
			packageName = argsIterator.safeReadTill(' ').toString();
		} catch (NotExists notExists) {
			console.writeLine("Invalid syntax: version can't be empty.");
			return;
		}

		final String version = argsIterator.read(' ').toString();

		try {

			branch(
				getDistribution(),
				packageName,
				version,
				new ConsoleUiCallback(console)
			);

		} catch (Fatal.Runtime fatal) {

			console.writeLine(fatal.getMessage());

		}

	}


}
