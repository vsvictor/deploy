package ic.struct.sequence


// java imports:


// core imports:

import ic.annotations.Narrowing
import ic.interfaces.action.Safe2Action1
import ic.throwables.Break
import ic.throwables.Empty
import ic.throwables.End
import ic.throwables.NotSupported

import ic.throwables.Empty.EMPTY
import ic.throwables.End.END
import ic.util.Arrays.iterableToArray


/**
 * Sequence get a data structure that makes possible to iterate through its Item's.
 * Usually, Sequence get strictly ordered.
 * Can be infinite or finite.
 */
interface Sequence<Item> : SafeSequence<Item, RuntimeException>, Iterable<Item> {


	@Narrowing
	override fun getIterator() : Series<Item>


	@Throws(Empty::class)
	@JvmDefault
	fun safeGetFirst() : Item {
		try {
			return iterator.notNull
		} catch (end: End) {
			throw EMPTY
		}
	}

	@JvmDefault
	val first : Item get() = safeGetFirst()


	@JvmDefault
	override fun iterator(): Iterator<Item> {
		val iterator = iterator
		return object : Iterator<Item> {
			var hasNext: Boolean = false
			var next: Item? = null
			init {
				try {
					next = iterator.get()
					hasNext = true
				} catch (end: End) {
					next = null
					hasNext = false
				}

			}
			override fun hasNext(): Boolean {
				return hasNext
			}
			override fun next() : Item {
				val next = this.next
				try {
					this.next = iterator.get()
					this.hasNext = true
				} catch (end: End) {
					this.next = null
					this.hasNext = false
				}
				@Suppress("UNCHECKED_CAST")
				return next as Item
			}
		}

	}


	open class FromArray<Item>(private val array: Array<out Item>) : Sequence<Item> {

		override fun getIterator(): Series<Item> {
			return object : Series<Item> {
				var index = 0
				@Synchronized
				@Throws(End::class)
				override fun get(): Item? {
					return if (index < array.size)
						array[index++]
					else
						throw END
				}
			}
		}

	}


	class Default<Item> @SafeVarargs constructor(vararg array: Item) : FromArray<Item>(array) {

		constructor(iterable: Iterable<Item>) : this(*iterableToArray<Item>(iterable))

	}


}
