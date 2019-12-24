package ic.cmd;


import org.jetbrains.annotations.NotNull;
import ic.interfaces.condition.Condition1;
import ic.interfaces.stoppable.Stoppable;
import ic.struct.list.List;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.NotExists;

import static ic.cmd.WriteCommandList.writeCommandList;


public abstract class SelectorCommand extends Command.BaseCommand implements Stoppable {


	protected abstract String getShellWelcome();
	protected abstract String getShellTitle();


	protected abstract @NotNull List<Command> initChildren() throws Fatal;

	private List<Command> children;

	private Command runningCommand;


	@Override public void implementRun(final Console console, Text args) throws Fatal, InvalidSyntax {

		final CharInput argsIterator = args.getIterator();

		final String commandName 	= argsIterator.readTill(' ').toString();
		final Text commandArgs		= argsIterator.read();

		if (children == null) {
			children = initChildren();
			assert children != null;
		}

		if (commandName.isEmpty()) {

			if (commandArgs.isEmpty()) {

				new Shell() {
					@Override protected String getShellWelcome() 		{ return SelectorCommand.this.getShellWelcome(); 	}
					@Override protected String getShellTitle() 			{ return SelectorCommand.this.getShellTitle(); 		}
					@Override protected List<Command> getChildren() 	{ return children; 									}
				}.run(console);

			} else {

				console.writeLine("Command name can't be empty.");

				writeCommandList(console, children);

			}

		} else {

			try {

				final Command command = children.findOrThrow(new Condition1<Command>() { @Override public boolean is(Command command) {
					return command.getName().equals(commandName);
				} });

				synchronized (this) {
					runningCommand = command;
				}

				command.run(console, commandArgs);

			} catch (NotExists notExists) {

				console.writeLine("No such command \"" + commandName + "\".");
				writeCommandList(console, children);
				return;

			} finally {

				synchronized (this) {
					runningCommand = null;
				}

			}

		}

	}


	@Override public void stop() {

		final Command runningCommand; {
			synchronized (this) {
				runningCommand = this.runningCommand;
				this.runningCommand = null;
			}
		}

		if (runningCommand instanceof Stoppable) { final Stoppable stoppableCommand = (Stoppable) runningCommand;
			stoppableCommand.stop();
		}

	}


}
