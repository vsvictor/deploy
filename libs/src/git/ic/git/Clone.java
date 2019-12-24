package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.interfaces.action.Action1;
import ic.storage.Directory;
import ic.text.SplitText;
import ic.text.Text;
import ic.throwables.AccessDenied;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;

import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotExists.NOT_EXISTS;


@Degenerate class Clone { @Hide private Clone() {}


	static Pair<String, Text> clone(

		@NotNull Console console,
		@NotNull String url,
		@NotNull String destinationPath,
		@Nullable Pair<String, String> auth,
		@NotNull Function1<String, Unit> warningHandler

	) throws NotExists, AlreadyExists, AccessDenied {

		console.writeLine("LANG=en_US git config --global core.sshCommand 'ssh -o StrictHostKeyChecking=no'");

		final String command; {
			if (auth == null) {
				command = "LANG=en_US git clone " + url + " " + destinationPath;
			} else {
				if (url.startsWith("https://")) {
					command = "LANG=en_US git clone https://" + auth.getFirst() + ":" + auth.getSecond() + "@" + url.substring("https://".length()) + " " + destinationPath;
				} else {
					command = "LANG=en_US sshpass -p '" + auth.getSecond() + "' git clone " + auth.getFirst() + "@" + url + " " + destinationPath;
				}
			}
		}

		console.writeLine(command);

		final Text response = console.read();
		System.out.println(response);

		for (Text responseRow : new SplitText(response, '\n')) {
			if (responseRow.isEmpty()) continue;
			if (responseRow.startsWith("Warning: ") || responseRow.startsWith("warning: ")) {
				warningHandler.invoke(responseRow.toString());
				continue;
			}
			if (responseRow.startsWith("Cloning into '") && responseRow.endsWith("'...")) 								continue; else
			if (responseRow.startsWith("remote: "))																		continue; else
			if (responseRow.startsWith("Checking out files: "))															continue; else
			if (responseRow.startsWith("fatal: '") && responseRow.endsWith("' does not appear to be a git repository")) throw NOT_EXISTS; else
			if (responseRow.startsWith("fatal: repository '") && responseRow.endsWith("' not found")) 					throw NOT_EXISTS; else
			if (responseRow.startsWith("fatal: unable to update url base from redirection:")) 							throw NOT_EXISTS; else
			if (
				responseRow.startsWith("fatal: destination path '") &&
				responseRow.endsWith("' already exists and get not an empty directory.")
			) throw ALREADY_EXISTS;
			if (responseRow.startsWith("remote: Repository ") && responseRow.endsWith(" not found")) throw NOT_EXISTS;
			else throw new RuntimeException(response + "\nROW: " + responseRow);
		}

		return new Pair<>(command, response);

	}

	static Directory clone(String url, Directory baseDir, String destinationPath, @Nullable Pair<String, String> credentials, Function1<String, Unit> warningHandler) throws NotExists, AlreadyExists, AccessDenied {

		final SystemConsole systemConsole = new SystemConsole();

		systemConsole.writeLine("cd " + baseDir.absolutePath);

		final Pair<String, Text> commandAndResponse = clone(
			systemConsole,
			url,
			destinationPath,
			credentials,
			warningHandler
		);

		try {
			return Directory.getExistingOrThrow(baseDir, destinationPath);
		} catch (NotExists notExists) { throw new RuntimeException(commandAndResponse.getFirst() + "\n" + commandAndResponse.getSecond(), notExists); }

	}


}
