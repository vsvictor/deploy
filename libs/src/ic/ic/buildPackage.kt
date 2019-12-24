package ic

import ic.InstantCoffee.getIcJavaVersion
import ic.apps.ic.UiCallback
import ic.javac.compileJava
import ic.kotlinc.compileKotlin
import ic.storage.Directory
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.collection.JoinCollection
import ic.struct.list.EditableList
import ic.struct.set.CountableSet
import ic.struct.set.EditableSet
import ic.struct.set.UnionCountableSet
import ic.throwables.CompilationException
import ic.throwables.Fatal
import ic.throwables.NotExists
import ic.throwables.NotNeeded


internal fun buildPackage(

	distribution: Distribution,
	outputDirectory: Directory,
	packageName: String,
	packagesToBuild: CountableSet<String>,
	builtPackages: EditableSet<String>,
	uiCallback: UiCallback

) {

	if (builtPackages.contains(packageName)) return

	val fullDependencies = CountableSet.Default(getFullDependencies(distribution, packageName))

	val fullDependenciesWithKotlin = UnionCountableSet(
		fullDependencies,
		CountableSet.Default("kotlin")
	)

	fullDependenciesWithKotlin.forEach { dependencyPackageName ->

		if (packageName == "kotlin" && dependencyPackageName == "kotlin") return@forEach

		if (!packagesToBuild.contains(dependencyPackageName)) return@forEach

		buildPackage(
			distribution,
			outputDirectory,
			dependencyPackageName,
			packagesToBuild,
			builtPackages,
			uiCallback
		)

	}

	uiCallback.onPackageBuildStarted(packageName)

	val srcPackageDir = distribution.getPackageSrcDirectory(packageName)

	val tmpPackageDir: Directory
	run {
		outputDirectory.removeIfExists(packageName)
		tmpPackageDir = outputDirectory.createFolder(packageName)
	}

	try {

		try {

			compileKotlin(
				srcPackageDir, tmpPackageDir,
				ConvertCollection<Directory, String>(
					JoinCollection(
						fullDependencies,
						Collection.Default(packageName)
					)
				) { dependency ->
					if (dependency == packageName) {
						return@ConvertCollection tmpPackageDir
					} else if (packagesToBuild.contains(dependency)) {
						return@ConvertCollection outputDirectory.getFolder(dependency)
					} else {
						return@ConvertCollection distribution.binDirectory.getFolder(dependency)
					}
				},
				JoinCollection(
					ConvertCollection<Collection<Directory>, String>(
						JoinCollection(
							fullDependencies,
							Collection.Default(packageName)
						)
					) { dependencyPackageName ->
						val packageJarDirectories = EditableList.Default<Directory>()
						packageJarDirectories.add(distribution.jarDirectory.getFolder(dependencyPackageName))
						try {
							packageJarDirectories.add(distribution.getPackageSrcDirectory(dependencyPackageName).getFolderOrThrow("jar"))
						} catch (ignored: NotExists) {}
						packageJarDirectories
					}
				),
				{ string -> uiCallback.handleWarning(string) }
			)

		} catch (ignored: NotNeeded) {}

		try {

			compileJava(
				srcPackageDir, tmpPackageDir,
				ConvertCollection(
					JoinCollection(
						fullDependenciesWithKotlin,
						Collection.Default(packageName)
					)
				) { dependency ->
					if (dependency == packageName) {
						return@ConvertCollection tmpPackageDir
					} else if (packagesToBuild.contains(dependency)) {
						return@ConvertCollection outputDirectory.getFolder(dependency)
					} else {
						return@ConvertCollection distribution.binDirectory.getFolder(dependency)
					}
				},
				JoinCollection(
					ConvertCollection<Collection<Directory>, String>(
						JoinCollection(
							fullDependenciesWithKotlin,
							Collection.Default(packageName)
						)
					) { dependency ->
						val packageJarDirectories = EditableList.Default<Directory>()
						packageJarDirectories.add(distribution.getJarDirectory().getFolder(dependency))
						try {
							packageJarDirectories.add(distribution.getPackageSrcDirectory(dependency).getFolderOrThrow("jar"))
						} catch (ignored: NotExists) {
						}

						packageJarDirectories
					}
				),
				{ warning -> uiCallback.handleWarning(warning) },
				getIcJavaVersion()
			)

		} catch (ignored: NotNeeded) {}

	} catch (compilationException: CompilationException) {
		throw Fatal.Runtime(compilationException)
	}

	builtPackages.add(packageName)

}