package ic.struct.map


import ic.interfaces.action.Action1
import ic.interfaces.getter.Getter2
import ic.struct.collection.Collection
import ic.struct.set.CountableSet


interface CountableMap2<Value, Key1, Key2> : Getter2<Value, Key1, Key2>, Collection<Value> {

	val keys: CountableSet<Pair<Key1, Key2>>

	override fun implementForEach (action: Action1<Value>) {
		keys.forEach {
			action(
				get(
					it.first,
					it.second
				)
			)
		}
	}

}