package ic.text


import ic.interfaces.stringifiable.Stringifiable


fun stringValue (obj : Any?) : String{

	if (obj == null) return "null"

	if (obj is Stringifiable) return obj.stringValue

	else return "${ obj.javaClass.name }: $obj"

}