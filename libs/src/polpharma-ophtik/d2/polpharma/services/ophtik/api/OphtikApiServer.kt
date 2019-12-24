package d2.polpharma.services.ophtik.api


import d2.modules.scenarios.admin.ScenariosAdminRoute
import d2.modules.scenarios.api.ScenariosApiRoute
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.*
import d2.polpharma.services.ophtik.api.achievements.OphtikAchievementsApiRoute
import d2.polpharma.services.ophtik.api.survey.OphtikSurveyApiRoute
import d2.polpharma.services.ophtik.api.users.UsersApiRoute
import d2.polpharma.services.ophtik.model.User
import d2.polpharma.services.ophtik.ophtikDomainName
import d2.polpharma.services.ophtik.ophtikEducationStorage
import d2.polpharma.services.ophtik.ophtikSurveyStorage
import ic.Assets.Companion.resources
import ic.json.JsonArrays.toJsonArray
import ic.network.http.*
import ic.stream.ByteSequence
import ic.struct.list.List
import ic.struct.map.CountableMap
import ic.struct.set.CountableSet
import ic.text.Charset
import ic.throwables.*
import ic.tier
import org.json.JSONObject


class OphtikApiServer : HttpsServer() {

	override fun getDomainName (request: HttpRequest?) = ophtikDomainName
	override fun getDomainNames() = CountableSet.Default<String>(ophtikDomainName)
	override fun getCertEmail() = polpharmaCertEmail

	override fun getFallbackHttpPort() = 8192

	override fun toAllowCors() = true

	override fun initRoute() : HttpRoute = FolderHttpRoute.Final(

		UsersApiRoute(),

		OphtikEventsApiRoute(),
		OphtikQuestionsApiRoute(),
		OphtikAchievementsApiRoute(),

		object : ScenariosApiRoute() {
			override val name = "education"
			override fun getBearer() = polpharmaServerKey
			override fun getUserIds() = User.mapper.ids
			override fun getStorage() = ophtikEducationStorage
			override fun getUserRoleById (userId: String): String = User.mapper.getItem(userId).role
			override fun onScenarioFinish(userId: String, subjectId: String, scenarioId: String, firstTime: Boolean, currentTimeScore: Int) {}
			override fun checkServerKey(serverKey: String)  = checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
			override fun notifyScenario (subjectId: String, scenarioId: String) {
				val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
				val requestJson = JSONObject()
				requestJson.put("subjectId", subjectId)
				requestJson.put("scenarioId", scenarioId)
				requestJson.put("title", scenario.title)
				requestJson.put("roles", toJsonArray<String, String>(scenario.roles) { role -> role })
				if (tier == "stage") {
					HttpClient.request(HttpRequest(
						"https://www.corezoid.com/api/1/json/public/549341/54c0917af9985fe0de173be406ac2ace2608649f", "POST",
						Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
					))
				}
			}
		},

		OphtikSurveyApiRoute(),

		FolderHttpRoute.Final(
			"admin",
			List.Default<HttpRoute>(
				object : ScenariosAdminRoute() {
					override val name = "education"
					override fun getPassword() = polpharmaSuperadminPassword
					override fun getStorage() = ophtikEducationStorage
					override fun getUserIds() = User.mapper.ids
					override fun notifyScenario (subjectId: String, scenarioId: String) {
						val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
						val requestJson = JSONObject()
						requestJson.put("subjectId", subjectId)
						requestJson.put("scenarioId", scenarioId)
						requestJson.put("title", scenario.title)
						requestJson.put("roles", toJsonArray<String, String>(scenario.roles) { role -> role })
						if (tier == "stage") {
							HttpClient.request(HttpRequest(
								"https://www.corezoid.com/api/1/json/public/549341/54c0917af9985fe0de173be406ac2ace2608649f", "POST",
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
					override fun getImagesPageUrl(): String {
						return "https://images.bd-polpharma.com.ua/upload/adminka"
					}
					override fun checkUserAuth(auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)
				},
				object : ScenariosAdminRoute() {
					override val name: String get() = "survey"
					override fun getPassword() = polpharmaSuperadminPassword
					override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
					override fun getStorage() = ophtikSurveyStorage
					override fun getUserIds() = User.mapper.ids
					override fun notifyScenario(subjectId: String, scenarioId: String) {
						val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
						val requestJson = JSONObject()
						requestJson.put("subjectId", subjectId)
						requestJson.put("scenarioId", scenarioId)
						requestJson.put("title", scenario!!.title)
						requestJson.put("roles", toJsonArray(scenario.roles) { role -> role })
						if (tier == "stage") {
							try {
								HttpClient.request(HttpRequest(
									"https://www.corezoid.com/api/1/json/public/538465/6e450f688ec59d3249e2007d8b8e2bb705b0c6c4", "POST",
									Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
								))
							} catch (e: IOException) {
								throw IOException.Runtime(e)
							} catch (e: HttpException) {
								throw HttpException.Runtime(e)
							}
						} else if (tier == "prod") {
							try {
								HttpClient.request(HttpRequest(
									"https://www.corezoid.com/api/1/json/public/540038/8d719a98e84a87a61f5c31935257745bb41ce479", "POST",
									Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
								))
							} catch (e: IOException) {
								throw IOException.Runtime(e)
							} catch (e: HttpException) {
								throw HttpException.Runtime(e)
							}
						}
					}
					override fun uploadImage(image: ByteSequence, name: String): String {
						try {
							HttpClient.request(HttpRequest(
								"https://images.bd-polpharma.com.ua/upload/api/upload-image?name=" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png", "POST",
								CountableMap.Default(
									"Authorization", "Bearer 1e7082351284c8af"
								),
								image
							))
							return "https://images.bd-polpharma.com.ua/upload/" + Charset.DEFAULT_HTTP.encodeUrl(name) + ".png"
						} catch (e: IOException) {
							throw IOException.Runtime(e)
						} catch (e: HttpException) {
							throw HttpException.Runtime(e)
						}
					}
					override fun getImagesPageUrl(): String {
						return "https://images.bd-polpharma.com.ua/upload/adminka"
					}
				}
			)
		),

		object : StorageHttpRoute() {
			override fun getStorage(head: String) = resources
		}

	)

}