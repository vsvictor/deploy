package ic.interfaces.getter


import ic.throwables.None


interface Getter4<Value, Arg1, Arg2, Arg3, Arg4> : SafeGetter4<Value, Arg1, Arg2, Arg3, Arg4, None> {


	operator override fun get(arg1: Arg1, arg2: Arg2, arg3: Arg3, arg4: Arg4) : Value


}
