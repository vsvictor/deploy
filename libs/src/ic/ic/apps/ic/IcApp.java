package ic.apps.ic;


import ic.annotations.Necessary;
import ic.annotations.Repeat;
import ic.app.CommandLineApp;
import ic.cmd.Command;
import ic.text.Text;


public class IcApp extends CommandLineApp {


	@Override public String getPackageName() { return "ic"; }


	@Necessary @Repeat public IcApp(Text args) { super(args); }


	@Override protected Command initCommand() {
		return new IcCommand();
	}


}
