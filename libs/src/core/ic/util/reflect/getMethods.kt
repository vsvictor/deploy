package ic.util.reflect


import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.list.List
import ic.struct.set.CountableSet
import ic.throwables.NotExists
import ic.throwables.Skip.SKIP


fun getNonStaticMethodNames (c : Class<out Any>) : CountableSet<String> = CountableSet.Default(
	ConvertCollection<String, java.lang.reflect.Method>(
		Collection.Default<java.lang.reflect.Method>(*c.methods)
	) { method ->
		if (java.lang.reflect.Modifier.isStatic(method.modifiers)) throw SKIP;
		method.name
	}
)


fun getNonStaticMethodNamesAndArgClasses (c : Class<out Any>) : CountableSet<Pair<String, List<Class<*>>>> = CountableSet.Default(
	ConvertCollection<Pair<String, List<Class<*>>>, java.lang.reflect.Method>(
		Collection.Default<java.lang.reflect.Method>(*c.methods)
	) { method ->
		if (java.lang.reflect.Modifier.isStatic(method.modifiers)) throw SKIP;
		Pair(method.name, List(*method.parameterTypes))
	}
)


fun getStaticMethodNames (c : Class<out Any>) : CountableSet<String> = CountableSet.Default(
	ConvertCollection<String, java.lang.reflect.Method>(
		Collection.Default<java.lang.reflect.Method>(*c.methods)
	) { method ->
		if (!java.lang.reflect.Modifier.isStatic(method.modifiers)) throw SKIP;
		method.name
	}
)

fun getStaticMethodNamesAndArgClasses (c : Class<out Any>) : CountableSet<Pair<String, List<Class<*>>>> = CountableSet.Default(
	ConvertCollection<Pair<String, List<Class<*>>>, java.lang.reflect.Method>(
		Collection.Default<java.lang.reflect.Method>(*c.methods)
	) { method ->
		if (!java.lang.reflect.Modifier.isStatic(method.modifiers)) throw SKIP;
		Pair(method.name, List(*method.parameterTypes))
	}
)


@Throws(NotExists::class)
fun getMethodOrThrow (c : Class<out Any>, name: String, argClasses: List<Class<*>>) : Method = try {
	Method(c.getMethod(name, *argClasses.toArray(Class::class.java)))
} catch (e: NoSuchMethodException) { throw NotExists(e) }

fun getMethodOrNull (c : Class<out Any>, name: String, argClasses: List<Class<*>>) : Method? = try {
	getMethodOrThrow(c, name, argClasses)
} catch (e: NotExists) { null }