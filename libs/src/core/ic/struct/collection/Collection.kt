package ic.struct.collection


import ic.interfaces.action.Action1
import java.util.function.Consumer

import ic.interfaces.countable.Countable
import ic.interfaces.condition.Condition1
import ic.math.randomInt
import ic.throwables.Break
import ic.throwables.Empty
import ic.throwables.NotExists

import ic.throwables.Empty.EMPTY
import ic.throwables.NotExists.NOT_EXISTS
import ic.throwables.WrongType
import ic.util.Arrays.createArray
import ic.util.Arrays.iterableToArray


/**
 * Collection get a data structure that implements forEach.
 * Collections are finite and countable.
 * In general Collection is unordered, however its implementations can be ordered.
 * In general case forEach method does not guarantee same invocation order.
 */
interface Collection<Item> : Iterable<Item>, Countable {

	fun implementForEach(action: Action1<Item>)

	@JvmDefault
	override val count: Int get() {
		class Counter : (Item) -> Unit {
			var count = 0
			override fun invoke (item: Item) {
				count++
			}
		}
		val counter = Counter()
		forEach(counter)
		return counter.count
	}

	@JvmDefault
	val any: Item get() {
		try {
			return safeGetAny()
		} catch (empty: Empty) {
			throw Empty.Runtime()
		}
	}

	@JvmDefault
	override fun iterator(): Iterator<Item> {
		val javaList = java.util.ArrayList<Item>()
		try {
			implementForEach(Action1.Final { javaList.add(it) })
		} catch (breakThrowable: Break) {}
		return javaList.iterator()
	}

	@JvmDefault
	override fun forEach(action: Consumer<in Item>) {
		try {
			implementForEach(
				object : Action1<Item> {
					override fun run(item : Item) {
						action.accept(item)
					}
				}
			)
		} catch (breakThrowable: Break) {}
	}

	@JvmDefault
	fun toJavaList(): List<Item> {
		val javaList = ArrayList<Item>()
		forEach { javaList.add(it) }
		return javaList
	}


	private class Found internal constructor(internal val item: Any?) : RuntimeException() {
		override fun fillInStackTrace() = null
	}

	@JvmDefault
	@Throws(NotExists::class)
	fun findOrThrow (searcher: (Item) -> Boolean) : Item {
		try {
			forEach { item ->
				if (searcher(item)) throw Found(item)
			}
			throw NOT_EXISTS
		} catch (found: Found) {
			@Suppress("UNCHECKED_CAST")
			return found.item as Item
		}
	}


	@JvmDefault
	@Throws(NotExists.Runtime::class)
	fun find(searcher: (Item) -> Boolean): Item {
		try {
			return findOrThrow(searcher)
		} catch (notExists: NotExists) {
			throw NotExists.Runtime()
		}

	}

	@JvmDefault
	fun contains(searcher: (Item) -> Boolean): Boolean {
		try {
			findOrThrow(searcher)
			return true
		} catch (notExists: NotExists) {
			return false
		}

	}

	@Throws(Empty::class)
	@JvmDefault
	fun safeGetAny(): Item {
		try {
			return findOrThrow(Condition1.True())
		} catch (notExists: NotExists) {
			throw EMPTY
		}
	}

	@JvmDefault
	fun toArray(): Array<Item> {
		val javaList = java.util.ArrayList<Item>()
		forEach(Consumer { javaList.add(it) })
		@Suppress("UNCHECKED_CAST")
		return javaList.toArray() as Array<Item>
	}

	@JvmDefault
	fun <Type> toArray(itemClass: Class<Type>): Array<Item> {
		val javaList = java.util.ArrayList<Item>()
		forEach(Consumer {
			if (it != null) {
				if (!itemClass.isInstance(it)) throw WrongType.Runtime(itemClass, it)
			}
			javaList.add(it)
		})
		@Suppress("UNCHECKED_CAST")
		return javaList.toArray(createArray(itemClass, javaList.size)) as Array<Item>
	}


	@JvmDefault
	val random : Item get() {
		val javaList = java.util.ArrayList<Item>()
		forEach(Consumer { javaList.add(it) })
		return javaList[randomInt(javaList.size)]
	}

	@JvmDefault
	fun doForEach(action: (Item) -> Unit) {
		try {
			implementForEach(
					object : Action1<Item> {
						override fun run(item : Item) {
							action(item)
						}
					}
			)
		} catch (breakThrowable: Break) {}
	}

	class FromIterable<Item>(private val iterable: Iterable<Item>) : Collection<Item> {

		override fun iterator(): Iterator<Item> {
			return iterable.iterator()
		}

		@Throws(Break::class)
		override fun implementForEach(action: Action1<Item>) {
			for (item in iterable) action(item)
		}

	}


	open class FromArray<Item>(private val items: Array<out Item>) : Collection<Item> {

		override val count: Int
			@Synchronized get() = items.size

		@Throws(Break::class)
		override fun implementForEach(action: Action1<Item>) {
			for (item in items) {
				action(item)
			}
		}

	}


	class Default<Item> constructor (vararg items : Item) : FromArray<Item> (items) {

		@Suppress("UNCHECKED_CAST")
		constructor(items: Iterable<Item>) : this(*iterableToArray<Any>(items, Any::class.java) as Array<out Item>)

	}


}
