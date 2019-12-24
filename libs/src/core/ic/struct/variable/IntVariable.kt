package ic.struct.variable


interface IntVariable : Variable<Int> {


	val intValue : Int

	@JvmDefault
	override val value get() = intValue

	@JvmDefault
	override fun get() = intValue


	class Constant (override val intValue : Int) : IntVariable


}
