@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence
import ic.struct.list.ConvertList
import ic.struct.list.List
import ic.util.Bytes.intToByteSequence


internal object ListStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, List<*>>() {

	override val type get() = List::class.java

	override fun implementGet (obj: List<*>) : DeclaredClassWithContent<ByteSequence> {
		synchronized (obj) {
			return DeclaredClassWithContent(
				List::class.java,
				JoinByteSequence(
					intToByteSequence(obj.size),
					JoinByteSequence(
						List(
							ConvertList (obj) { serializeToByteSequence(it) }
						)
					)
				)
			)
		}
	}

}