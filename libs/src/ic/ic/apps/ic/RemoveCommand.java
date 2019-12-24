package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.NotExists;
import static ic.Remove.remove;


abstract class RemoveCommand extends Command.BaseCommand {


	@Override public String getName() { return "remove"; }

	@Override public String getSyntax() { return "remove <package_name>"; }

	@Override public String getDescription() { return "Remove package from project"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override public void implementRun(Console console, Text args) throws Fatal {

		final String packageName = args.toString();

		if (packageName.isEmpty()) {
			console.writeLine("Package name can't be empty");
			return;
		}

		try {

			remove(
				getDistribution(),
				packageName
			);

		} catch (NotExists notExists) {

			console.writeLine("Package " + packageName + " not found");

		}

	}


}
