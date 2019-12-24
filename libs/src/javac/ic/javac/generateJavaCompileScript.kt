package ic.javac


import ic.storage.Directory
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.list.List
import ic.text.JoinStrings


fun generateJavaCompileScript(

	inputDir: Directory,
	outputDir: Directory,

	compiledDependencies: Collection<Directory>,
	jarDependencies: Collection<Directory>,

	directoryToPath: (Directory) -> String,

	javaVersion: String

): String = (

	"shopt -s globstar;" +
	"javac -source " + javaVersion + " -target " + javaVersion + " -Xlint:unchecked -Xlint:-options " + (
		"-classpath " + (
			JoinStrings(
				List.Default(
					ConvertCollection(compiledDependencies, directoryToPath)
				),
				':'
			).stringValue +
			":" +
			JoinStrings(
				List.Default(
					ConvertCollection(jarDependencies) { directory -> "'" + directoryToPath.invoke(directory) + "/*'" }
				),
				':'
			).stringValue
		) + " " +
		"-sourcepath " + directoryToPath.invoke(inputDir) + " " +
		"-d " + directoryToPath.invoke(outputDir) + " " +
		directoryToPath.invoke(inputDir) + "/**/*.java"
	)

)