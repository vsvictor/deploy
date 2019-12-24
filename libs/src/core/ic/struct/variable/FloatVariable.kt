package ic.struct.variable


interface FloatVariable : Variable<Float> {


	val floatValue : Float

	override val value get() = floatValue


	@JvmDefault override fun get() = value


	class Constant : FloatVariable {

		private val backingValue : Float

		constructor (value : Float) {
			backingValue = value
		}

		override val floatValue get() = backingValue

	}

}
