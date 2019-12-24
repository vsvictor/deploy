package d2.polpharma.services.hrm.api.education


import d2.modules.scenarios.api.ScenariosApiRoute
import d2.modules.scenarios.model.History.getScenariosPassedCount
import d2.modules.scenarios.model.History.getSubjectsPassedCount
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.hrm.model.*
import d2.polpharma.services.hrm.polpharmaHrmEducationStorage
import d2.polpharma.services.polpharmaServerKey
import ic.json.JsonArrays
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpClient
import ic.network.http.HttpRequest
import ic.text.Charset
import ic.tier
import org.json.JSONObject


class EducationApiRoute : ScenariosApiRoute() {


	override fun checkServerKey(serverKey: String)  = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override val name = "education"

	override fun getStorage() = polpharmaHrmEducationStorage

	override fun getBearer() = polpharmaServerKey

	override fun getUserIds() = User.mapper.ids

	override fun getUserRoleById (id: String) = User.mapper.getOrThrow(id).role

	override fun notifyScenario(subjectId: String, scenarioId: String) {
		if (tier == "stage") {
			val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
			val requestJson = JSONObject()
			requestJson.put("subjectId", subjectId)
			requestJson.put("scenarioId", scenarioId)
			requestJson.put("title", scenario.title)
			requestJson.put("roles", JsonArrays.toJsonArray<String, String>(scenario.roles) { role -> role })
			HttpClient.request(HttpRequest(
				"https://www.corezoid.com/api/1/json/public/583279/3e6a6714e2c36b65d47602394940c02946a71ff2", "POST",
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			))
		}
	}

	override fun onScenarioFinish (userId: String, subjectId: String, scenarioId: String, firstTime: Boolean, currentTimeScore: Int) {
		if (currentTimeScore >= 100)
			pushAchievement(
				userId,
				ACHIEVEMENT_SCENARIO_PASSED_100,
				true,
				Notification.CATEGORY_EDUCATION
			)
		if (firstTime) {
			val subjectsPassedCount = getSubjectsPassedCount(storage, userId)
			if (subjectsPassedCount >= 7)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_7,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 6)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_6,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 5)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_5,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 4)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_4,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 3)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_3,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 2)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_2,
					false,
					Notification.CATEGORY_EDUCATION
				)
			else if (subjectsPassedCount >= 1)
				pushAchievement(
					userId,
					ACHIEVEMENT_SUBJECTS_PASSED_1,
					false,
					Notification.CATEGORY_EDUCATION
				)
			val scenariosPassedCount = getScenariosPassedCount(storage, userId)
			if (scenariosPassedCount >= 1)
				pushAchievement(
					userId,
					ACHIEVEMENT_SCENARIOS_PASSED_1,
					false,
					Notification.CATEGORY_EDUCATION
				)
		}
	}

}