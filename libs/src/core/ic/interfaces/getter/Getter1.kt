package ic.interfaces.getter


import ic.throwables.None


interface Getter1<Value, Arg> : SafeGetter1<Value, Arg, None> {

	override fun get (arg: Arg) : Value

}
