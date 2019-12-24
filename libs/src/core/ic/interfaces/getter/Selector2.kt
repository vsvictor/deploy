package ic.interfaces.getter


import ic.struct.list.List
import ic.throwables.NotSupported


import ic.throwables.NotSupported.NOT_SUPPORTED


abstract class Selector2<Value, Arg1, Arg2> : Getter2<Value, Arg1, Arg2> {

	protected abstract val selectGetters : Iterable<(Arg1, Arg2) -> Value>

	@Throws(NotSupported::class)
	fun safeGet(arg1: Arg1, arg2: Arg2): Value {
		for (selectGetter in selectGetters) {
			try {
				return selectGetter.invoke(arg1, arg2)
			} catch (notSupported: NotSupported) {}
		}
		throw NOT_SUPPORTED
	}

	override fun get (arg1: Arg1, arg2: Arg2) = safeGet(arg1, arg2)

	class Final<Value, Arg1, Arg2> (
		override val selectGetters : Iterable<(Arg1, Arg2) -> Value>
	) : Selector2<Value, Arg1, Arg2>() {
		constructor (vararg selectGetters : (Arg1, Arg2) -> Value) : this (List.Default(*selectGetters))
	}

}