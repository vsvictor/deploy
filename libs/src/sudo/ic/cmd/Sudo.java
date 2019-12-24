package ic.cmd;


import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.interfaces.getter.Getter;
import ic.struct.collection.Collection;
import ic.text.ReplaceText;
import ic.text.Text;
import ic.throwables.End;
import ic.throwables.NotSupported;

import static ic.system.bash.BashKt.singleQuote;


public class Sudo extends Console.BaseConsole {


	private final Console sourceConsole;

	private final Getter<String> passwordGetter;

	private Boolean root;

	private String password;


	@Override public char getChar() throws End 	{ throw new NotSupported.Runtime(); }
	@Override public void put(char c) 			{ throw new NotSupported.Runtime(); }

	@Override public void close() { sourceConsole.close(); }


	@Override public synchronized void writeLine(@NotNull String command) {
		if (root == null) {
			sourceConsole.writeLine("echo $USER");
			root = sourceConsole.read().equals("root");
		}
		if (root) {
			sourceConsole.writeLine(command);
		} else {
			if (password == null) {
				password = passwordGetter.getNotNull();
			}
			if (command.startsWith("cd ") || command.equals("cd")) {
				sourceConsole.writeLine(command);
			} else {
				sourceConsole.writeLine(
					"echo '" + password + "' | sudo -S -p '$(passwordPrompt)' bash -c " + singleQuote(command)
				);
			}
		}
	}

	@Override public void writeLine(Text text) {
		writeLine(text.toString());
	}


	@Override public Text read() {
		return new ReplaceText(
			sourceConsole.read(),
			new Collection.Default<>(
				new Pair<>("$(passwordPrompt)", "")
			)
		);
	}


	public Sudo(@NotNull Console sourceConsole, @NotNull Getter<String> passwordGetter) {
		this.sourceConsole = sourceConsole;
		this.passwordGetter = passwordGetter;
	}


	public Sudo(@NotNull Console sourceConsole) {
		this(
			sourceConsole,
			() -> {
				final UserConsole userConsole = new UserConsole();
				userConsole.write("Enter your sudo password: ");
				return userConsole.readLine().toString();
			}
		);
	}


}
