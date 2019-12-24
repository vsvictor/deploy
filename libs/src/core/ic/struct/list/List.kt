package ic.struct.list


import ic.annotations.*
import ic.interfaces.action.*
import ic.throwables.*
import ic.struct.VariableItem
import ic.struct.collection.Collection
import ic.struct.collection.FilterCollection
import ic.struct.sequence.Sequence
import ic.struct.sequence.Series
import ic.throwables.Empty
import ic.throwables.Empty.EMPTY
import ic.throwables.NotExists.NOT_EXISTS
import ic.util.Arrays
import java.util.function.Consumer


interface List<Item> : Collection<Item>, Sequence<Item> {


	val length : Int


	@JvmDefault
	override val count get() = length


	// Get by index:

	fun implementGet(@Valid @Positive index: Int): Item

	@JvmDefault
	@Throws(IndexOutOfBounds::class)
	fun safeGet(index: Int): Item {
		synchronized(this) {
			val length = count
			if (!isIndexValid(length, index)) throw IndexOutOfBounds(length, index)
			return implementGet(index)
		}
	}

	@JvmDefault
	operator fun get (index: Int) : Item {
		return safeGet(index)
	}


	@JvmDefault
	val lastOrThrow: Item
		@Throws(Empty::class)
		get() = synchronized(this) {
			val count = count
			if (count == 0) throw EMPTY
			return get(count - 1)
		}

	@JvmDefault
	val last : Item get() = lastOrThrow

	@JvmDefault
	fun forEach (action: (Item, Int) -> Unit) {
		val count = count
		try {
			for (i in 0 until count) action(get(i), i)
		} catch (breakThrowable: Break) {}
	}

	@JvmDefault
	override fun implementForEach (action: Action1<Item>) {
		val count = count
		try {
			for (i in 0 until count) action.run(get(i))
		} catch (breakThrowable: Break) {}
	}

	@JvmDefault
	override fun getIterator(): Series<Item> {
		return object : Series<Item> {
			protected var index = 0
			@Throws(End::class)
			override fun get(): Item? {
				synchronized(this@List) {
					try {
						if (index >= this@List.count) throw End()
						return this@List.implementGet(index)
					} finally {
						index++
					}
				}
			}
		}
	}


	// Find index:

	@JvmDefault
	@Throws(NotExists::class)
	fun findIndexOrThrow(searcher: (Item) -> Boolean): Int {
		val count = count
		for (i in 0 until count) {
			if (searcher(get(i))) return i
		}
		throw NOT_EXISTS
	}

	@JvmDefault
	@Throws(NotExists.Runtime::class)
	@Positive
	fun findIndex(searcher: (Item) -> Boolean): Int {
		try {
			return findIndexOrThrow(searcher)
		} catch (notExists: NotExists) {
			throw NotExists.Runtime(notExists)
		}

	}

	@JvmDefault
	override fun iterator() : Iterator<Item> {
		return object : Iterator<Item> {
			var i = 0
			var count = this@List.count
			override fun hasNext(): Boolean {
				return i < count
			}
			override fun next(): Item {
				return get(i++)
			}
		}
	}


	@JvmDefault
	fun isIndexValid(length: Int, index: Int): Boolean {
		if (index < 0) return false
		@Suppress("RedundantIf", "LiftReturnOrAssignment")
		if (index >= length) return false
		else return true
	}


	class FromArray<Item> (private val array: kotlin.Array<Item>) : List<Item> {

		override val length: Int get() = array.size

		override fun implementGet(index: Int): Item {
			return array[index]
		}

		override fun toArray() = array

	}


	class FromJavaList<Item>(private val javaList: MutableList<Item>) : List<Item> {

		override val length: Int get() = javaList.size

		override fun implementGet(index: Int): Item {
			return javaList.get(index)
		}

	}


	class Default<Item> : List<Item> {

		private val array : kotlin.Array<*>

		override val length: Int get() = array.size

		@Suppress("UNCHECKED_CAST")
		override fun implementGet (index: Int) : Item {
			val item = array[index]
			if (item is VariableItem<*>) {
				return item() as Item
			}
			else return item as Item
		}

		constructor(vararg items: Any) {
			array = Arrays.filter(items) { it !is Skip }
		}

		constructor(items: Iterable<Item>) {
			array = FilterCollection(Collection.FromIterable(items)) { it !is Skip }.toArray()
		}

		constructor(items: Collection<Item>) {
			array = FilterCollection(items) { it !is Skip }.toArray()
		}
		constructor(items: Collection<Item>, count: Int) {
			val array = arrayOfNulls<Any?>(count)
			var i = 0
			items.doForEach {
				if (i >= count)  throw Break()
				array[i++] = it
			}
			this.array = array
		}
	}


}
