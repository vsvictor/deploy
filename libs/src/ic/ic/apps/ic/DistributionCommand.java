package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.SelectorCommand;
import ic.struct.list.List;
import ic.throwables.Fatal;

import static ic.throwables.Skip.SKIP;


public abstract class DistributionCommand extends SelectorCommand {

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected List<Command> initChildren() throws Fatal { return new List.Default<Command>(

		new StoreCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new ListCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new AddCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new RemoveCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new UpdateCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new SetStoreCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		getDistribution().buildOnUpdate ? SKIP : new BuildCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new UploadCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		},

		new ExportCommand() {
			@Override protected Distribution getDistribution() throws Fatal { return DistributionCommand.this.getDistribution(); }
		}

	); }

}
