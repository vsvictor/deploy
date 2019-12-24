package ic.interfaces.getter


import ic.struct.list.List
import ic.throwables.NotSupported


import ic.throwables.NotSupported.NOT_SUPPORTED


abstract class Selector4<Value, Arg1, Arg2, Arg3, Arg4> : Getter4<Value, Arg1, Arg2, Arg3, Arg4> {

	protected abstract val selectGetters : Iterable<(Arg1, Arg2, Arg3, Arg4) -> Value>

	protected open fun generateNotSupportedMessage(arg1: Arg1, arg2: Arg2, arg3: Arg3, arg4: Arg4) : String? = null

	@Throws(NotSupported::class)
	fun safeGet(arg1: Arg1, arg2: Arg2, arg3: Arg3, arg4: Arg4): Value {
		for (selectGetter in selectGetters) {
			try {
				return selectGetter(arg1, arg2, arg3, arg4)
			} catch (notSupported: NotSupported) {}
		}; throw run {
			val notSupportedMessage = generateNotSupportedMessage(arg1, arg2, arg3, arg4)
			if (notSupportedMessage == null) NOT_SUPPORTED
			else NotSupported(notSupportedMessage)
		}
	}

	override fun get (arg1: Arg1, arg2: Arg2, arg3: Arg3, arg4: Arg4) : Value {
		try {
			return safeGet(arg1, arg2, arg3, arg4)
		} catch (notSupported : NotSupported) { throw NotSupported.Runtime(notSupported) }
	}

	class Final<Value, Arg1, Arg2, Arg3, Arg4> (
		override val selectGetters : Iterable<(Arg1, Arg2, Arg3, Arg4) -> Value>
	) : Selector4<Value, Arg1, Arg2, Arg3, Arg4>() {
		constructor (vararg selectGetters : (Arg1, Arg2, Arg3, Arg4) -> Value) : this (List.Default(*selectGetters))
	}

}