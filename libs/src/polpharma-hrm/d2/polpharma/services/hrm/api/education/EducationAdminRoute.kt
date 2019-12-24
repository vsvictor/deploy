package d2.polpharma.services.hrm.api.education


import d2.modules.scenarios.admin.ScenariosAdminRoute
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.hrm.model.User
import d2.polpharma.services.hrm.polpharmaHrmEducationStorage
import d2.polpharma.services.polpharmaSuperadminPassword
import ic.json.JsonArrays.toJsonArray
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpClient
import ic.network.http.HttpRequest
import ic.stream.ByteSequence
import ic.struct.map.CountableMap
import ic.text.Charset
import ic.tier
import org.json.JSONObject


class EducationAdminRoute : ScenariosAdminRoute() {

	override val name = "education"

	override fun getPassword() = polpharmaSuperadminPassword

	override fun getStorage() = polpharmaHrmEducationStorage

	override fun getUserIds() = User.mapper.ids

	override fun notifyScenario(subjectId: String, scenarioId: String) {
		if (tier == "prod") {
			val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
			val requestJson = JSONObject()
			requestJson.put("subjectId", subjectId)
			requestJson.put("scenarioId", scenarioId)
			requestJson.put("title", scenario.title)
			requestJson.put("roles", toJsonArray<String, String>(scenario.roles) { role -> role })
			HttpClient.request(HttpRequest(
				"https://www.corezoid.com/api/1/json/public/583279/3e6a6714e2c36b65d47602394940c02946a71ff2", "POST",
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			))
		}
	}

	override fun uploadImage(image: ByteSequence, name: String): String {
		HttpClient.request(HttpRequest(
			"https://images.bd-polpharma.com.ua/upload/api/upload-image?name=" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png", "POST",
			CountableMap.Default(
				"Authorization", "Bearer 1e7082351284c8af"
			),
			image
		))
		return "https://images.bd-polpharma.com.ua/upload/" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png"
	}

	override fun getImagesPageUrl() = "https://images.bd-polpharma.com.ua/upload/adminka"

	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)


}