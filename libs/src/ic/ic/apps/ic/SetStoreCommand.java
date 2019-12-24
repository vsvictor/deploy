package ic.apps.ic;


import ic.Distribution;
import ic.PackageInstance;
import ic.Store;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.struct.collection.Collection;
import ic.text.CharInput;
import ic.text.ReplaceText;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.NotExists;
import kotlin.Pair;

import static ic.TextResources.getResText;
import static ic.throwables.InvalidSyntax.INVALID_SYNTAX;


public abstract class SetStoreCommand extends Command.BaseCommand {

	@Override public String getName() { return "set-store"; }

	@Override public String getSyntax() { return "set-store <package_name> <store_name>"; }

	@Override public String getDescription() { return "Download changes from store"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final String packageName;
		final String storeName; {
			final CharInput argsIterator = args.getIterator();
			try {
				packageName = argsIterator.safeReadTill(' ').toString();
			} catch (NotExists notExists) { throw INVALID_SYNTAX; }
			storeName = argsIterator.read().toString();
		}

		final Distribution distribution = getDistribution();

		final PackageInstance packageInstance; try {
			packageInstance = PackageInstance.byName(distribution, packageName);
		} catch (NotExists notExists) {
			console.writeLine(new ReplaceText(getResText("ic/package-not-exists"), new Collection.Default<>(
				new Pair<>("$(packageName)", packageName)
			)));
			return;
		}

		final Store store; try {
			store = Store.byName(distribution, storeName);
		} catch (NotExists notExists) {
			console.writeLine(new ReplaceText(getResText("ic/store-not-exists"), new Collection.Default<>(
				new Pair<>("$(storeName)", storeName)
			)));
			return;
		}

		packageInstance.setStore(store);
		packageInstance.save(distribution, packageName);

	}

}
