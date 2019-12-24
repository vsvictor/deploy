package ic.text


import ic.throwables.End
import ic.throwables.End.END


class RepeatText : Text.BaseText {


	private val pattern : Text

	private val times : Int


	override fun getIterator() : CharInput {

		return object : CharInput.BaseCharInput() {

			var patternIterator : CharInput = pattern.iterator
			var counter = 0

			@Synchronized
			override fun getChar() : Char {
				while (true) {
					if (counter >= times) throw END
					try {
						return patternIterator.char
					} catch (end : End) {
						patternIterator = pattern.iterator
						counter++
					}
				}
			}

		}

	}


	constructor(
		pattern : Text,
		times : Int
	) {
		this.pattern = pattern
		this.times = times
	}

	constructor(
		pattern : String,
		times : Int
	) : this (
		Text.FromString(pattern),
		times
	)

	constructor(
		pattern : Char,
		times : Int
	) : this (
		pattern.toString(),
		times
	)


}