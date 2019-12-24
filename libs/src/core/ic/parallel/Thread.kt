package ic.parallel


import ic.Service
import ic.interfaces.action.Action
import ic.interfaces.condition.Condition
import java.util.Date


abstract class Thread : Service() {

	protected abstract fun doInParallel()

	private val javaThread = java.lang.Thread {
		try {
			doInParallel()
		} finally {
			notifyEnded()
		}
	}

	override fun isReusable() = false

	override fun implementStart() {
		javaThread.start()
	}

	override fun implementStop() {
		if (!javaThread.isAlive) return
		javaThread.interrupt()
		waitFor()
	}


	fun waitFor() {
		try {
			javaThread.join()
		} catch (e: InterruptedException) {
			throw Error(e)
		}
	}


	class Final (private val action: () -> Unit) : Thread() {
		override fun doInParallel() = action()
	}


}
