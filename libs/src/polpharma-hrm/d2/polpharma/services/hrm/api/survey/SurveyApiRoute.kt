package d2.polpharma.services.hrm.api.survey


import d2.modules.scenarios.api.ScenariosApiRoute
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.hrm.model.*
import d2.polpharma.services.hrm.polpharmaHrmSurveyStorage
import d2.polpharma.services.polpharmaServerKey
import ic.json.JsonArrays
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpClient
import ic.network.http.HttpRequest
import ic.text.Charset
import org.json.JSONObject


class SurveyApiRoute : ScenariosApiRoute() {

	override fun checkServerKey (serverKey: String)  = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override val name = "survey"

	override fun getStorage() = polpharmaHrmSurveyStorage

	override fun getBearer() = polpharmaServerKey

	override fun getUserIds() = User.mapper.ids

	override fun getUserRoleById(id: String) = User.mapper.getItem(id).role

	override fun onScenarioFinish(userId: String, subjectId: String, scenarioId: String, firstTime: Boolean, currentTimeScore: Int) {}

	override fun notifyScenario(subjectId: String, scenarioId: String) {
		val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
		val requestJson = JSONObject()
		requestJson.put("subjectId", subjectId)
		requestJson.put("scenarioId", scenarioId)
		requestJson.put("title", scenario.title)
		requestJson.put("roles", JsonArrays.toJsonArray(scenario.roles) { role -> role })
		HttpClient.request(HttpRequest(
			"https://www.corezoid.com/api/1/json/public/515730/ce387f4cff8b49a0ccf9080af1e2c407c422b3a0", "POST",
			Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
		))
	}

}