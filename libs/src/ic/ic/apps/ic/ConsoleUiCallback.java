package ic.apps.ic;


import ic.throwables.Fatal;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Console;
import ic.struct.collection.Collection;
import ic.text.ReplaceText;
import ic.text.Text;

import static ic.TextResources.getResText;


public class ConsoleUiCallback extends UiCallback {


	private final Console console;


	@Override public void onPackageAddStarted(String packageName) {
		console.writeLine("Package adding: " + packageName + " ...");
	}

	@Override public void onPackageUpdateStarted(String packageName) {
		console.writeLine("Package updating: " + packageName + " ...");
	}

	@Override public void onPackageBuildStarted(String packageName) {
		console.writeLine("Package building: " + packageName + " ...");
	}

	@Override public void onPackageUploadStarted(String packageName) {
		console.writeLine("Package uploading: " + packageName + " ...");
	}

	@Override public void onPackageTestStarted(String packageName) {
		console.writeLine("Package testing: " + packageName + " ...");
	}

	@Override public void onPackageTestResult(String packageName, Text testResult) {
		console.write(testResult);
	}

	@Override public void onExportCloneStarted(String url) {
		console.writeLine(new ReplaceText(
			getResText("ic/on-export-clone-started"),
			new Collection.Default<>(
				new Pair<>("$(url)", url)
			)
		));
	}


	@Override public void handleWarning(String warning) {
		if (warning.startsWith("warning: redirecting to")) return;
		console.writeLine(warning);
	}


	@Override public Pair<String, String> askAuth(@NotNull String destination, @NotNull Pair<String, String> existingAuth) {

		final String userName; {
			if (existingAuth.getFirst() == null) {
				console.writeLine("Enter username for " + destination + ":");
				Text userNameFromConsole = console.readLine();
				if (userNameFromConsole.isEmpty()) {
					throw new Fatal.Runtime("Username can't be empty");
				} else {
					userName = userNameFromConsole.toString();
				}
			} else {
				userName = existingAuth.getFirst();
			}
		}

		final String password; {
			if (existingAuth.getSecond() == null) {
				console.writeLine("Enter password for " + userName + " at " + destination + ":");
				final Text passwordFromConsole = console.readLine();
				if (passwordFromConsole.isEmpty()) {
					password = null;
				} else {
					password = passwordFromConsole.toString();
				}
			} else {
				password = existingAuth.getSecond();
			}
		}

		return new Pair<>(userName, password);

	}


	@Override public String askCommitMessage() {
		console.writeLine("Enter commit message:");
		return console.readLine().toString();
	}


	public ConsoleUiCallback(@NotNull Console console) {
		this.console = console;
	}


}
