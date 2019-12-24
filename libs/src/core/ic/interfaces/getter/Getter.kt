package ic.interfaces.getter


import ic.annotations.Narrowing
import ic.annotations.Repeat
import ic.throwables.None
import ic.throwables.NotExists


interface Getter<Value> : SafeGetter<Value, None> {


	@Narrowing
	override fun get(): Value?

	@Narrowing
	@Repeat
	@Throws(NotExists::class)
	@JvmDefault override fun getOrThrow(): Value {
		return super.getOrThrow()
	}

	@Narrowing
	@Repeat
	@Throws(NotExists.Runtime::class)
	@JvmDefault override fun getNotNull(): Value {
		return super.getNotNull()
	}

	@Narrowing
	@Repeat
	@JvmDefault override fun get(defaultValueGetter: () -> Value): Value {
		return super.get(defaultValueGetter)
	}


}
