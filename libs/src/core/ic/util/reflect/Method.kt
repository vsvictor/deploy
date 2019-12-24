@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.util.reflect


import ic.interfaces.getter.UntypedGetter2
import ic.struct.list.List
import ic.throwables.AccessDenied
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


class Method internal constructor (private val javaMethod : Method) : UntypedGetter2<Any?, List<Any?>> {

	override fun getObject (obj: Any?, args: List<Any?>): Any {

		return try {
			javaMethod.invoke(obj, *args.toArray())
		} catch (e: IllegalAccessException) 	{ throw AccessDenied.Runtime(e)
		} catch (e: InvocationTargetException) 	{ throw e.targetException }

	}

}