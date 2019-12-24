package ic.interfaces.getter


import ic.text.stringValue
import ic.throwables.NotSupported


abstract class Selector1<Value, Arg> : Getter1<Value, Arg> {

	abstract val selectGetters: Iterable<(Arg) -> Value>

	@Throws(NotSupported::class)
	fun safeGet (arg: Arg) : Value {
		for (selectGetter in selectGetters) {
			try {
				val value = selectGetter(arg)
				return value
			} catch (notSupported: NotSupported) {}
		}
		throw NotSupported()
	}

	override fun get (arg: Arg) : Value {
		try {
			return safeGet(arg)
		} catch (notSupported: NotSupported) {
			throw NotSupported.Runtime("arg: ${ stringValue(arg) }")
		}
	}

	class Final<Value, Arg>(
		override val selectGetters: Iterable<(Arg) -> Value>
	) : Selector1<Value, Arg>() {
		constructor(vararg selectGetters : (Arg) -> Value) : this(arrayListOf(*selectGetters))
	}

}