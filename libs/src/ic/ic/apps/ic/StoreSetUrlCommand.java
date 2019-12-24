package ic.apps.ic;


import ic.Distribution;
import ic.Store;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.*;


public abstract class StoreSetUrlCommand extends Command.BaseCommand {


	@Override public final String getName() { return "set-url"; }

	@Override public final String getSyntax() { return "set-url <store_name> <store_url>"; }

	@Override public String getDescription() { return "Change store's url"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override public final void implementRun(final Console console, Text args) throws Fatal, InvalidSyntax {

		final CharInput argsIterator = args.getIterator();

		final String storeName; try {
			storeName = argsIterator.safeReadTill(' ').toString();
		} catch (NotExists notExists) {
			console.writeLine("No url specified");
			return;
		} {
			if (storeName.isEmpty()) {
				console.writeLine("Store name can't be empty");
				return;
			}
		}

		final String url = argsIterator.read().toString(); {
			if (url.isEmpty()) {
				console.writeLine("URL can't be empty");
				return;
			}
		}

		final Distribution distribution = getDistribution();

		try {

			final Store store = Store.byName(distribution, storeName);
			store.setUrl(url);
			Store.save(distribution, store);

		} catch (NotExists notExists) {

			console.writeLine("Store " + storeName + " not exists");

		}

	}


}
