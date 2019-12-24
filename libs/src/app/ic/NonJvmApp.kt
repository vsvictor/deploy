package ic


import ic.text.Text


internal abstract class NonJvmApp(args: Text) : App(args) {


	abstract fun getMain() : String


	override fun implementRun(args: Text) {

		importSource(getMain(), true)

	}

	override fun implementStop() {

	}

}