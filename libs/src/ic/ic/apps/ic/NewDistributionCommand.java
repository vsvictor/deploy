package ic.apps.ic;


import ic.Distribution;
import org.jetbrains.annotations.Nullable;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.storage.Directory;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.Distributions.createDistribution;
import static ic.throwables.InvalidSyntax.INVALID_SYNTAX;


public class NewDistributionCommand extends Command.BaseCommand {

	@Override public String getName() { return "new"; }

	@Override public String getSyntax() { return "new default|gradle <path>"; }

	@Override public String getDescription() { return "Create new distribution"; }

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final CharInput argsIterator = args.getIterator();

		final @Nullable Distribution.ProjectType projectType; {

			final String projectTypeString = argsIterator.readTill(' ').toString();

			if (projectTypeString.equals("default"))	projectType = null;								else
			if (projectTypeString.equals("gradle")) 	projectType = Distribution.ProjectType.GRADLE;

			else throw INVALID_SYNTAX;

		}

		final Directory rootDirectory; {

			final String path = argsIterator.readTill(' ').toString();

			rootDirectory = Directory.createIfNotExists(path);

		}

		createDistribution(rootDirectory, projectType);

	}

}
