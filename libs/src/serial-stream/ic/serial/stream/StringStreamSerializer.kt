@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence
import ic.text.Charset
import ic.util.Bytes.intToByteSequence


internal object StringStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, String>() {

	override val type get() = String::class.java

	override fun implementGet (obj: String) : DeclaredClassWithContent<ByteSequence> {
		val byteArray = Charset.UTF_8.stringToByteArray(obj)
		return DeclaredClassWithContent(
			String::class.java,
			JoinByteSequence(
				intToByteSequence(byteArray.size),
				ByteSequence.FromByteArray(byteArray)
			)
		)
	}

}