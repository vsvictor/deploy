package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.Text;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import ic.throwables.Fatal;

import static ic.Add.add;


abstract class AddCommand extends Command.BaseCommand {


	@Override public String getName() { return "add"; }

	@Override public String getSyntax() { return "add <package_name>"; }

	@Override public String getDescription() { return "Add package to project"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override public void implementRun(Console console, Text args) throws Fatal {

		final String packageName = args.toString();

		if (packageName.isEmpty()) {
			console.writeLine("Package name can't be empty");
			return;
		}

		try {

			add(
				getDistribution(),
				packageName,
				new ConsoleUiCallback(console)
			);

		} catch (AlreadyExists alreadyExists) {

			console.writeLine("Package " + packageName + " already added");

		} catch (NotExists notExists) {

			console.writeLine("Package " + packageName + " not found");

		}

	}


}
