package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.struct.set.CountableSet;
import ic.text.SplitString;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.Update.update;


public abstract class UpdateCommand extends Command.BaseCommand {

	@Override public String getName() { return "update"; }

	@Override public String getSyntax() { return "update [<package_names>]"; }

	@Override public String getDescription() { return "Download changes from store"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final Distribution distribution = getDistribution();

		final CountableSet<String> packagesToUpdate; {
			if (args.isEmpty()) {
				packagesToUpdate = distribution.getPackageNames();
			} else {
				packagesToUpdate = new CountableSet.Default<>(
					new SplitString(args.toString(), ' ')
				);
			}
		}

		update(
			getDistribution(),
			packagesToUpdate,
			new CountableSet.Default<String>(),
			new ConsoleUiCallback(console)
		);

	}

}
