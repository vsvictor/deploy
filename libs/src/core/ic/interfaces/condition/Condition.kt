package ic.interfaces.condition


import ic.struct.collection.Collection
import ic.throwables.None


interface Condition : SafeCondition<None> {

	@JvmDefault
	val value : Boolean

	@JvmDefault
	override fun getBoolean() : Boolean = value

	companion object {

		@JvmField val FALSE: Condition = object : Condition {
			override val value = false
		}

		@JvmField val TRUE: Condition = object : Condition {
			override val value = true
		}

		@JvmField val ELSE = TRUE

	}

	class Final (private val lambda : () -> Boolean) : Condition {
		override val value get() = lambda()
	}

	class Not (val condition: Condition) : Condition {
		override val value get() = !condition.value
	}

	class And (val operands: Iterable<Condition>) : Condition {
		override val value : Boolean get() {
			for (operand in operands) {
				if (!operand.value) return false
			}; return true
		}
		constructor (vararg operands : Condition) : this (Collection.Default<Condition>(*operands))
	}

}
