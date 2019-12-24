package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.NotExists;
import ic.throwables.NotNeeded;

import static ic.SwitchVersion.switchVersion;


public abstract class SwitchCommand extends Command.BaseCommand {


	@Override public String getName() { return "switch"; }

	@Override public String getSyntax() { return "switch <package_name> <version>"; }

	@Override public String getDescription() { return "Checkout to branch or commit"; }


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

			switchVersion(
				getDistribution(),
				packageName,
				version,
				new ConsoleUiCallback(console)
			);

		} catch (NotNeeded notNeeded) {

			console.writeLine("Already on " + version);

		} catch (Fatal.Runtime fatal) {

			console.writeLine(fatal.getMessage());

		}

	}


}
