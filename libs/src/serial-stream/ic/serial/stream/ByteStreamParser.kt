package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED


object ByteStreamParser : SafeGetter4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>): Any? {
		if (declaredClass != Byte::class.java && declaredClass!!.name != "java.lang.Byte") throw NOT_SUPPORTED
		return input.getByte()
	}

}