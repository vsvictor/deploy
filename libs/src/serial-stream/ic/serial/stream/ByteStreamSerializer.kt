@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.util.Arrays.createByteArray


internal object ByteStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Byte>() {

	override val type get() = Byte::class.java

	override fun implementGet (obj: Byte) : DeclaredClassWithContent<ByteSequence> {
		return DeclaredClassWithContent(
			Byte::class.java,
			ByteSequence.FromByteArray(createByteArray(obj))
		)
	}

}