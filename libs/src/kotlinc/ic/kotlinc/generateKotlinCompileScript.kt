package ic.kotlinc


import ic.interfaces.getter.Getter1
import ic.storage.Directory
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.collection.JoinCollection
import ic.struct.list.List
import ic.text.Charset
import ic.text.JoinStrings
import ic.text.Text


fun generateKotlinCompileScript(

	inputDir: Directory,
	outputDir: Directory,

	compiledDependencies: Collection<Directory>,
	jarDependencies: Collection<Directory>,

	directoryToPath: (Directory) -> String

) : Text {

	val script = (
		"shopt -s globstar;" +
		"kotlinc " + (
			"-jvm-target 1.8 " +
			"-Xjvm-default=enable " +
			"-classpath " + (
				JoinStrings(
					List.Default<String>(
						ConvertCollection<String, Directory>(compiledDependencies, directoryToPath)
					),
					':'
				).toString() +
				":" +
				JoinStrings(
					List.Default(
						JoinCollection<String>(
							ConvertCollection<Collection<String>, Directory>(jarDependencies) { jarDependency : Directory ->
								val jarDependencyPath = directoryToPath(jarDependency)
								//System.out.println("JAR_DEPENDENCY " + jarDependency.absolutePath)
								ConvertCollection(jarDependency.keys) {
									key -> "'$jarDependencyPath/${Charset.DEFAULT_UNIX.encodeUrl(key)}'"
								}
							}
						)
					),
					':'
				)
			) + " " +
			directoryToPath(inputDir) + "/ " +
			"-d " + directoryToPath(outputDir)
		)
	)

	//System.out.println("SCRIPT " + script);

	return Text.FromString(script)

}