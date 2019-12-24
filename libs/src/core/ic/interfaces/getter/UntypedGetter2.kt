package ic.interfaces.getter


import ic.throwables.None


interface UntypedGetter2<Arg1, Arg2> : SafeUntypedGetter2<Arg1, Arg2, None> {

	override fun getObject (arg1: Arg1, arg2: Arg2) : Any?

}