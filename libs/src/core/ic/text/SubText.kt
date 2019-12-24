package ic.text

import ic.throwables.End
import ic.throwables.End.END


class SubText (val sourceText: Text, val start: Int, val end: Int) : Text.BaseText() {

	override fun getIterator() = object : CharInput.BaseCharInput() {

		val sourceTextIterator = sourceText.iterator

		init {
			try {
				sourceTextIterator.skip(start)
			} catch (end : End) {}
		}

		var index = start

		@Synchronized
		override fun getChar() : Char {
			if (index >= end) throw END
			val char = sourceTextIterator.getChar()
			index++
			return char
		}

	}

}