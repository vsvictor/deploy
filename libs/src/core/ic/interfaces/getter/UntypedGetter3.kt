package ic.interfaces.getter


import ic.throwables.None


interface UntypedGetter3<Arg1, Arg2, Arg3> : SafeUntypedGetter3<Arg1, Arg2, Arg3, None> {

	override fun getObject (arg1: Arg1, arg2: Arg2, arg3: Arg3) : Any?

}