package ic.parallel

import ic.interfaces.action.Action


fun doInParallel (action: () -> Unit) {
	Thread(action).start()
}

fun doInParallel (action: Action) = doInParallel (action as () -> Unit)