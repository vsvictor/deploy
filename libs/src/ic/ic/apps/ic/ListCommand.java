package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.struct.list.ConvertList;
import ic.struct.list.List;
import ic.struct.order.OrderedCountableSet;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;

import static ic.Store.getStoreName;
import static ic.cmd.TableFormatter.writeTable;
import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;


public abstract class ListCommand extends Command.BaseCommand {

	@Override public String getName() { return "list"; }

	@Override public String getSyntax() { return "list"; }

	@Override public String getDescription() { return "List packages"; }

	protected abstract Distribution getDistribution() throws Fatal;

	@Override protected void implementRun(Console console, Text args) throws InvalidSyntax, Fatal {

		final Distribution distribution = getDistribution();

		writeTable(
			console,
			new ConvertList<>(
				new OrderedCountableSet.Default<>(
					ALPHABETIC_STRING_ORDER,
					distribution.getPackageNames()
				),
				packageName -> new List.Default<>(packageName, getStoreName(distribution, packageName))
			),
			1
		);

	}

}
