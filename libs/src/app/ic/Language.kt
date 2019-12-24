package ic


import ic.stream.ByteSequence
import ic.struct.map.EditableMap
import ic.text.Charset
import ic.util.reflect.getClassByName
import ic.util.reflect.instantiate
import ic.struct.list.List


interface Language {

	fun eval (file : ByteSequence, main : Boolean) : Any?

	fun eval (file : ByteSequence) : Any? {
		return eval(file, false)
	}

	companion object {

		private val langByExtAssets = Assets("lang-by-ext")

		private val byExt = EditableMap.Default<Language, String>()

		fun byExt(ext : String) : Language {
			return byExt.createIfNull(ext) {
				getClassByName<Language>(
					Charset.DEFAULT_UNIX.bytesToString(
						langByExtAssets.readOrThrow(ext)
					)
				).instantiate(List(), List())
			}
		}

	}

}