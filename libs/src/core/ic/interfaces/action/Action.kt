package ic.interfaces.action


import ic.throwables.None


interface Action : SafeAction<None> {

	class Final (private val lambda : () -> Unit) : Action {

		override fun run() { lambda() }

	}

}
