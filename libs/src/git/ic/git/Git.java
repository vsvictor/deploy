package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.annotations.Redirect;
import ic.cmd.Console;
import ic.interfaces.action.Action1;
import ic.cmd.SystemConsole;
import ic.interfaces.getter.Getter1;
import ic.storage.Directory;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.*;
import kotlin.Function;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotNeeded.NOT_NEEDED;


public @Degenerate class Git { @Hide private Git() {}


	// Clone:

	public static @Redirect void clone(
		@NotNull Console console,
		@NotNull String url,
		@NotNull String destinationPath,
		@Nullable Pair<String, String> credentials,
		@NotNull Action1<String> warningHandler
	) throws NotExists, AlreadyExists, AccessDenied { Clone.clone(console, url, destinationPath, credentials, warningHandler); }

	public static @Redirect Directory clone(
		@NotNull String url,
		@NotNull Directory baseDir,
		@NotNull String destinationPath,
		@Nullable Pair<String, String> credentials,
		@NotNull Action1<String> warningHandler
	) throws NotExists, AlreadyExists, AccessDenied { return Clone.clone(url, baseDir, destinationPath, credentials, warningHandler); }


	// Pull:

	public static @Redirect void pull(
		@NotNull Console console,
		@NotNull Getter1<String, String> commandModifier,
		@NotNull Getter1<Text, Text> responseModifier,
		@NotNull String url,
		@NotNull Pair<String, String> auth,
		@NotNull Action1<String> warningHandler
	) throws NotExists, NotNeeded, Conflict { Pull.pull(console, commandModifier, responseModifier, url, auth, warningHandler); }

	public static @Redirect void pull(
		@NotNull Directory repositoryDir,
		@NotNull String url,
		@NotNull Pair<String, String> auth,
		@NotNull Action1<String> warningHandler
	) throws NotExists, NotNeeded, Conflict { Pull.pull(repositoryDir, url, auth, warningHandler); }


	// Get current branch:

	public static @Redirect String getCurrentBranch(
		@NotNull Console console
	) { return GetCurrentBranch.getCurrentBranch(console); }

	public static @Redirect String getCurrentBranch(
		@NotNull Directory repositoryDir
	) { return GetCurrentBranch.getCurrentBranch(repositoryDir); }


	// Checkout:

	public static void checkout(
		@NotNull Console console,
		@NotNull String version, @NotNull Action1<String> warningHandler
	) throws NotExists, NotNeeded { Checkout.checkout(console, version, warningHandler); }

	public static void checkout(
		@NotNull Directory repositoryDir,
		@NotNull String version,
		@NotNull Action1<String> warningHandler
	) throws NotExists, NotNeeded { Checkout.checkout(repositoryDir, version, warningHandler); }


	// Add all:

	public static @Redirect void addAll(@NotNull Console console) 			{ AddAll.addAll(console); 		}
	public static @Redirect void addAll(@NotNull Directory repositoryDir) 	{ AddAll.addAll(repositoryDir); }


	// Commit:

	public static @Redirect void commit(@NotNull Console console, @NotNull String message) throws NotNeeded 		{ Commit.commit(console, message); 			}
	public static @Redirect void commit(@NotNull Directory repositoryDir, @NotNull String message) throws NotNeeded { Commit.commit(repositoryDir, message); 	}


	public static void push(
		@NotNull Directory repositoryDir,
		@NotNull String url, @NotNull Pair<String, String> auth,
		@NotNull Function1<String, Unit> warningHandler
	) throws NotNeeded, Rejected {
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("LANG=en_US git config --global core.sshCommand 'ssh -o StrictHostKeyChecking=no'");
		final String currentBranch = getCurrentBranch(repositoryDir);
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		{
			systemConsole.writeLine("LANG=en_US git remote remove ic");
			if (url.startsWith("https://")) {
				systemConsole.writeLine("LANG=en_US git remote add ic https://" + auth.getFirst() + ":" + auth.getSecond() + "@" + url.substring("https://".length()));
			} else {
				systemConsole.writeLine("LANG=en_US git remote add ic " + auth.getFirst() + "@" + url);
			}
		} try {
			if (url.startsWith("https://")) {
				systemConsole.writeLine("LANG=en_US git push ic " + currentBranch);
			} else {
				if (auth.getSecond() == null) {
					systemConsole.writeLine("LANG=en_US git push ic " + currentBranch);
				} else {
					systemConsole.writeLine("LANG=en_US sshpass -p '" + auth.getSecond() + "' git push ic " + currentBranch);
				}
			}
			final Text response = systemConsole.read();
			for (Text responseRow : new SplitText(response, '\n')) {
				if (responseRow.isEmpty()) continue;
				if (responseRow.startsWith("Warning: ") || responseRow.startsWith("warning: ")) {
					warningHandler.invoke(responseRow.toString());
					continue;
				}
				if (responseRow.equals("To " + url.trim())) continue;
				if (responseRow.startsWith(" ! [rejected] ")) throw new Rejected(response.toString());
				if (responseRow.startsWith(" * [new branch]      ")) continue;
				if (responseRow.startsWith("warning: redirecting to ")) continue;
				if (responseRow.contains("..") && responseRow.contains("->")) continue;
				if (responseRow.equals("Everything up-to-date")) throw NOT_NEEDED;
				if (responseRow.startsWith("remote: ")) {
					warningHandler.invoke(responseRow.toString());
					continue;
				}
				else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
			}
		} finally {
			systemConsole.writeLine("LANG=en_US git remote remove ic");
		}
	}



	public static void branch(@NotNull Directory repositoryDir, @NotNull String branch, @NotNull Action1<String> warningHandler) throws AlreadyExists {
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		systemConsole.writeLine("LANG=en_US git checkout -b " + branch);
		final Text response = systemConsole.read();
		for (Text responseRow : new SplitText(response, '\n')) {
			if (responseRow.isEmpty()) continue;
			if (responseRow.startsWith("Warning: ") || responseRow.startsWith("warning: ")) {
				warningHandler.run(responseRow.toString());
				continue;
			}
			if (responseRow.startsWith("D       ")) continue;
			if (responseRow.startsWith("Switched to a new branch '") && responseRow.endsWith("'")) continue;
			if (responseRow.startsWith("fatal: A branch named '") && responseRow.endsWith("' already exists.")) throw ALREADY_EXISTS;
			else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
		}
	}

}
