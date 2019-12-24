package ic.apps.ic;


import ic.Distribution;
import ic.Store;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.*;


public abstract class StoreRemoveCommand extends Command.BaseCommand {


	@Override public final String getName() { return "remove"; }

	@Override public final String getSyntax() { return "remove <store_name>"; }

	@Override public String getDescription() { return "Remove store"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override public final void implementRun(final Console console, Text args) throws Fatal {

		final CharInput argsIterator = args.getIterator();

		final String storeName = argsIterator.read().toString();

		try {

			Store.removeStore(
				getDistribution(),
				storeName
			);

		} catch (Fatal.Runtime fatal) {

			console.writeLine(fatal.getMessage());

		} catch (NotExists notExists) {

			console.writeLine("No such store");

		}

	}


}
