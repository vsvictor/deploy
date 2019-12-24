@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence
import ic.text.Charset
import ic.text.Text
import ic.util.Bytes.intToByteSequence


internal object TextStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Text>() {

	override val type get() = Text::class.java

	override fun implementGet (obj: Text) : DeclaredClassWithContent<ByteSequence> {
		val byteArray = Charset.UTF_8.textToByteArray(obj)
		return DeclaredClassWithContent(
			Text::class.java,
			JoinByteSequence(
				intToByteSequence(byteArray.size),
				ByteSequence.FromByteArray(byteArray)
			)
		)
	}

}