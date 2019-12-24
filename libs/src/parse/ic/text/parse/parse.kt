package ic.text.parse


import ic.loop
import ic.text.CharInput
import ic.text.TextBuffer
import ic.throwables.Break.BREAK
import ic.throwables.End
import ic.throwables.UnableToParse


fun <Expression> parse (

	input : CharInput,

	initialState : State<Expression>

) : Expression? {

	var state = initialState
	var char = ' '
	var currentLineIndex = 0
	val currentLine = TextBuffer()
	val currentLineSpace = TextBuffer()

	try {

		try {

			loop {

				char = input.char

				if (char != '\n') {
					currentLine.put(char)
				}

				state = state.next(char)

				//System.out.println("PARSE char: '$char', state: $state")

				if (char == '\n') {
					currentLine.empty()
					currentLineIndex++
					currentLineSpace.empty()
				} else {
					currentLineSpace.put(
						if (char == '\t') 	'\t'
						else				' '
					)
				}

			}

		} catch (end : End) {}

		return state.finalize()

	} catch (thrown : Throwable) {

		try {
			loop {
				val c = input.char
				if (c == '\n') throw BREAK
				currentLine.put(c)
			}
		} catch (end : End) {}

		throw UnableToParse(
			(
				"\n" +
				"\n" +
				if (thrown.message == null) "" else {
					"${ thrown.message }\n" +
					"\n"
				} +
				"line ${ currentLineIndex + 1 }:\n" +
				"\n" +
				"$currentLine\n" +
				"$currentLineSpace^\n" +
				"\n" +
				"state: $state\n" +
				"char: '$char'\n"
			),
			thrown
		)

	}

}