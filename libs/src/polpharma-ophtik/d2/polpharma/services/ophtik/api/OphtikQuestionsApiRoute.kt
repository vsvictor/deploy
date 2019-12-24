package d2.polpharma.services.ophtik.api


import d2.modules.questions.api.QuestionsApiRoute
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.ophtik.model.User
import d2.polpharma.services.ophtik.ophtikQuestionsStorage
import ic.network.http.BasicHttpAuth
import ic.throwables.NotExists
import ic.throwables.NotExists.NOT_EXISTS


class OphtikQuestionsApiRoute : QuestionsApiRoute() { override val name = "questions"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override fun getStorage() = ophtikQuestionsStorage

	override fun getAnswererIdByCategory (category: String?) = null

	@Throws(NotExists::class)
	override fun userToString (userId: String) : String? {
		val user = User.mapper[userId]?: throw NOT_EXISTS
		return "${ user.surname } ${ user.name }"
	}

}