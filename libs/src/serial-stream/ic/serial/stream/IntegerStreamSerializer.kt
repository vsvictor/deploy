@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.util.Bytes.intToByteArray


internal object IntegerStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Int>() {

	override val type get() = Int::class.java

	override fun implementGet (obj: Int) : DeclaredClassWithContent<ByteSequence> {
		return DeclaredClassWithContent(
			Integer::class.java,
			ByteSequence.FromByteArray(intToByteArray(obj))
		)
	}

}