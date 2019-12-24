package d2.modules.docs.api


import ic.text.Text


abstract class ApiRequestDoc {

	abstract val name : String

	abstract val url : String

	abstract val httpMethod : String

	abstract val requestExample : Text?

	abstract val responseExample : Text

}