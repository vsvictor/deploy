package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.struct.set.CountableSet;
import ic.text.SplitString;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.Build.build;


public abstract class BuildCommand extends Command.BaseCommand {

	@Override public String getName() { return "build"; }

	@Override public String getSyntax() { return "build [<package_names>]"; }

	@Override public String getDescription() { return "Compile packages"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final Distribution distribution = getDistribution();

		final CountableSet<String> packagesToBuild; {
			if (args.isEmpty()) {
				packagesToBuild = distribution.getPackageNames();
			} else {
				packagesToBuild = new CountableSet.Default<String>(
					new SplitString(args.toString(), ' ')
				);
			}
		}

		build(
			getDistribution(),
			packagesToBuild,
			new ConsoleUiCallback(console)
		);

	}

}
