package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.storage.Directory;
import ic.struct.map.UntypedCountableMap;
import ic.struct.set.CountableSet;
import ic.text.CharInput;
import ic.text.SplitString;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.NotExists;

import static ic.ExportKt.export;
import static ic.throwables.InvalidSyntax.INVALID_SYNTAX;


public abstract class ExportCommand extends Command.BaseCommand {

	@Override public String getName() { return "export"; }

	@Override public String getSyntax() { return "export default|gradle <output_path_or_url> <package_names>"; }

	@Override public String getDescription() { return "Export project"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final CharInput argsIterator = args.getIterator();

		final Distribution.ProjectType projectType;
		final CountableSet<String> packageNames;
		final String outputPathOrUrl; try {
			{ final Text projectTypeText = argsIterator.safeReadTill(' ');
				if (projectTypeText.equals("default")) 	projectType = null; 							else
				if (projectTypeText.equals("gradle")) 	projectType = Distribution.ProjectType.GRADLE;
				else throw INVALID_SYNTAX;
			}
			outputPathOrUrl = argsIterator.safeReadTill(' ').toString();
			packageNames = new CountableSet.Default<>(new SplitString(argsIterator.read().toString(), ' '));
		} catch (NotExists notExists) { throw INVALID_SYNTAX; }

		if (outputPathOrUrl.contains(":")) {
			export(
				getDistribution(),
				outputPathOrUrl,
				null,
				projectType,
				new UntypedCountableMap.Default<String>(),
				packageNames,
				new ConsoleUiCallback(console)
			);
		} else {
			export(
				getDistribution(),
				Directory.createIfNotExists(outputPathOrUrl),
				projectType,
				new UntypedCountableMap.Default<String>(),
				packageNames,
				new ConsoleUiCallback(console)
			);
		}

	}

}
