package ic.interfaces.action


import ic.throwables.None


interface Action2<Arg1, Arg2> : SafeAction2<Arg1, Arg2, None> {

	class Final<Arg1, Arg2> (private val lambda : (Arg1, Arg2) -> Unit) : Action2<Arg1, Arg2> {
		override fun run (arg1: Arg1, arg2: Arg2) = lambda(arg1, arg2)
	}

}
