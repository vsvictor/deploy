package ic.javac


import ic.cmd.SystemConsole
import ic.storage.Directory
import ic.struct.collection.Collection
import ic.text.*
import ic.throwables.CompilationException
import ic.throwables.NotNeeded

import ic.throwables.NotNeeded.NOT_NEEDED


@Throws(CompilationException::class, NotNeeded::class)
fun compileJava(

	inputDir: Directory,
	outputDir: Directory,

	compiledDependencies: Collection<Directory>,
	jarDependencies: Collection<Directory>,

	warningHandler: (String) -> Unit,

	javaVersion: String

) {

	val systemConsole = SystemConsole()

	systemConsole.writeLine(generateJavaCompileScript(
		inputDir, outputDir, compiledDependencies, jarDependencies,
		{ directory -> directory.absolutePath },
		javaVersion
	))

	run {
		val response = systemConsole.read()
		for (line in SplitText(response, '\n')) {
			if (line.isEmpty) continue
			if (line.startsWith("Note: "))
				warningHandler.invoke(line.toString())
			else if (line.contains(": warning: "))
				warningHandler.invoke(line.toString())
			else if (line.startsWith("\t"))
				warningHandler.invoke(line.toString())
			else if (line.startsWith("  "))
				warningHandler.invoke(line.toString())
			else if (line.endsWith(" warning"))
				warningHandler.invoke(line.toString())
			else if (line.endsWith(" warnings"))
				warningHandler.invoke(line.toString())
			else if (line.startsWith("javac: file not found: ") && line.endsWith("/**/*.java"))
				throw NOT_NEEDED
			else if (line.startsWith("error: file not found: ") && line.endsWith("/**/*.java"))
				throw NOT_NEEDED
			else
				throw CompilationException("$response\nLINE: $line")
		}
	}

}