@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.util.Bytes.longToByteArray


internal object LongStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Long>() {

	override val type get() = Long::class.java

	override fun implementGet (obj: Long) : DeclaredClassWithContent<ByteSequence> {
		return DeclaredClassWithContent(
			Integer::class.java,
			ByteSequence.FromByteArray(longToByteArray(obj))
		)
	}

}