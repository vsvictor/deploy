@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package ic.struct.map


import ic.annotations.ToOverride
import ic.interfaces.condition.Condition1
import ic.struct.variable.Ephemeral
import ic.throwables.NotExists
import ic.hash.HashTable
import ic.hash.IntHasher
import ic.interfaces.action.Action1
import ic.struct.set.CountableSet


class EphemeralKeysMap<Value, Key>(

	private val ephemeralVariableCreator: (Key) -> Ephemeral<Key>

) : EditableMap<Value, Key> {

	private val keyHasher = initKeyHasher()


	private val hashTable = HashTable(IntHasher<Entry> { entry -> entry.keyHash })


	@ToOverride
	protected fun initKeyHasher(): IntHasher<Key> {
		return IntHasher.Default()
	}


	private inner class Entry internal constructor(key: Key, internal val value: Value) {
		internal val ephemeralKey: Ephemeral<Key>?
		internal val keyHash: Int

		init {
			this.ephemeralKey = ephemeralVariableCreator(key)
			this.keyHash = key.hashCode()
		}
	}

	private inner class EntrySearcher internal constructor(internal val key: Key) : Condition1<Entry> {
		override fun `is`(entry: Entry): Boolean {
			return entry.ephemeralKey!!.get() === key
		}
	}


	@Synchronized
	internal fun cleanUp() {
		hashTable.removeAll { entry -> entry.ephemeralKey!!.get() == null }
	}


	@Synchronized
	override fun get(key: Key): Value? {
		cleanUp()
		try {
			return hashTable.get(
				keyHasher.get(key),
				EntrySearcher(key)
			).value
		} catch (notExists: NotExists) {
			return null
		}

	}

	@Synchronized
	public override fun set(key: Key, value: Value) {
		hashTable.set(
			keyHasher.get(key),
			EntrySearcher(key),
			Entry(key, value)
		)
	}

	override fun getKeys(): CountableSet<Key> {
		return object : CountableSet<Key> {

			override fun implementForEach(action: Action1<Key>) {
				synchronized(this@EphemeralKeysMap) {
					hashTable.forEach { entry ->
						val key = entry.ephemeralKey!!.get()
						if (key == null) return@forEach
						action.run(key)
					}
				}
			}

			override fun contains(key: Key): Boolean {
				synchronized(this@EphemeralKeysMap) {
					return hashTable.contains(
						keyHasher.get(key),
						EntrySearcher(key)
					)
				}
			}

		}
	}


}
