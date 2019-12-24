package ic.text


import ic.annotations.*
import ic.interfaces.countable.Countable
import ic.interfaces.stringifiable.Stringifiable
import ic.struct.sequence.Sequence
import ic.throwables.*


interface Text : Sequence<Char>, Countable, Stringifiable {


	@Narrowing
	override fun getIterator(): CharInput


	fun equals(string: String): Boolean

	fun startsWith(subtext: Text): Boolean

	fun startsWith(string: String): Boolean

	fun endsWith(subtext: Text): Boolean

	fun endsWith(string: String): Boolean

	operator fun contains(subtext: Text): Boolean

	operator fun contains(string: String): Boolean

	operator fun contains(char : Char): Boolean

	override val count: Int get() {

		var counter = 0
		val iterator = iterator
		try {
			while (true) {
				iterator.get()
				counter++
			}
		} catch (end: End) {
			return counter
		}

	}

	val length : Int get() = count

	val asString : String get() {
		val series = iterator
		val stringBuilder = StringBuilder()
		try {
			while (true) stringBuilder.append(series.char)
		} catch (end: End) {}
		return stringBuilder.toString()
	}

	fun asString() : String = asString

	@JvmDefault
	override val stringValue : String get() = asString


	// Subclasses:

	abstract class BaseText : Text {

		@ToOverride override fun toString() = stringValue

		override fun equals(other: Any?): Boolean {
			if (other is Text) {
				return stringValue == other.stringValue
			} else {
				return false
			}
		}

		override fun hashCode(): Int {
			return stringValue.hashCode()
		}

		override fun equals(string: String): Boolean {
			return stringValue == string
		}

		override fun startsWith(subtext: Text): Boolean {
			return stringValue.startsWith(subtext.stringValue)
		}

		@Overload
		override fun startsWith(string: String): Boolean {
			return startsWith(FromString(string))
		}

		override fun endsWith(subtext: Text): Boolean {
			return stringValue.endsWith(subtext.stringValue)
		}

		@Overload
		override fun endsWith(string: String): Boolean {
			return endsWith(Text.FromString(string))
		}

		override fun contains(subtext: Text): Boolean {
			return stringValue.contains(subtext.stringValue)
		}

		@Overload
		override fun contains(string: String): Boolean {
			return contains(Text.FromString(string))
		}

		@Overload
		override fun contains(char: Char): Boolean {
			return contains(char.toString())
		}

	}

	open class FromString(private val string: String) : BaseText() {
		override fun getIterator(): CharInput {
			return object : CharInput.BaseCharInput() {
				private var index = 0
				@Synchronized
				@Throws(End::class)
				override fun getChar(): Char {
					if (index >= string.length) throw End()
					return string.get(index++)
				}

				@Synchronized
				@Throws(End::class)
				override fun skip(amount: Int) {
					assert(amount >= 0)
					index += amount
					if (index > string.length) throw End()
				}
			}
		}

		override val stringValue = string;

	}


	class FromCharInput(input: CharInput) : BaseText() {

		private var input : CharInput?

		init {
			this.input = input
		}

		@CallOnce("for each FromCharInput instance")
		@Synchronized
		@Throws(CalledTwiceTwice.Runtime::class)
		override fun getIterator(): CharInput {
			if (input == null) throw CalledTwiceTwice.Runtime(
				"Text.FromCharInput.getIterator() has been called twice twice."
			)
			val iterator = this.input
			this.input = null
			return iterator!!
		}

	}

	class Default (string : String) : FromString (string) {

		constructor (text: Text) : this (text.stringValue)

	}


	companion object {

		@Necessary
		@JvmField val SERIALIZABLE_CLASS_TO_INSTANTIATE: Class<*> = FromString::class.java

		@JvmField val EMPTY : Text = Text.FromString("")

	}


}
