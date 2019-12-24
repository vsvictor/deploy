package ic.interfaces.getter


import ic.throwables.NotSupported

import ic.throwables.NotSupported.NOT_SUPPORTED


abstract class TypeSelectGetter2<Value, Arg1, Arg2, Arg1Type : Arg1, Arg2Type : Arg2> : SafeGetter2<Value, Arg1, Arg2, NotSupported> {

	protected abstract val arg1Type: Class<Arg1Type>
	protected abstract val arg2Type: Class<Arg2Type>

	protected abstract fun implementGet(arg1: Arg1Type, arg2: Arg2Type) : Value

	@Throws(NotSupported::class)
	override fun get(arg1: Arg1, arg2: Arg2): Value {

		if (arg1 == null) throw NOT_SUPPORTED
		if (arg2 == null) throw NOT_SUPPORTED

		if (!arg1Type.isInstance(arg1)) throw NOT_SUPPORTED
		if (!arg2Type.isInstance(arg2)) throw NOT_SUPPORTED

		@Suppress("UNCHECKED_CAST")
		return implementGet(arg1 as Arg1Type, arg2 as Arg2Type)

	}

}