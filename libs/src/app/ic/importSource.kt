package ic


import ic.JvmClass.Companion.JVM_CLASS
import ic.storage.MergeStreamStorage
import ic.storage.StreamStorage
import ic.struct.list.List
import ic.struct.list.ReverseList
import ic.struct.sequence.ConvertSequence
import ic.struct.set.EditableSet
import ic.text.SplitString
import ic.throwables.AlreadyExists.ALREADY_EXISTS


private val imported = EditableSet.Default<String>()


internal fun importSource(path : String, main : Boolean) {

	val language 		: Language
	var folder 			: StreamStorage
	val fileNameWithExt : String

	synchronized(imported) {

		if (imported.contains(path)) throw ALREADY_EXISTS;

		val distribution = Distribution.get()

		folder = MergeStreamStorage(
			ConvertSequence<StreamStorage, String>(ReverseList(app.includedPackageNames)) {
				packageName -> distribution.getPackageSrcDirectory(packageName)
			}
		)

		val pathSplit = List.Default<String>(
			SplitString(path, '.')
		)

		val pathSplitLength = pathSplit.length

		var i = 0; while (i < pathSplitLength - 1) {
			folder = folder.getFolder(pathSplit[i])
			i++
		}

		val fileName = pathSplit[pathSplitLength - 1]

		fileNameWithExt = folder.keys.find { it.startsWith("$fileName.") }

		val ext = fileNameWithExt.substring("$fileName.".length)

		if (ext == "java") 	throw JVM_CLASS
		if (ext == "kt")	throw JVM_CLASS

		language = Language.byExt(ext)

		imported.add(path)

	}

	language.eval(folder.read(fileNameWithExt), main)

}


@Throws(JvmClass::class)
fun importSource(path : String) {
	importSource(path, false)
}