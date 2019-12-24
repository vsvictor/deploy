package ic.interfaces.getter


import ic.throwables.NotSupported

import ic.throwables.NotSupported.NOT_SUPPORTED


abstract class TypeSelectGetter4<
	Value,
	Arg1, Arg2, Arg3, Arg4,
	Arg1Type : Arg1, Arg2Type : Arg2, Arg3Type : Arg3, Arg4Type : Arg4
> : SafeGetter4<Value, Arg1, Arg2, Arg3, Arg4, NotSupported> {

	protected abstract val arg1Type: Class<Arg1Type>
	protected abstract val arg2Type: Class<Arg2Type>
	protected abstract val arg3Type: Class<Arg2Type>
	protected abstract val arg4Type: Class<Arg2Type>

	protected abstract fun implementGet(arg1: Arg1Type, arg2: Arg2Type, arg3: Arg3Type, arg4: Arg4Type): Value

	@Throws(NotSupported::class)
	override fun get(arg1: Arg1, arg2: Arg2, arg3: Arg3, arg4: Arg4): Value {

		if (arg1 == null) throw NOT_SUPPORTED
		if (arg2 == null) throw NOT_SUPPORTED
		if (arg3 == null) throw NOT_SUPPORTED
		if (arg4 == null) throw NOT_SUPPORTED

		if (!arg1Type.isInstance(arg1)) throw NOT_SUPPORTED
		if (!arg2Type.isInstance(arg2)) throw NOT_SUPPORTED
		if (!arg3Type.isInstance(arg3)) throw NOT_SUPPORTED
		if (!arg4Type.isInstance(arg4)) throw NOT_SUPPORTED

		@Suppress("UNCHECKED_CAST")
		return implementGet(arg1 as Arg1Type, arg2 as Arg2Type, arg3 as Arg3Type, arg4 as Arg4Type)

	}

}