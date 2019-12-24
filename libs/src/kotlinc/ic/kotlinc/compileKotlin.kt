package ic.kotlinc


import ic.cmd.SystemConsole
import ic.storage.Directory
import ic.struct.collection.Collection
import ic.text.SplitText
import ic.throwables.CompilationException
import ic.throwables.NotNeeded

import ic.throwables.NotNeeded.NOT_NEEDED


@Throws(CompilationException::class, NotNeeded::class)
fun compileKotlin(

	inputDir: Directory,
	outputDir: Directory,

	compiledDependencies: Collection<Directory>,
	jarDependencies: Collection<Directory>,

	warningHandler: (String) -> Unit

) {

	prepareSystem()

	val systemConsole = SystemConsole()

	systemConsole.writeLine(generateKotlinCompileScript(
		inputDir, outputDir, compiledDependencies, jarDependencies,
		{ directory -> directory.absolutePath }
	))

	run {
		val response = systemConsole.read()
		for (line in SplitText(response, '\n')) {
			if (line.isEmpty) continue else
			if (line.contains(" warning: ")) 					warningHandler.invoke(line.stringValue) else
			if (line.startsWith("import ")) 					warningHandler.invoke(line.stringValue) else
			if (line.startsWith("class ")) 						warningHandler.invoke(line.stringValue) else
			if (line.startsWith("abstract class ")) 			warningHandler.invoke(line.stringValue) else
			if (line.startsWith("object ")) 					warningHandler.invoke(line.stringValue) else
			if (line.startsWith("fun ")) 						warningHandler.invoke(line.stringValue) else
			if (line.startsWith("val ")) 						warningHandler.invoke(line.stringValue) else
			if (line.startsWith(" ") || line.startsWith("\t")) 	warningHandler.invoke(line.stringValue) else
			if (line.equals("error: no source files")) throw NOT_NEEDED
			else throw CompilationException("$response\nLINE: $line")
		}
	}

}