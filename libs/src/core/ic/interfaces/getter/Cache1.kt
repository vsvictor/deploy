package ic.interfaces.getter


import ic.struct.map.EditableMap


class Cache1<Value, Arg> (

	private val cacheImplementation : EditableMap<Value, Arg>,

	private val sourceGetter : (Arg) -> Value

) : Getter1<Value, Arg> {

	override fun get (arg : Arg) = cacheImplementation.createIfNull(arg) { sourceGetter(arg) }

	constructor (sourceGetter : (Arg) -> Value) : this (EditableMap.Default(), sourceGetter)

}