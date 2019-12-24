package ic.util.reflect


import ic.struct.list.List
import ic.throwables.NotExists
import java.lang.RuntimeException
import java.lang.reflect.InvocationTargetException


@Throws(NotExists::class)
fun <Type> Class<Type>.instantiateOrThrow (argClasses: List<Class<*>>, args: List<Any?>) : Type {
	try {
		return getConstructor(*argClasses.toArray(Class::class.java)).newInstance(*args.toArray())
	} catch (e: NoSuchMethodException) 		{ throw NotExists(e)
	} catch (e: InvocationTargetException) 	{ throw e.targetException
	} catch (e: Throwable) {
		throw RuntimeException(
			"Can't instantiate $name with args ($argClasses) $args",
			e
		)
	}
}


fun <Type> Class<Type>.instantiate (argClasses: List<Class<*>>, args: List<Any?>) : Type = try {
	instantiateOrThrow(argClasses, args)
} catch (noSuchConstructor : NotExists) { throw NotExists.Runtime(noSuchConstructor) }