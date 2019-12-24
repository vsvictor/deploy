package ic.interfaces.getter


import ic.throwables.NotSupported

import ic.throwables.NotSupported.NOT_SUPPORTED


abstract class TypeSelectGetter1<Value, Arg, ArgType : Arg> : SafeGetter1<Value, Arg, NotSupported> {

	protected abstract val type: Class<ArgType>

	protected abstract fun implementGet(arg: ArgType): Value

	@Throws(NotSupported::class)
	override fun get(arg: Arg): Value {

		assert (arg != null)

		if (!type.isInstance(arg)) throw NOT_SUPPORTED

		@Suppress("UNCHECKED_CAST")
		return implementGet(arg as ArgType)

	}

}