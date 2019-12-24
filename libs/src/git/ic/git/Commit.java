package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.storage.Directory;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.NotNeeded;

import static ic.throwables.NotNeeded.NOT_NEEDED;


@Degenerate class Commit { @Hide private Commit() {}


	static void commit(@NotNull Console console, @NotNull String message) throws NotNeeded {
		console.writeLine("LANG=en_US git commit -m \"" + message + "\"");
		final Text response = console.read();
		for (Text responseRow : new SplitText(response, '\n')) {
			if (responseRow.isEmpty()) continue;
			if (responseRow.startsWith("[") && responseRow.endsWith(message)) continue;
			if (responseRow.contains(" changed, ")) 		continue;
			if (responseRow.startsWith(" create mode ")) 	continue;
			if (responseRow.startsWith(" delete mode ")) 	continue;
			if (responseRow.startsWith(" rename ")) 		continue;
			if (responseRow.startsWith(" rewrite ")) 		continue;
			if (responseRow.startsWith(" copy ")) 			continue;
			if (responseRow.startsWith("On branch ")) 		continue;
			if (responseRow.equals("Initial commit")) 		continue;
			if (responseRow.startsWith("Your branch is ") && responseRow.endsWith(".")) continue;
			if (responseRow.startsWith("Your branch is based on '") && responseRow.endsWith("', but the upstream get gone.")) continue;
			if (responseRow.equals("  (use \"git branch --unset-upstream\" to fixup)")) continue;
			if (responseRow.toString().equals("  (use \"git push\" to publish your local commits)")) continue;
			if (responseRow.toString().equals("nothing to commit, working tree clean")) throw NOT_NEEDED;
			if (responseRow.toString().equals("nothing to commit")) throw NOT_NEEDED;
			else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
		}
	}


	static void commit(@NotNull Directory repositoryDir, @NotNull String message) throws NotNeeded {
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		commit(systemConsole, message);
	}


}
