package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.storage.Directory;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.collection.JoinCollection;
import ic.struct.list.EditableList;
import ic.throwables.*;

import static ic.InstantCoffee.getIcJavaVersion;
import static ic.InstantCoffee.getIcKotlinVersion;
import static ic.gradle.GenerateBuildGradle.generateBuildGradle;


@Degenerate class UpdateProject { @Hide private UpdateProject() {}


	static void updateProject(

		@NotNull final Distribution distribution

	) {

		if (distribution.projectType == null) return;

		if (distribution.projectType == Distribution.ProjectType.GRADLE) {

			generateBuildGradle(
				distribution.rootDirectory,
				new ConvertCollection<>(
					distribution.getPackagesDirectory().getKeys(),
					packageName -> distribution.rootDirectory.relativizePath(
						distribution.getPackageSrcDirectory(packageName).absolutePath
					)
				),
				new Collection.Default<>(),
				new ConvertCollection<>(
					new JoinCollection<>(
						new ConvertCollection<>(
							distribution.getPackagesDirectory().getKeys(),
							packageName -> {
								final EditableList<Directory> jarDirectories = new EditableList.Default<>();
								jarDirectories.add(distribution.getJarDirectory().getFolder(packageName));
								if (!packageName.equals("kotlin")) {
									try {
										jarDirectories.add(distribution.getPackageSrcDirectory(packageName).getFolderOrThrow("jar"));
									} catch (NotExists notExists) {}
								}
								return jarDirectories;
							}
						)
					),
					jarDirectory -> distribution.rootDirectory.relativizePath(jarDirectory.absolutePath)
				),
				"ic.App",
				getIcJavaVersion(),
				getIcKotlinVersion()
			);

		} else

		if (distribution.projectType == Distribution.ProjectType.ANDROID) {

			throw new NotImplementedYet();

		}

		else throw new InvalidValue.Runtime("projectType: " + distribution.projectType);

	}


}
