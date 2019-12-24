package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.apps.ic.UiCallback;
import ic.git.Git;
import ic.throwables.AlreadyExists;
import ic.throwables.Fatal;
import ic.throwables.NotExists;


public @Degenerate class Branch { @Hide private Branch() {}


	public static void branch(

		@NotNull final Distribution distribution,
		@NotNull final String packageName,
		@NotNull final String branch,
		@NotNull final UiCallback uiCallback

	) {

		assert distribution != null;
		assert packageName 	!= null;
		assert branch 		!= null;
		assert uiCallback 	!= null;

		final PackageInstance packageInstance; try {
			packageInstance = PackageInstance.byName(distribution, packageName);
		} catch (NotExists notExists) {
			throw new Fatal.Runtime("No such package: " + packageName);
		}

		if (!distribution.getPackagesDirectory().contains(packageName))

		try {

			Git.branch(
				distribution.getPackageSrcDirectory(packageName),
				branch,
				uiCallback::handleWarning
			);

		} catch (AlreadyExists alreadyExists) { throw new Fatal.Runtime("Branch already exists: " + branch + ", package: " + packageName); }

		packageInstance.setVersion(branch);

		packageInstance.save(distribution, packageName);

	}


}
