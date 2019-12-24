package ic


import ic.stream.ByteSequence
import ic.text.Charset
import ic.text.Text


abstract class TextLanguage : Language {


	abstract fun eval(script : Text, main : Boolean) : Any?


	fun eval (script: Text) : Any? {
		return eval(script, false)
	}


	fun eval (script : String) : Any? {
		return eval(Text.FromString(script), false)
	}


	override fun eval (file : ByteSequence, main : Boolean) : Any? {
		return eval(
			Charset.DEFAULT_UNIX.bytesToText(file),
			main
		)
	}


}