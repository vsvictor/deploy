package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.util.Bytes.bytesToInt


object IntegerStreamParser : SafeGetter4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>): Any? {
		if (declaredClass != Int::class.java && declaredClass!!.name != "java.lang.Integer") throw NOT_SUPPORTED
		return bytesToInt(
			input.getByte(),
			input.getByte(),
			input.getByte(),
			input.getByte()
		)
	}

}