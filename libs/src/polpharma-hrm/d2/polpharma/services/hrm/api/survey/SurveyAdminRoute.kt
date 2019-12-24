package d2.polpharma.services.hrm.api.survey


import d2.modules.scenarios.admin.ScenariosAdminRoute
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.hrm.model.User
import d2.polpharma.services.hrm.polpharmaHrmSurveyStorage
import d2.polpharma.services.polpharmaSuperadminPassword
import ic.json.JsonArrays.toJsonArray
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpClient
import ic.network.http.HttpRequest
import ic.stream.ByteSequence
import ic.struct.map.CountableMap
import ic.text.Charset
import org.json.JSONObject


class SurveyAdminRoute : ScenariosAdminRoute() {

	override val name = "survey"

	override fun getPassword() = polpharmaSuperadminPassword

	override fun getStorage() = polpharmaHrmSurveyStorage

	override fun getUserIds() = User.mapper.ids

	override fun notifyScenario(subjectId: String, scenarioId: String) {
		val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
		val requestJson = JSONObject()
		requestJson.put("subjectId", subjectId)
		requestJson.put("scenarioId", scenarioId)
		requestJson.put("title", scenario.title)
		requestJson.put("roles", toJsonArray(scenario.roles) { role -> role })
		HttpClient.request(HttpRequest(
			"https://www.corezoid.com/api/1/json/public/515730/ce387f4cff8b49a0ccf9080af1e2c407c422b3a0", "POST",
			Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
		))
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

	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)


}