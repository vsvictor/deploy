package ic.apps.ic;


import ic.Distribution;
import ic.Store;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.*;


public abstract class StoreAddCommand extends Command.BaseCommand {


	@Override public final String getName() { return "add"; }

	@Override public final String getSyntax() { return "add <store_name> <store_url>"; }

	@Override public String getDescription() { return "Add new store"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override public final void implementRun(final Console console, Text args) throws Fatal, InvalidSyntax {

		final CharInput argsIterator = args.getIterator();

		final Text storeName; try {
			storeName = argsIterator.safeReadTill(' ');
		} catch (NotExists notExists) {
			console.writeLine("No url specified");
			return;
		} {
			if (storeName.isEmpty()) {
				console.writeLine("Store name can't be empty");
				return;
			}
		}

		final String rawUrl = argsIterator.read().toString(); {
			if (rawUrl.isEmpty()) {
				console.writeLine("URL can't be empty");
				return;
			}
		}

		try {

			Store.addStore(
				getDistribution(),
				storeName.toString(),
				rawUrl,
				new ConsoleUiCallback(console)
			);

		} catch (AlreadyExists alreadyExists) {

			console.writeLine("Store " + storeName + " already exists");

		}

	}


}
