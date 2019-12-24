package ic;


import ic.apps.ic.UiCallback;
import ic.cmd.SystemConsole;
import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.struct.map.EditableMap;
import org.jetbrains.annotations.NotNull;
import ic.javac.*;
import ic.storage.Directory;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.struct.list.EditableList;
import ic.struct.map.CountableMap;
import ic.struct.set.CountableSet;
import ic.struct.set.EditableSet;
import ic.struct.set.UnionCountableSet;
import ic.text.Text;
import ic.throwables.*;

import static ic.BuildPackageKt.buildPackage;
import static ic.GetFullDependenciesKt.getFullDependencies;


@Degenerate public class Build { @Hide private Build() {}

	public static void build(

		@NotNull final Distribution distribution,
		@NotNull final CountableSet<String> packagesToBuild,
		@NotNull final UiCallback uiCallback

	) throws Fatal {

		final Directory binDirectory		= distribution.getBinDirectory();
		final Directory tmpDirectory 		= distribution.getTmpDirectory();
		final Directory tmpBinDirectory 	= tmpDirectory.createFolderIfNotExists("bin");
		final Directory tmpTestDirectory 	= tmpDirectory.createFolderIfNotExists("test");

		final EditableSet<String> builtPackages = new EditableSet.Default<>();

		packagesToBuild.forEach(packageName -> buildPackage(
			distribution,
			tmpBinDirectory,
			packageName,
			packagesToBuild,
			builtPackages,
			uiCallback
		));

		final LaunchScriptGenerator launchScriptGenerator = new LaunchScriptGenerator() {
			@Override protected String directoryToPath(Directory directory) {
				String path = directory.absolutePath;
				if (distribution.portable) {
					path = distribution.rootDirectory.relativizePath(path);
				}
				return path;
			}
		};

		builtPackages.forEach(packageName -> {

			final Package packageObject = Package.load(distribution, packageName);

			if (packageObject.testClassName != null) {

				final Collection<String> allRequiredPackages = new UnionCountableSet<>(
					new CountableSet.Default<>(
						getFullDependencies(distribution, packageName)
					),
					new CountableSet.Default<>(packageName),
					new CountableSet.Default<>("kotlin")
				);

				launchScriptGenerator.generateLaunchScript(
					tmpTestDirectory,
					packageName,
					new ConvertCollection<>(
						allRequiredPackages,
						dependency -> {
							if (builtPackages.contains(dependency)) {
								return tmpBinDirectory.getFolder(dependency);
							} else {
								return binDirectory.getFolder(dependency);
							}
						}
					),
					new JoinCollection<>(
						new ConvertCollection<>(
							allRequiredPackages,
							dependency -> {
								final EditableList<Directory> jarDirectories = new EditableList.Default<>();
								jarDirectories.add(distribution.getJarDirectory().getFolder(dependency));
								try {
									jarDirectories.add(distribution.getPackageSrcDirectory(dependency).getFolderOrThrow("jar"));
								} catch (NotExists ignored) {}
								return jarDirectories;
							}
						)
					),
					"ic.test.Test",
					new Text.FromString(packageName),
					new CountableMap.Default<>(
						"IC_DISTRIBUTION_PATH", distribution.rootDirectory.absolutePath
					)
				);

				{ uiCallback.onPackageTestStarted(packageName);

					final SystemConsole systemConsole = new SystemConsole();
					systemConsole.writeLine("cd " + distribution.rootDirectory.absolutePath);
					systemConsole.writeLine(tmpTestDirectory.absolutePath + "/" + packageName);
					final Text testResult = systemConsole.read();
					uiCallback.onPackageTestResult(packageName, testResult);

				}

			}

			final Directory tmpPackageDir = tmpBinDirectory.getFolder(packageName);

			distribution.getBinDirectory().set(packageName, tmpPackageDir);

		});

		tmpBinDirectory.remove();
		tmpTestDirectory.remove();

		final EditableMap<String, String> envs = new EditableMap.Default<>(); {
			if (!distribution.portable) {
				envs.set("IC_DISTRIBUTION_PATH", distribution.rootDirectory.absolutePath);
			}
		}

		builtPackages.forEach(packageName -> {

			final Package packageObject = Package.load(distribution, packageName);

			if (packageObject.main != null) {

				final Collection<String> allRequiredPackages = new UnionCountableSet<>(
					new CountableSet.Default<>(
						getFullDependencies(distribution, packageName)
					),
					new CountableSet.Default<>(packageName),
					new CountableSet.Default<>("kotlin")
				);

				launchScriptGenerator.generateLaunchScript(
					distribution.getLaunchDirectory(),
					packageName,
					new ConvertCollection<>(
						allRequiredPackages,
						requiredPackageName -> distribution.getBinDirectory().getNotNull(requiredPackageName)
					),
					new JoinCollection<>(
						new ConvertCollection<>(
							allRequiredPackages,
							requiredPackageName -> {
								final EditableList<Directory> jarDirectories = new EditableList.Default<>();
								jarDirectories.add(distribution.getJarDirectory().getFolder(requiredPackageName));
								try {
									jarDirectories.add(distribution.getPackageSrcDirectory(requiredPackageName).getFolderOrThrow("jar"));
								} catch (NotExists ignored) {}
								return jarDirectories;
							}
						)
					),
					"ic.App",
					new Text.FromString(packageName),
					envs
				);

			}

		});

	}


}
