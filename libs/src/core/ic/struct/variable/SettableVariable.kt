package ic.struct.variable


import ic.interfaces.setter.GetterSetter


interface SettableVariable<Value> : Variable<Value>, GetterSetter<Value> {


	override var value: Value

	override fun set(value: Value) {
		this.value = value
	}


	class Default<Value> : SettableVariable<Value> {

		private var valueValue : Value
		override var value : Value
			get() = valueValue
			set(value) { valueValue = value }

		constructor (value : Value) {
			valueValue = value
		}

	}


}
