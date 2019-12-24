package ic.util.reflect


import ic.throwables.NotExists


@Throws(NotExists::class)
fun <Type> getClassByNameOrThrow (name : String) : Class<Type> {
	try {
		@Suppress("UNCHECKED_CAST")
		return Class.forName(name) as Class<Type>
	} catch (e: ClassNotFoundException) { throw NotExists(e) }
}


fun <Type> getClassByName (name : String) : Class<Type> = try {
	getClassByNameOrThrow(name)
} catch (notExists: NotExists) { throw NotExists.Runtime(notExists) }