package ic.struct.variable


class StringLengthVariable (val string : StringVariable) : IntVariable {

	override val intValue: Int get() = string.value.length

}