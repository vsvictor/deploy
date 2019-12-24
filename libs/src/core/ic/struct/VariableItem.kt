package ic.struct


import ic.struct.variable.Variable


class VariableItem<Item> (private val valueGetter : () -> Item) : Variable <Item> {

	override val value get() = valueGetter()

}