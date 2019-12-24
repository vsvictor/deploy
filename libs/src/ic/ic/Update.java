package ic;


import ic.apps.ic.UiCallback;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.throwables.Conflict;
import ic.network.http.HttpClient;
import ic.network.http.HttpException;
import ic.storage.Directory;
import ic.stream.ByteSequence;
import ic.struct.set.CountableSet;
import ic.struct.set.EditableSet;
import ic.throwables.*;

import static ic.AuthKt.getAuth;
import static ic.Build.build;
import static ic.Store.byName;
import static ic.TextResources.getResString;
import static ic.AddPackage.addPackage;
import static ic.ClonePackage.clonePackage;
import static ic.UpdateProject.updateProject;
import static ic.git.Git.*;


@Degenerate public class Update { @Hide private Update() {}


	private static void updatePackage(

		@NotNull final Distribution distribution,

		@NotNull final String packageName,

		@NotNull final CountableSet<String> alreadyDownloadedPackages,

		@NotNull final EditableSet<String> updatedPackages,

		@NotNull final UiCallback uiCallback

	) {

		if (updatedPackages.contains(packageName)) return;

		if (!alreadyDownloadedPackages.contains(packageName)) {

			try { final Directory packageSrcDir = distribution.getPackageSrcDirectoryOrThrow(packageName);

				uiCallback.onPackageUpdateStarted(packageName);

				final Store store; try {
					store = Store.load(distribution, packageName);
				} catch (NotExists notExists) { throw new InconsistentData(notExists); }

				final Pair<String, String> remoteAuth = getAuth(store.getUrl(), "read", uiCallback);

				final String repositoryUrl = store.getUrl() + "/" + packageName;
				try {
					pull(
						packageSrcDir,
						repositoryUrl,
						remoteAuth,
						uiCallback::handleWarning
					);
				} catch (NotExists notExists) {
					throw new Fatal.Runtime(getResString("ic/repository-not-exists").replace("$(url)", repositoryUrl));
				} catch (Conflict conflict) {
					uiCallback.handleWarning(conflict.getMessage());
				} catch (NotNeeded notNeeded) {}

			} catch (NotExists packageSrcDirNotExists) {

				if (distribution.getPackagesDirectory().contains(packageName)) {

					uiCallback.onPackageUpdateStarted(packageName);

					final PackageInstance packageInstance = distribution.getPackagesDirectory().getNotNull(packageName);

					final Store store; try {
						store = byName(distribution, packageInstance.getStoreName());
					} catch (NotExists notExists) { throw new InconsistentData(notExists); }

					try {
						clonePackage(distribution, packageName, store, uiCallback);
					} catch (NotExists notExists) { throw new Fatal.Runtime("Can't find package " + packageName + " in " + store.name); }

				} else {

					try {
						addPackage(distribution, packageName, uiCallback);
					} catch (AlreadyExists alreadyExists) 	{ throw new InconsistentData(alreadyExists);
					} catch (NotExists notExists) 			{ throw new Fatal.Runtime("Can't find package " + packageName + " in stores"); }

				}

			}

		}

		updatedPackages.add(packageName);

		final Package packageObject = Package.load(distribution, packageName);

		packageObject.stores.forEach(store -> {
			try {
				Store.addStore(distribution, store.name, store.url, uiCallback);
			} catch (AlreadyExists alreadyExists) {
			}
		});


		packageObject.dependencies.forEach(dependency -> {

			if (distribution.getPackagesDirectory().contains(dependency)) return;

			updatePackage(
				distribution,
				dependency,
				alreadyDownloadedPackages,
				updatedPackages,
				uiCallback
			);

		});

	}


	public static void update(

		@NotNull final Distribution distribution,

		@NotNull final CountableSet<String> packagesToUpdate,
		@NotNull final CountableSet<String> alreadyDownloadedPackages,

		@NotNull final UiCallback uiCallback

	) throws Fatal {

		final EditableSet<String> updatedPackages = new EditableSet.Default<>(); // Including added packages

		packagesToUpdate.forEach(packageName -> updatePackage(
			distribution,
			packageName,
			alreadyDownloadedPackages,
			updatedPackages,
			uiCallback
		));

		if (!distribution.getPackagesDirectory().contains("kotlin")) {
			updatePackage(
				distribution,
				"kotlin",
				alreadyDownloadedPackages,
				updatedPackages,
				uiCallback
			);
		}


		updatedPackages.forEach(packageName -> {

			{ final Directory packageJarDir = distribution.getJarDirectory().createFolderIfNotExists(packageName);

				Package.load(distribution, packageName).jarDependencies.forEach(jarDependency -> {

					if (packageJarDir.getKeys().contains(jarDependency)) return;

					final ByteSequence jar; try {
						jar = HttpClient.request(jarDependency).getBody();
					} catch (IOException | HttpException e) { throw new Fatal.Runtime(e); }

					packageJarDir.write(jarDependency, jar);

				});

			}

		});

		if (distribution.buildOnUpdate) {
			build(
				distribution,
				updatedPackages,
				uiCallback
			);
		}

		updateProject(distribution);

	}


}
