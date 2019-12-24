package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.struct.set.CountableSet;
import ic.text.SplitString;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.Upload.upload;


public abstract class UploadCommand extends Command.BaseCommand {

	@Override public String getName() { return "upload"; }

	@Override public String getSyntax() { return "upload [<package_names>]"; }

	@Override public String getDescription() { return "Upload changes to store"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final Distribution distribution = getDistribution();

		final CountableSet<String> packagesToUpload; {
			if (args.isEmpty()) {
				packagesToUpload = distribution.getPackageNames();
			} else {
				packagesToUpload = new CountableSet.Default<String>(
					new SplitString(args.toString(), ' ')
				);
			}
		}

		upload(
			getDistribution(),
			packagesToUpload,
			new ConsoleUiCallback(console)
		);

	}

}
