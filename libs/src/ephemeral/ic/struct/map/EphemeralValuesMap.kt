@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.struct.map


import ic.annotations.ToOverride
import ic.struct.set.CountableSet
import ic.struct.variable.Ephemeral
import ic.throwables.NotExists

import java.lang.System.currentTimeMillis


class EphemeralValuesMap<Value, Key>(

	private val ephemeralVariableCreator: (Value) -> Ephemeral<Value>

) : EditableMap<Value, Key> {

	private val implementation = initImplementation()


	@Volatile
	private var lastCleanUp = currentTimeMillis()


	@ToOverride
	protected fun initImplementation(): EditableMap<Ephemeral<Value>, Key> {
		return EditableMap.Default()
	}

	private fun cleanUp() {
		if (currentTimeMillis() - lastCleanUp < CLEAN_UP_DELAY) return
		synchronized(this) {
			implementation.keys.forEach { key ->
				try {
					if (implementation.getOrThrow(key).get() == null) implementation.set(key, null)
				} catch (notExists: NotExists) {
				}
			}
		}
		lastCleanUp = currentTimeMillis()
	}


	@Synchronized
	override fun get(key: Key): Value? {
		cleanUp()
		val ephemeralValue = implementation.get(key) ?: return null
		return ephemeralValue.get()
	}

	public override fun set(key: Key, value: Value?) {
		implementation.set(key, if (value == null) null else ephemeralVariableCreator(value))
	}

	override fun getKeys(): CountableSet<Key> {
		return implementation.keys
	}

	companion object {
		private val CLEAN_UP_DELAY: Long = 4194304
	}


}
