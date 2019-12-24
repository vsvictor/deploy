package ic.service.commands;


import ic.Distribution;
import ic.cmd.Command;
import ic.cmd.Console;
import ic.service.monitoring.monitor.MonitorService;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.Fatal;
import ic.throwables.InvalidSyntax;
import ic.throwables.NotSupported;

import static ic.AppKt.getApp;
import static ic.DataStorageKt.getDataStorage;
import static ic.Deploy.deploy;


public abstract class DeployCommand extends Command.BaseCommand {


	@Override public String getName() { return "deploy"; }

	@Override public String getSyntax() { return "deploy <tier>"; }

	@Override public String getDescription() { return "Deploy to remote machine"; }


	protected abstract MonitorService initMonitorService();


	@Override protected void implementRun(final Console console, Text args) throws InvalidSyntax, Fatal {

		final CharInput argsIterator = args.getIterator();

		final Distribution.ProjectType projectType; {
			final String projectTypeStringFromStorage = getDataStorage().get("deploy-project-type");
			final String projectTypeString; {
				if (projectTypeStringFromStorage == null) {
					console.write("Enter exported project type to deploy " + getApp().getPackageName() + ": ");
					projectTypeString = console.readLine().toString();
				} else {
					projectTypeString = projectTypeStringFromStorage;
				}
			}
			if (projectTypeString.equals("default")) 	projectType = null; 							else
			if (projectTypeString.equals("gradle"))		projectType = Distribution.ProjectType.GRADLE;
			else throw new NotSupported.Runtime("projectType: \"" + projectTypeString + "\"");
			if (projectTypeStringFromStorage == null) {
				getDataStorage().set("deploy-project-type", projectTypeString);
			}
		}

		final String tier = argsIterator.readTill(' ').toString();

		final String host = getDataStorage().createFolderIfNotExists("deploy-hosts").createIfNull(
			tier,
			() -> {
				console.write("Enter service host for " + getApp().getPackageName() + "." + tier + ": ");
				return console.readLine().toString();
			}
		);

		final MonitorService monitorService = initMonitorService();

		deploy(projectType, tier, host, monitorService);

	}

}