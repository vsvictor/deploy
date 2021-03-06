package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.text.Charset
import ic.text.Text
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.util.Bytes.bytesToInt


object TextStreamParser : SafeGetter4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>): Any? {
		if (declaredClass != Text::class.java) throw NOT_SUPPORTED
		val length = bytesToInt(
			input.byte,
			input.byte,
			input.byte,
			input.byte
		)
		return Charset.UTF_8.bytesToText(input.read(length))
	}

}