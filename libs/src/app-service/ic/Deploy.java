package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.interfaces.condition.Condition;
import ic.network.icotp.IcotpMode;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.apps.ic.ConsoleUiCallback;
import ic.apps.ic.UiCallback;
import ic.cmd.Console;
import ic.cmd.Sudo;
import ic.cmd.UserConsole;
import ic.cmd.ssh.SshSession;
import ic.git.Git;
import ic.network.SocketAddress;
import ic.network.icotp.IcotpClient;
import ic.network.icotp.Ping;
import ic.network.icotp.Pong;
import ic.service.monitoring.monitor.MonitorService;
import ic.service.monitoring.Monitoring;
import ic.storage.Storage;
import ic.struct.map.UntypedCountableMap;
import ic.struct.set.CountableSet;
import ic.system.onboot.OnBoot;
import ic.throwables.*;

import static ic.AppKt.getApp;
import static ic.AuthKt.getAuth;
import static ic.DataStorageKt.getDataStorage;
import static ic.ExportKt.*;
import static ic.cmd.UserConsole.USER_CONSOLE;
import static ic.parallel.SleepKt.sleepTill;
import static ic.system.bash.BashKt.singleQuote;


@Degenerate public class Deploy { @Hide private Deploy() {}


	private static void deployOnRemote(

		@NotNull UserConsole console,
		@NotNull String host,
		@NotNull final String serviceName,
		@NotNull final String gitUrl,
		@NotNull final String tier,
		@Nullable final Distribution.ProjectType projectType,
		@NotNull final String args,
		@NotNull final UiCallback uiCallback

	) throws Fatal {

		final Pair<String, String> sshAuth = getAuth(host, "ssh", uiCallback);

		console.writeLine("Connecting to " + host + "...");

		final Console sshSession = new Sudo(new SshSession(host, sshAuth), () -> sshAuth.getSecond());

		sshSession.writeLine("apt install git sshpass");

		sshSession.writeLine(OnBoot.getInstallCommand());

		sshSession.writeLine("mkdir /deploy");
		sshSession.writeLine("cd /deploy");

		sshSession.writeLine("rm -rf " + serviceName);

		try {
			Git.clone(
				sshSession,
				gitUrl, serviceName, getAuth(gitUrl, "write", uiCallback),
				uiCallback::handleWarning
			);
		} catch (NotExists notExists) 			{ throw new NotExists.Runtime(notExists);
		} catch (AlreadyExists alreadyExists) 	{ throw new AlreadyExists.Runtime(alreadyExists);
		} catch (AccessDenied accessDenied) 	{ throw new AccessDenied.Runtime(accessDenied); 	}

		sshSession.writeLine("cd " + serviceName);

		try {
			Git.checkout(
				sshSession,
				tier,
				uiCallback::handleWarning
			);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists);
		} catch (NotNeeded notNeeded) {}

		// TODO Run only once
		sshSession.writeLine(getExportedScriptsPath(projectType) + "/prepare-system");

		sshSession.writeLine(
			getExportedLaunchPath() + "/" + getApp().getPackageName() + " " + args + " " + sshAuth.getFirst() + ":" + sshAuth.getSecond()
		);

		console.writeLine(sshSession.read());

		sshSession.writeLine(
			"echo " + singleQuote(
				"cd /deploy/" + serviceName + ";" +
				getExportedLaunchPath() + "/" + getApp().getPackageName() + " " + args + " " + sshAuth.getFirst() + ":" + sshAuth.getSecond()
			) + " > /onboot/" + serviceName
		);

		sshSession.writeLine(
			"chmod +x /onboot/" + serviceName
		);

		sshSession.close();

	}


	public static void deploy(

		@Nullable final Distribution.ProjectType projectType,
		@NotNull final String tier,
		@NotNull final String host,
		@Nullable final MonitorService monitorService

	) throws Fatal {

		final UserConsole userConsole = USER_CONSOLE;

		final UiCallback uiCallback = new ConsoleUiCallback(userConsole);

		final String appPackageName = getApp().getPackageName();

		final Storage dataStorage = getDataStorage().createFolderIfNotExists("deploy");

		final String gitUrl = dataStorage.createIfNull("git-url", () -> {

			userConsole.writeLine("Enter git repository url to export " + appPackageName);

			return userConsole.readLine().toString().trim();

		});

		export(
			Distribution.get(),
			gitUrl,
			tier,
			projectType,
			new UntypedCountableMap.Default<>(
				"tier", tier
			),
			new CountableSet.Default<>(appPackageName),
			uiCallback
		);

		if (monitorService != null) {

			final String monitorHost = monitorService.getHost(); {
				assert monitorHost != null;
			}

			deployOnRemote(userConsole, monitorHost, "service-monitor", gitUrl, tier, projectType, "monitor restart", uiCallback);

			sleepTill(() -> {

				try {
					final Pong pong = IcotpClient.request(
						new SocketAddress(
							monitorHost, Monitoring.PORT_MONITOR
						),
						IcotpMode.PLAIN,
						new Ping()
					);
					return true;
				} catch (Throwable throwable) {
					return false;
				}

			});

		}

		deployOnRemote(userConsole, host, tier + "." + appPackageName, gitUrl, tier, projectType, "restart", uiCallback);

	}


}
