@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.serial.stream


import ic.interfaces.getter.TypeSelectGetter1
import ic.serial.DeclaredClassWithContent
import ic.stream.ByteSequence
import ic.stream.JoinByteSequence
import ic.text.Text
import ic.throwables.stackTraceAsText


internal object ThrowableStreamSerializer : TypeSelectGetter1<DeclaredClassWithContent<ByteSequence>, Any?, Throwable>() {

	override val type get() = Throwable::class.java

	override fun implementGet (obj: Throwable) : DeclaredClassWithContent<ByteSequence> {
		return DeclaredClassWithContent(
			obj.javaClass,
			JoinByteSequence(
				serializeToByteSequence(String::class.java, obj.message),
				serializeToByteSequence(Text::class.java, 	obj.stackTraceAsText)
			)
		)
	}

}