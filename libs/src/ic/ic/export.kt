package ic


import ic.AddPackage.addPackage
import ic.apps.ic.UiCallback
import ic.git.Git
import ic.git.Rejected
import ic.storage.Directory
import ic.storage.scp.Scp
import ic.struct.map.CountableMap
import ic.struct.map.UntypedCountableMap
import ic.struct.set.CountableSet
import ic.throwables.*

import ic.Build.build
import ic.TextResources.getResString
import ic.TextResources.getResText
import ic.UpdateProject.updateProject
import ic.cmd.SystemConsole
import ic.interfaces.action.Action1
import ic.struct.map.EditableMap
import ic.text.Charset


fun getExportedScriptsPath(projectType: Distribution.ProjectType?): String {
	if (projectType == null) return "."
	return if (projectType === Distribution.ProjectType.GRADLE) {
		"scripts"
	} else {
		throw NotSupported.Runtime("projectType: " + projectType.name)
	}
}

val exportedLaunchPath : String = "launch"


@Throws(Fatal::class)
fun export(

	inputDistribution: Distribution,
	outputDirectory: Directory,
	projectType: Distribution.ProjectType?,
	customData: UntypedCountableMap<String>,
	mainPackages: CountableSet<String>,
	uiCallback: UiCallback

) {

	val outputDistribution: Distribution
	run {
		if (projectType == null) {
			outputDistribution = Distribution(
				outputDirectory,
				true,
				"stores",
				"packages",
				"src",
				false,
				CountableMap.Default(),
				"bin",
				"tmp",
				"jar",
				exportedLaunchPath,
				true, null,
				customData
			)
		} else if (projectType === Distribution.ProjectType.GRADLE) {
			val customSrcPaths = EditableMap.Default<String, String>()
			run {
				if (mainPackages.count == 1) {
					mainPackages.forEach { packageName -> customSrcPaths.set(packageName, "src") }
				} else {
					mainPackages.forEach { packageName -> customSrcPaths.set(packageName, "src/$packageName") }
				}
			}
			outputDistribution = Distribution(
				outputDirectory,
				true,
				".ic/stores",
				".ic/packages",
				"libs/src",
				false,
				customSrcPaths,
				"bin",
				".ic/tmp",
				"libs/jar",
				exportedLaunchPath,
				false,
				Distribution.ProjectType.GRADLE,
				customData
			)
		} else
			throw NotSupported.Runtime("projectType: " + projectType.name)
	}

	outputDistribution.storesDirectory.replace(inputDistribution.storesDirectory)
	outputDistribution.packagesDirectory.replace(inputDistribution.packagesDirectory)

	inputDistribution.packageNames.forEach { packageName ->
		val outputPackageSrcPath: String
		try {
			outputPackageSrcPath = outputDistribution.getPackageSrcPath(
				Store.getStoreNameOrThrow(inputDistribution, packageName),
				packageName
			)
		} catch (notExists: NotExists) {
			throw InconsistentData(notExists)
		}
		Directory.createIfNotExists(outputDirectory, outputPackageSrcPath).replace(inputDistribution.getPackageSrcDirectory(packageName))
		Directory.getExisting(outputDirectory, outputPackageSrcPath).remove(".git")
	}

	outputDistribution.jarDirectory.replace(inputDistribution.jarDirectory)

	try {
		addPackage(outputDistribution, "ic", uiCallback)
	} catch (alreadyExists : AlreadyExists) {
		updateProject(outputDistribution)
	}

	build(
		outputDistribution,
		inputDistribution.packageNames,
		uiCallback
	)

	val outputScriptsDirectory = Directory.createIfNotExists(outputDirectory, getExportedScriptsPath(projectType))

	Scp.download(
		"instant-coffee.store:install/prepare-system",
		"public", "ilovejava",
		outputScriptsDirectory, "prepare-system"
	)

	run {
		outputScriptsDirectory.write("build", Charset.DEFAULT_UNIX.stringToByteArray("launch/ic local build"))
		val systemConsole = SystemConsole()
		systemConsole.writeLine("cd ${ outputScriptsDirectory.absolutePath }")
		systemConsole.writeLine("chmod +x build")
	}

	if (projectType == Distribution.ProjectType.GRADLE) {
		outputDistribution.rootDirectory.write(".gitignore", Charset.DEFAULT_UNIX.stringToByteArray(
			"/.idea\n" +
			"*.iml\n" +
			"/gradle\n" +
			"/.gradle\n" +
			"/gradlew\n" +
			"/gradlew.bat"
		))
	}

	outputDistribution.rootDirectory.write("readme.txt", Charset.DEFAULT_UNIX.textToByteArray(getResText("ic/exported-readme")));

}


@Throws(Fatal::class)
fun export(

	inputDistribution: Distribution,
	outputGitUrl: String,
	branch: String?,
	projectType: Distribution.ProjectType?,
	customData: UntypedCountableMap<String>,
	mainPackages: CountableSet<String>,
	uiCallback: UiCallback

) {

	val repositoryWriteAuth = getAuth(outputGitUrl, "write", uiCallback)

	uiCallback.onExportCloneStarted(outputGitUrl)

	val outputDirectory: Directory
	try {
		outputDirectory = Git.clone(
			outputGitUrl,
			inputDistribution.tmpDirectory,
			"exported",
			repositoryWriteAuth,
			Action1.Final { uiCallback.handleWarning(it) }
		)
	} catch (notExists: NotExists) {
		throw Fatal(getResString("ic/repository-not-exists").replace("$(url)", outputGitUrl))
	} catch (alreadyExists: AlreadyExists) {
		throw Fatal(getResString("ic/directory-already-exists").replace("$(path)", inputDistribution.tmpDirectory.absolutePath + "/exported"))
	} catch (accessDenied: AccessDenied) {
		throw Fatal(getResString("ic/repository-access-denied").replace("$(url)", outputGitUrl))
	}

	if (branch != null) {

		try {
			Git.checkout(
				outputDirectory,
				branch,
				Action1.Final { uiCallback.handleWarning(it) }
			)
		} catch (notNeeded: NotNeeded) {
		} catch (notExists: NotExists) {
			try {
				Git.branch(
					outputDirectory,
					branch,
					Action1.Final { uiCallback.handleWarning(it) }
				)
			} catch (alreadyExists: AlreadyExists) {
				throw InconsistentData(alreadyExists)
			}
		}

	}

	try {

		outputDirectory.keys.forEach { fileName ->
			if (fileName == ".git") return@forEach
			outputDirectory.remove(fileName)
		}

		export(inputDistribution, outputDirectory, projectType, customData, mainPackages, uiCallback)

		Git.addAll(outputDirectory)
		try {
			Git.commit(outputDirectory, uiCallback.askCommitMessage())
			Git.push(
				outputDirectory,
				outputGitUrl,
				repositoryWriteAuth,
				{ uiCallback.handleWarning(it) }
			)
		} catch (notNeeded: NotNeeded) {
			notNeeded.printStackTrace()
		} catch (rejected: Rejected) {



			throw Fatal(getResString("ic/repository-push-rejected").replace("$(url)", outputGitUrl))
		}

	} finally {

		outputDirectory.remove()

	}

}