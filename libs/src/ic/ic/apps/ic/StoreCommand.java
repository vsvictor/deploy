package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.cmd.SelectorCommand;
import ic.struct.list.List;
import ic.throwables.Fatal;
import kotlin.Unit;


public abstract class StoreCommand extends SelectorCommand {


	@Override public String getName() { return "store"; }

	@Override public String getSyntax() { return "store"; }

	@Override public String getDescription() { return "Stores shell"; }


	@Override protected String getShellWelcome() { return null; }

	@Override protected String getShellTitle() { return "Stores shell:"; }


	protected abstract Distribution getDistribution() throws Fatal;


	@Override protected List<Command> initChildren() { return new List.Default<Command>(

		new StoreAddCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return StoreCommand.this.getDistribution(); }
		},

		new StoreRemoveCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return StoreCommand.this.getDistribution(); }
		},

		new StoreSetUrlCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return StoreCommand.this.getDistribution(); }
		}

	); }


}
