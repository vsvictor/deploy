package d2.polpharma.services.hrm.api.questions


import d2.polpharma.services.hrm.model.User
import d2.polpharma.services.hrm.polpharmaHrmQuestionsStorage
import ic.network.http.BasicHttpAuth
import ic.throwables.NotExists


class QuestionsApiRoute : d2.modules.questions.api.QuestionsApiRoute() { override val name = "questions"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun getStorage() = polpharmaHrmQuestionsStorage

	override fun getAnswererIdByCategory (category: String?) = "00000014.a5534d3a1caf021d"

	override fun userToString (userId: String) : String {
		try {
			val user = User.mapper.getItemOrThrow(userId)
			return "${ user.surname } ${ user.name }"
		} catch (notExists : NotExists) {
			return "null"
		}
	}

}