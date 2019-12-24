package ic

internal open class JvmClass : Exception() {

	companion object {

		val JVM_CLASS = object : JvmClass() {
			override fun fillInStackTrace() = null
		}

	}

}

