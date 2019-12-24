package ic.apps.ic;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.SelectorCommand;
import ic.struct.list.Array;
import ic.struct.list.List;
import ic.throwables.Fatal;

import static ic.Distributions.getGlobalDistribution;
import static ic.Distributions.getLocalDistribution;
import static ic.TextResources.getResString;


public class IcCommand extends SelectorCommand {


	@Override public String getName() { return "ic"; }

	@Override public String getSyntax() { return "ic"; }

	@Override public String getDescription() { return getResString("ic/ic-command-description"); }


	@Override protected String getShellWelcome() {
		return null;
	}

	@Override protected String getShellTitle() { return getResString("ic/ic-shell-title"); }


	@Override protected List<Command> initChildren() { return new Array.Default<Command>(

		new DistributionCommand() {

			@Override protected Distribution getDistribution() throws Fatal { return getLocalDistribution(); }

			@Override public String getName() 				{ return "local"; 									}
			@Override public String getSyntax() 			{ return "local"; 									}
			@Override public String getDescription() 		{ return "Distribution in current work directory"; 	}
			@Override protected String getShellWelcome() 	{ return null; 										}
			@Override protected String getShellTitle() 		{ return "Local distribution:"; 					}

		},

		new DistributionCommand() {

			@Override protected Distribution getDistribution() throws Fatal { return getGlobalDistribution(); }

			@Override public String getName() 				{ return "global"; 						}
			@Override public String getSyntax() 			{ return "global"; 						}
			@Override public String getDescription() 		{ return "System-wide distribution"; 	}
			@Override protected String getShellWelcome() 	{ return null; 							}
			@Override protected String getShellTitle() 		{ return "Global distribution:"; 		}

		},

		new NewDistributionCommand()

	); }


}
