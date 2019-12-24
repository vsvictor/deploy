package ic.text

class AllCapsText (string : String) : Text.FromString(string.toUpperCase()) {

	constructor (text: Text) : this (text.stringValue)

}