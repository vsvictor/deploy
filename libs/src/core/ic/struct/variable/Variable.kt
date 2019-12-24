package ic.struct.variable


import ic.interfaces.getter.Getter


interface Variable<Value> : Getter<Value> {


	val value : Value


	@JvmDefault override fun get() = value


	class Constant<Value> (override val value : Value) : Variable<Value>


	class Final<Value> (private val valueGetter : () -> Value) : Variable <Value> {
		override val value get() = valueGetter()
	}

}
