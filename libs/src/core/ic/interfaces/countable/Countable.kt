package ic.interfaces.countable


import ic.interfaces.sizeable.Sizeable


interface Countable : Sizeable<Int> {


	val count: Int


	@JvmDefault
	val isEmpty: Boolean get() = count == 0

	@JvmDefault
	override val size get() = count


}
