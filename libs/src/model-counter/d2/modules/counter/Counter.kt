package d2.modules.counter


import ic.storage.Storage
import ic.struct.map.CountableMap
import ic.struct.map.EditableMap


fun incrementCounter(
	storage: 	Storage,
	userId: 	String,
	counterName: 	String,
	action: (Int) -> Unit
) {
	val counterMap = EditableMap.Default(storage.get(userId) { CountableMap.Default<Int, String>() })
	val newCounter = counterMap.get(counterName) { 0 } + 1
	counterMap[counterName] = newCounter
	storage.set(userId, counterMap)
	action(newCounter)
}