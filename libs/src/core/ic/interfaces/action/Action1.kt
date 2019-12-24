package ic.interfaces.action


import ic.throwables.None


interface Action1<Arg> : SafeAction1<Arg, None> {

	class Final<Arg> (private val lambda: (Arg) -> Unit) : Action1<Arg> {

		override fun run (arg: Arg) = lambda(arg)

	}

}
