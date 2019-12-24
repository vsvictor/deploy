package ic.interfaces.condition


import ic.struct.variable.FloatVariable


class FloatLessThanCondition (

	val a : FloatVariable,
	val b : FloatVariable

) : Condition {

	override val value get() = a.notNull < b.notNull

}