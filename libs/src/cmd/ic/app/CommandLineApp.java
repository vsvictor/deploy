package ic.app;


import ic.App;
import ic.annotations.Default;
import ic.annotations.Necessary;
import ic.interfaces.stoppable.Stoppable;
import ic.text.Text;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Command;

import static ic.cmd.UserConsole.USER_CONSOLE;


public abstract class CommandLineApp extends App {


	protected abstract @NotNull Command initCommand();

	private final @NotNull Command command = initCommand();


	@Override protected final void implementRun(Text args) {

		command.run(
			USER_CONSOLE,
			args
		);

	}

	@Default @Override protected void implementStop() {
		if (command instanceof Stoppable) { final Stoppable stoppableCommand = (Stoppable) command;
			stoppableCommand.stop();
		}
	}

	@Necessary public CommandLineApp(Text args) {
		super(args);
	}


}
