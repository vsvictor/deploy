package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;


@Degenerate public class Remove { @Hide private Remove() {}


	public static void remove(

		@NotNull final Distribution distribution,

		@NotNull final String packageName

	) throws NotExists {

		if (!distribution.getPackagesDirectory().contains(packageName)) throw new NotExists("packageName: " + packageName);

		distribution.getPackagesDirectory().getKeys().forEach(dependentPackageName -> {

			if (Package.load(distribution, packageName).dependencies.contains(packageName)) {

				try {

					remove(
						distribution,
						packageName
					);

				} catch (NotExists notExists) { throw new InconsistentData(notExists); }

			}

		});

		distribution.getPackagesDirectory().set(packageName, null);
		distribution.getPackageSrcDirectory(packageName).remove();
		distribution.getJarDirectory().set(packageName, null);

		if (distribution.getBinDirectory() != null) distribution.getBinDirectory().set(packageName, null);

	}


}
