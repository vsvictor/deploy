package ic.interfaces.stringifiable


import ic.text.Text


interface Stringifiable {

	val stringValue : String

	@JvmDefault val textValue : Text get() = Text.FromString(stringValue)

}