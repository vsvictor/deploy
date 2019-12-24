package ic.cmd;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.interfaces.action.SafeAction;
import ic.interfaces.condition.Condition1;
import ic.struct.list.List;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Break;
import ic.throwables.NotExists;

import static ic.LoopKt.loop;
import static ic.parallel.SleepKt.sleep;
import static ic.throwables.Break.BREAK;
import static java.lang.System.currentTimeMillis;
import static ic.cmd.WriteCommandList.writeCommandList;


public abstract class Shell {


	protected abstract @Nullable String getShellWelcome();
	protected abstract @Nullable String getShellTitle();


	protected abstract @NotNull List<Command> getChildren();


	public void run(@NotNull final Console console) {

		assert console 	!= null;

		final List<Command> children = getChildren();

		assert children != null;

		{ final String shellWelcome = getShellWelcome();
			if (shellWelcome != null) console.writeLine(shellWelcome);
		}

		loop(new SafeAction<Break>() {

			long commandStartTime;

			@Override public void run() throws Break {

				if (currentTimeMillis() - commandStartTime > 16384L) {

					{ final String shellTitle = getShellTitle();
						if (shellTitle != null) console.writeLine(shellTitle);
					}

					writeCommandList(console, children);

				}

				sleep(256);

				console.write(": ");

				final Text input = console.readLine();

				if (input.equals("exit")) throw BREAK;

				commandStartTime = currentTimeMillis();

				final CharInput inputIterator = input.getIterator();

				final String commandName = inputIterator.readTill(' ').toString();

				final Text args = inputIterator.read();

				if (commandName.isEmpty()) {

					if (args.isEmpty()) {

						{ final String shellTitle = getShellTitle();
							if (shellTitle != null) console.writeLine(shellTitle);
						}

						writeCommandList(console, children);

					} else {

						console.writeLine("Command name can't be empty.");

						writeCommandList(console, children);

					}

				} else {

					final Command command; try {

						command = children.findOrThrow(new Condition1<Command>() { @Override public boolean is(Command command) {
							return command.getName().equals(commandName);
						} });

					} catch (NotExists notExists) {

						console.writeLine("No such command \"" + commandName + "\".");
						writeCommandList(console, children);
						return;

					}

					command.run(console, args);

				}

			}

		});

	}


}
