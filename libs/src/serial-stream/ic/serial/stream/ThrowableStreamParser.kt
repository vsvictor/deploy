package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.List
import ic.text.Text
import ic.throwables.NotExists
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.util.reflect.instantiateOrThrow


object ThrowableStreamParser : SafeGetter4<Any?, ByteInput, Class<*>, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>, argClasses: List<Class<*>>, args: List<Any?>) : Any? {
		if (!Throwable::class.java.isAssignableFrom(declaredClass)) throw NOT_SUPPORTED
		val throwableMessage : String 			= parseFromStream(input, String::class.java)
		val throwableStackTraceAsText : Text 	= parseFromStream(input, Text::class.java)
		val message = "$throwableMessage\n${ throwableStackTraceAsText.asString }"
		return try {
			declaredClass.instantiateOrThrow(
				List<Class<*>>(String::class.java),
				List<Any?>(message)
			)
		} catch (noSuchConstructor: NotExists) {
			declaredClass.superclass.instantiateOrThrow(
				List<Class<*>>(String::class.java),
				List<Any?>(message)
			)
		}
	}

}