package ic.system.bash


import ic.struct.collection.Collection
import ic.text.JoinText
import ic.text.ReplaceText
import ic.text.Text


fun singleQuote(text: Text): Text {

	return JoinText(
		Text.FromString("$'"),
		ReplaceText(
			text,
			Collection.Default(
				Pair("\\", "\\\\"),
				Pair("\n", "\\n"),
				Pair("'", "\\'")
			)
		),
		Text.FromString("'")
	)

}


fun singleQuote(string: String): String {
	return singleQuote(Text.FromString(string)).toString()
}

fun scriptToCommand(text: Text): String {
	return "bash -c " + singleQuote(text)
}

fun scriptPathToCommand(path: String): String {
	return "bash $path"
}
