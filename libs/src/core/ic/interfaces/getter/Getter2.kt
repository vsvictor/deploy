package ic.interfaces.getter


import ic.throwables.None
import ic.throwables.NotExists

import ic.throwables.NotExists.NOT_EXISTS


interface Getter2<Value, Arg1, Arg2> : SafeGetter2<Value, Arg1, Arg2, None> {

	operator override fun get(arg1: Arg1, arg2: Arg2) : Value?

	@Throws(NotExists::class)
	fun getOrThrow(arg1: Arg1, arg2: Arg2): Value {
		return get(arg1, arg2) ?: throw NOT_EXISTS
	}

	@Throws(NotExists.Runtime::class)
	fun getNotNull(arg1: Arg1, arg2: Arg2): Value {
		try {
			return getOrThrow(arg1, arg2)
		} catch (notExists: NotExists) {
			throw NotExists.Runtime(notExists)
		}

	}


}
