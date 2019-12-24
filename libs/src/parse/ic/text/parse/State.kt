package ic.text.parse


abstract class State<Expression> {


	abstract fun next(char : Char) : State<Expression>

	abstract fun finalize() : Expression?


}