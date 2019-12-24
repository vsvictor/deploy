package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.apps.ic.UiCallback;
import ic.git.Git;
import ic.throwables.Fatal;
import ic.throwables.NotExists;
import ic.throwables.NotNeeded;


public @Degenerate class SwitchVersion { @Hide private SwitchVersion() {}


	public static void switchVersion(
		@NotNull final Distribution distribution,
		@NotNull final String packageName,
		@NotNull final String version,
		@NotNull final UiCallback uiCallback
	) throws NotNeeded {

		assert distribution	!= null;
		assert packageName 	!= null;
		assert version 		!= null;
		assert uiCallback 	!= null;

		final PackageInstance packageInstance; try {
			packageInstance = PackageInstance.byName(distribution, packageName);
		} catch (NotExists notExists) {
			throw new Fatal.Runtime("No such package: " + packageName);
		}

		try {

			Git.checkout(
				distribution.getPackageSrcDirectory(packageName),
				version,
				uiCallback::handleWarning
			);

		} catch (NotExists notExists) { throw new Fatal.Runtime("No such version: " + version + ", package: " + packageName); }

		packageInstance.setVersion(version);

		packageInstance.save(distribution, packageName);

	}


}
