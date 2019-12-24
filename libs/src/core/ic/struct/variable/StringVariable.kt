package ic.struct.variable


interface StringVariable : Variable<String> {


	class Constant (override val value : String) : StringVariable


}
