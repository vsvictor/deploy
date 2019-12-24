package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.interfaces.action.Action1;
import ic.interfaces.getter.Getter1;
import ic.storage.Directory;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.Conflict;
import ic.throwables.NotExists;
import ic.throwables.NotNeeded;

import static ic.git.Git.*;
import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.NotNeeded.NOT_NEEDED;


@Degenerate class Pull { @Hide private Pull() {}


	public static void pull(

		@NotNull Console console,
		@NotNull Getter1<String, String> commandModifier,
		@NotNull Getter1<Text, Text> responseModifier,
		@NotNull String url,
		@Nullable Pair<String, String> auth,
		@NotNull Action1<String> warningHandler

	) throws NotExists, NotNeeded, Conflict {

		console.writeLine(commandModifier.get("LANG=en_US git config --global core.sshCommand 'ssh -o StrictHostKeyChecking=no'"));
		final String currentBranch = getCurrentBranch(console);

		{
			console.writeLine(commandModifier.get("LANG=en_US git remote remove ic"));
			if (url.startsWith("https://")) {
				if (auth == null) {
					console.writeLine(commandModifier.get("LANG=en_US git remote add ic " + url));
				} else {
					console.writeLine(commandModifier.get("LANG=en_US git remote add ic https://" + auth.getFirst() + ":" + auth.getSecond() + "@" + url.substring("https://".length())));
				}
			} else {
				assert auth != null;
				console.writeLine(commandModifier.get("LANG=en_US git remote add ic " + auth.getFirst() + "@" + url));
			}
		} try {
			if (url.startsWith("https://")) {
				console.writeLine(commandModifier.get("LANG=en_US git pull --allow-unrelated-histories ic " + currentBranch));
			} else {
				assert auth != null;
				if (auth.getSecond() == null) {
					console.writeLine(commandModifier.get("LANG=en_US git pull --allow-unrelated-histories ic " + currentBranch));
				} else {
					console.writeLine(commandModifier.get("LANG=en_US sshpass -p '" + auth.getSecond() + "' git pull --allow-unrelated-histories ic " + currentBranch));
				}
			}
			final Text response = responseModifier.get(console.read());
			for (Text responseRow : new SplitText(response, '\n')) {
				if (responseRow.isEmpty()) continue;
				if (responseRow.startsWith("Warning: ") || responseRow.startsWith("warning: ")) {
					warningHandler.run(responseRow.toString());
					continue;
				}
				if (responseRow.startsWith("fatal: '") && responseRow.endsWith("' does not appear to be a git repository")) throw NOT_EXISTS;
				if (responseRow.equals("Already up to date.")) throw NOT_NEEDED;
				if (responseRow.startsWith("fatal: Couldn't find remote ref ")) throw NOT_NEEDED;
				if (responseRow.startsWith("CONFLICT ")) throw new Conflict(response.toString());
				if (responseRow.startsWith("Updating ")) 													continue;
				if (responseRow.startsWith("From ")) 														continue;
				if (responseRow.equals("Fast-forward")) 													continue;
				if (responseRow.startsWith("Auto-merging "))												continue;
				if (responseRow.startsWith("Removing "))													continue;
				if (responseRow.startsWith("Merge made by the '") && responseRow.endsWith("' strategy."))	continue;
				if (responseRow.startsWith(" ") && responseRow.contains(" | ")) 							continue;
				if (responseRow.startsWith("   ") && responseRow.contains("->")) 							continue;
				if (responseRow.contains(" changed, ")) 													continue;
				if (responseRow.startsWith(" create mode ")) 												continue;
				if (responseRow.startsWith(" delete mode ")) 												continue;
				if (responseRow.startsWith(" rename ")) 													continue;
				if (responseRow.startsWith(" rewrite ")) 													continue;
				if (responseRow.startsWith(" * branch            ")) 										continue;
				if (responseRow.startsWith(" * [new branch]      ")) 										continue;
				if (responseRow.equals("error: Pulling get not possible because you have unmerged files.")) {
					addAll(console);
					commit(console, "Merge");
					pull(console, commandModifier, responseModifier, url, auth, warningHandler);
					return;
				}
				if (responseRow.equals("error: Your local changes to the following files would be overwritten by merge:")) {
					addAll(console);
					commit(console, "Merge");
					pull(console, commandModifier, responseModifier, url, auth, warningHandler);
					return;
				}
				if (responseRow.equals("error: The following untracked working tree files would be overwritten by merge:")) {
					addAll(console);
					commit(console, "Merge");
					pull(console, commandModifier, responseModifier, url, auth, warningHandler);
					return;
				}
				else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
			}
		} finally {
			console.writeLine(commandModifier.get("LANG=en_US git remote remove ic"));
		}

	}

	public static void pull(

		@NotNull Directory repositoryDir,
		@NotNull String url,
		@NotNull Pair<String, String> auth,
		@NotNull Action1<String> warningHandler

	) throws NotExists, NotNeeded, Conflict {

		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);

		pull(
			systemConsole,
			command -> command,
			response -> response,
			url, auth, warningHandler
		);

	}


}
