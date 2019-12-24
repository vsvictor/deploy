package ic.interfaces.condition


import ic.struct.variable.IntVariable


class IntEqualCondition (

	val a : IntVariable,
	val b : IntVariable

) : Condition {

	override val value get() = a.intValue == b.intValue

}