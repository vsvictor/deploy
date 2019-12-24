package ic.serial.stream


import ic.interfaces.getter.SafeGetter4
import ic.stream.ByteInput
import ic.struct.list.JoinList
import ic.struct.list.List
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import ic.throwables.NotSupported
import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.util.reflect.instantiate
import ic.util.reflect.staticMethods


object StreamSerializableStreamParser : SafeGetter4<Any?, ByteInput, Class<*>?, List<Class<*>>, List<Any?>, NotSupported> {

	override fun get (input: ByteInput, declaredClass: Class<*>?, argClasses: List<Class<*>>, args: List<Any?>): Any? {

		if (declaredClass == null) 												throw NOT_SUPPORTED
		if (!StreamSerializable::class.java.isAssignableFrom(declaredClass)) 	throw NOT_SUPPORTED

		val classToInstantiate : Class<out Any> = try {
			declaredClass.getDeclaredField("SERIALIZABLE_CLASS_TO_INSTANTIATE").get(null) as Class<*>
		} catch (e: IllegalAccessException) { throw AccessDenied.Runtime(e)
		} catch (e: NoSuchFieldException) { declaredClass }

		try {
			return declaredClass.staticMethods.getOrThrow("parse", List(ByteInput::class.java))(null, List(input))
		} catch (noSuchMethod: NotExists) {}

		return classToInstantiate.instantiate(
			JoinList<Class<*>>(
				List.Default(ByteInput::class.java),
				argClasses
			),
			JoinList<Any?>(
				List.Default(input),
				args
			)
		)

	}

}