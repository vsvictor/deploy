package ic


import ic.interfaces.action.Action
import ic.interfaces.action.SafeAction
import ic.throwables.Break


fun loop(action: () -> Unit) {
	while (true) {
		try {
			action()
		} catch (breakThrowable: Break) {
			break
		}

	}
}


fun loop(action: SafeAction<Break>) {
	while (true) {
		try {
			action.run()
		} catch (breakThrowable: Break) {
			break
		}

	}
}


fun loop(action: Action, count: Int) {
	for (i in 0 until count) {
		action.run()
	}
}