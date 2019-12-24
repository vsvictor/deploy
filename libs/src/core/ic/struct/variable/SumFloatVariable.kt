package ic.struct.variable

import ic.struct.collection.Collection


class SumFloatVariable (

	val operands : Collection<FloatVariable>

) : FloatVariable {

	override val floatValue : Float get() {
		var sum = 0F
		operands.forEach { sum += it.floatValue }
		return sum
	}

	constructor (vararg operands: FloatVariable) : this (Collection.Default(*operands))

}