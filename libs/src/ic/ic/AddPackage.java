package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.apps.ic.UiCallback;
import ic.storage.Directory;
import ic.struct.list.ReverseList;
import ic.throwables.AlreadyExists;
import ic.throwables.NotExists;
import org.jetbrains.annotations.NotNull;

import static ic.ClonePackage.clonePackage;
import static ic.Store.getStores;
import static ic.git.Git.getCurrentBranch;
import static ic.throwables.NotExists.NOT_EXISTS;


@Degenerate class AddPackage { @Hide private AddPackage() {}


	static void addPackage(

		@NotNull final Distribution distribution,
		@NotNull final String packageName,
		@NotNull final UiCallback uiCallback

	) throws AlreadyExists, NotExists {

		if (distribution.getPackageNames().contains(packageName)) throw new AlreadyExists("packageName: " + packageName);

		uiCallback.onPackageAddStarted(packageName);

		final PackageInstance packageInstance; {

			PackageInstance value;

			searchingInStores: {

				for (Store store : new ReverseList<>(getStores(distribution))) {

					try {

						final Directory packageDir = clonePackage(distribution, packageName, store, uiCallback);

						value = new PackageInstance(
							store.name,
							getCurrentBranch(packageDir)
						);

						break searchingInStores;

					} catch (NotExists notExists) {}

				} throw NOT_EXISTS;

			}

			packageInstance = value;

		}

		distribution.getPackagesDirectory().set(
			packageName,
			packageInstance
		);

	}


}
