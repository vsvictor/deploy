@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.util.Arrays.createByteArray


internal object BooleanStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Boolean>() {

	override val type get() = Boolean::class.java

	override fun implementGet (obj: Boolean) : DeclaredClassWithContent<ByteSequence> {
		return DeclaredClassWithContent(
			Boolean::class.java,
			ByteSequence.FromByteArray(createByteArray(if (obj) 0.toByte() else 1.toByte()))
		)
	}

}