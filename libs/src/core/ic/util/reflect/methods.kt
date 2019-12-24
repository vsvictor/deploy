@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.util.reflect


import ic.struct.list.List
import ic.struct.map.CountableMap2


val Class<*>.nonStaticMethods : CountableMap2<Method, String, List<Class<*>>> get() {
	return object : CountableMap2<Method, String, List<Class<*>>> {
		override val keys = getNonStaticMethodNamesAndArgClasses(this@nonStaticMethods)
		override fun get (name: String, argClasses: List<Class<*>>) : Method? {
			return getMethodOrNull(this@nonStaticMethods, name, argClasses)
		}
	}
}

val Class<*>.staticMethods : CountableMap2<Method, String, List<Class<*>>> get() {
	return object : CountableMap2<Method, String, List<Class<*>>> {
		override val keys = getStaticMethodNamesAndArgClasses(this@staticMethods)
		override fun get (name: String, argClasses: List<Class<*>>) : Method? {
			return getMethodOrNull(this@staticMethods, name, argClasses)
		}
	}
}