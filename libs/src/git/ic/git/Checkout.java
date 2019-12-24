package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.interfaces.action.Action1;
import ic.interfaces.getter.Getter1;
import ic.storage.Directory;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.NotExists;
import ic.throwables.NotNeeded;

import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.NotNeeded.NOT_NEEDED;


@Degenerate class Checkout { @Hide private Checkout() {}


	public static void checkout(
		@NotNull Console console,
		@NotNull String version, @NotNull Function1<String, Unit> warningHandler
	) throws NotExists, NotNeeded {
		console.writeLine("LANG=en_US git checkout " + version);
		final Text response = console.read();
		for (Text responseRow : new SplitText(response, '\n')) {
			if (responseRow.isEmpty()) continue;
			if (responseRow.startsWith("Warning: ") || responseRow.startsWith("warning: ")) {
				warningHandler.invoke(responseRow.toString());
				continue;
			}
			if (responseRow.startsWith("D       ")) continue;
			if (responseRow.startsWith("Switched to branch '") && responseRow.endsWith("'")) continue;
			if (responseRow.startsWith("Switched to a new branch '") && responseRow.endsWith("'")) continue;
			if (responseRow.startsWith("Your branch is up to date with '") && responseRow.endsWith("'.")) continue;
			if (
				responseRow.startsWith("Branch '" + version + "' set up to track remote branch '" + version + "' from '") &&
				responseRow.endsWith("'.")
			) continue;
			if (
				responseRow.startsWith("Branch '" + version + "' set up to track remote branch '" + version + "' from '") &&
				responseRow.endsWith("'.")
			) continue;
			//if (responseRow.startsWith("error: pathspec '") && responseRow.endsWith("' did not match any file(s) known to git.")) throw NOT_EXISTS;
			if (responseRow.startsWith("Already on '")) throw NOT_NEEDED;
			//else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
		}
	}


	public static void checkout(
		@NotNull Directory repositoryDir,
		@NotNull String version,
		@NotNull Function1<String, Unit> warningHandler
	) throws NotExists, NotNeeded {
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		checkout(
			systemConsole,
			version,
			warningHandler
		);
	}


}
