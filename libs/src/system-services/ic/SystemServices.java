package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Overload;
import ic.cmd.SystemConsole;
import ic.storage.Directory;
import ic.struct.sequence.Series;
import ic.text.CharInput;
import ic.text.Charset;
import ic.text.SplitInput;
import ic.text.Text;
import ic.throwables.*;

import static ic.parallel.SleepKt.sleep;
import static ic.system.bash.BashKt.scriptToCommand;
import static ic.system.bash.NohupKt.nohup;
import static ic.throwables.AccessDenied.ACCESS_DENIED;
import static ic.throwables.WrongState.WRONG_STATE;


public @Degenerate class SystemServices { @Hide private SystemServices() {}


	private static Directory getPidsDirectory(@NotNull String userName) {
		return Directory.getExisting("/tmp").createFolderIfNotExists("service-pids-" + userName);
	}


	static void startService(@NotNull String userName, @NotNull String serviceName, @NotNull String command) throws AccessDenied, WrongState {

		if (!userName.equals("root")) {
			if (!userName.equals(System.getProperty("user.name"))) throw ACCESS_DENIED;
		}

		final SystemConsole systemConsole = new SystemConsole();

		final Directory pidsDirectory = getPidsDirectory(userName);

		final Directory logsDir; {
			if (userName.equals("root")) {
				logsDir = Directory.createIfNotExists("/root/.ic/logs/" + Charset.DEFAULT_UNIX.encodeUrl(serviceName));
			} else {
				logsDir = Directory.createIfNotExists("/home/" + userName + "/.ic/logs/" + Charset.DEFAULT_UNIX.encodeUrl(serviceName));
				systemConsole.writeLine("chown -R " + userName + " /home/" + userName + "/.ic");
				final Text response = systemConsole.read();
				if (!response.isEmpty()) throw new RuntimeException(response.toString());
			}
		}

		if (pidsDirectory.contains(serviceName)) throw WRONG_STATE;

		systemConsole.writeLine("sudo -u " + userName + " " + scriptToCommand(
			new Text.FromString(
				nohup(scriptToCommand(new Text.FromString(
					"echo $$ > " + pidsDirectory.absolutePath + "/" + serviceName + "; while true; do " + scriptToCommand(new Text.FromString(
						command + " &> " + logsDir.absolutePath + "/sysout-$(date +\"%Y-%m-%d-%H-%M-%S\")"
					) ) + "; done"
				) ))
			)
		));

		{
			sleep(1024);
			assert pidsDirectory.contains(serviceName);
		}

	}

	@Overload public static void startService(@NotNull String serviceName, @NotNull String command) throws WrongState {
		try {
			startService(
				System.getProperty("user.name"),
				serviceName,
				command
			);
		} catch (AccessDenied accessDenied) { throw new AccessDenied.Error(accessDenied); }
	}


	static void stopService(@NotNull String userName, @NotNull String serviceName) throws AccessDenied, WrongState {

		if (!userName.equals("root")) {
			if (!userName.equals(System.getProperty("user.name"))) throw ACCESS_DENIED;
		}

		final Directory pidsDirectory = getPidsDirectory(userName);

		final String pid; try {
			pid = Charset.DEFAULT_UNIX.bytesToString(pidsDirectory.readOrThrow(serviceName)).trim();
		} catch (NotExists notExists) { throw WRONG_STATE; }

		final SystemConsole systemConsole = new SystemConsole();

		systemConsole.writeLine("sudo -u " + userName + " ps -A -opid,pgid");

		final Text response = systemConsole.read();

		final CharInput responseIterator = response.getIterator();

		try {
			responseIterator.safeReadTill('\n');
		} catch (NotExists notExists) { throw new RuntimeException("RESPONSE: \n" + response); }

		String pgid = null;

		try {
			final Series<Text> responseRowsIterator = new SplitInput(responseIterator, '\n');
			while (true) { final Text responseRow = responseRowsIterator.getNotNull();
				if (responseRow.isEmpty()) continue;
				final Series<Text> responseRowIterator = new SplitInput(responseRow.getIterator(), ' ') {
					@Override protected boolean toSkipEmpty() { return true; }
				};
				final String rowPid, rowPgid; try {
					rowPid 	= responseRowIterator.getNotNull().getStringValue();
					rowPgid = responseRowIterator.getNotNull().getStringValue();
				} catch (End end) { throw new RuntimeException("RESPONSE: \n" + response); }
				if (pgid == null) {
					if (rowPid.equals(pid)) {
						pgid = rowPgid;
					}
				}
				if (pgid != null) {
					if (rowPgid.equals(pgid)) {
						systemConsole.writeLine("kill -TERM " + rowPid);
					}
				}
			}
		} catch (End end) {}

		pidsDirectory.remove(serviceName);

	}

	@Overload public static void stopService(@NotNull String serviceName) throws WrongState {
		try {
			stopService(
				System.getProperty("user.name"),
				serviceName
			);
		} catch (AccessDenied accessDenied) { throw new AccessDenied.Error(accessDenied); }
	}


}
