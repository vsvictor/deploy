package d2.polpharma.services.prm


import d2.modules.admins.api.AdminsApiRoute
import d2.modules.coupons.api.CouponsApiRoute
import d2.modules.coupons.user.api.UserCouponsApiRoute
import d2.modules.events.api.EventsApiRoute
import d2.modules.events.api.NotifyEventApiEndpoint
import d2.modules.events.api.NotifyStatisticsEventApiEndpoint
import d2.modules.points.api.PointsApiRoute
import d2.modules.scenarios.api.ScenariosApiRoute
import d2.polpharma.services.prm.api.users.UsersApiRoute
import d2.polpharma.services.polpharmaServerKey
import d2.polpharma.services.polpharmaSuperadminPassword
import d2.polpharma.services.prm.model.User
import ic.network.http.*
import ic.storage.BindingStorage
import ic.storage.StreamStorage
import ic.stream.ByteSequence
import ic.struct.collection.Collection
import ic.struct.list.List
import ic.struct.map.CountableMap
import ic.struct.set.CountableSet
import ic.text.Charset
import ic.throwables.IOException
import ic.throwables.NotSupported.NOT_SUPPORTED
import d2.modules.scenarios.admin.ScenariosAdminRoute
import d2.modules.scenarios.model.Scenario
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.prm.api.EmailBoughtCouponApiMethod
import d2.polpharma.services.prm.api.arrangements.ArrangementsApiRoute
import d2.polpharma.services.prm.model.FishkiEvent
import ic.Assets.Companion.resources
import ic.http.proxy.ProxyRoute
import ic.json.JsonArrays.toJsonArray
import ic.storage.Storage
import ic.tier
import org.json.JSONObject


class PolpharmaPrmHttpsServer : HttpsServer() {


	override fun getDomainName (request : HttpRequest?) : String {
		if (tier == "dev") return "dev.prm.market-insights.com.ua"
		else throw NOT_SUPPORTED
	}

	override fun getDomainNames() : CountableSet<String> {
		if (tier == "dev") return CountableSet.Default<String>(
			"dev.prm.market-insights.com.ua"
		)
		else throw NOT_SUPPORTED
	}

	override fun getCertEmail () = "Polpharma.Bot@gmail.com"

	override fun toAllowCors() = true


	override fun initRoute () : HttpRoute { return FolderHttpRoute.Final (

		object : AdminsApiRoute() { override val name = "admins"
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
			override val storage get() = polpharmaPrmAdminsStorage
		},

		UsersApiRoute(),

		object : EventsApiRoute() { override val name = "events"
			override val storage = polpharmaPrmEventsStorage
			override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
			override fun getUserIds() = User.mapper.ids
			override fun initNotifyEventEndpoints() = List.Default<NotifyEventApiEndpoint<*>>(
				object : NotifyStatisticsEventApiEndpoint() {
					override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
					override fun getStorage() = polpharmaPrmEventsStorage
				},
				object : NotifyEventApiEndpoint<FishkiEvent>() { override val name get() = "notify_fishki"
					override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
					override fun getStorage() = polpharmaPrmEventsStorage
					override fun createEvent (userId: String, requestJson: JSONObject) = FishkiEvent(
						pointsAmount = requestJson.getInt("pointsAmount"),
						fishkiAmount = requestJson.getInt("fishkiAmount"),
						pointsRemaining = requestJson.getInt("pointsRemaining")
					)
				}
			)
		},
		object : CouponsApiRoute() { override val name = "coupons"
			override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
			override val storage get() = polpharmaPrmCouponsStorage
		},
		object : UserCouponsApiRoute() { override val name = "user-coupons"
			override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)
			override val storage get() = polpharmaPrmCouponsStorage
		},
		object : PointsApiRoute() {
			override val name = "points"
			override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)
			override val storage get() = polpharmaPrmPointsStorage
			override val userIds get() = User.mapper.ids
		},

		object : PointsApiRoute() {
			override val name = "fishki"
			override fun checkServerKey(serverKey: String) 	= checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)
			override val storage get() = polpharmaPrmFishkiStorage
			override val userIds get() = User.mapper.ids
		},

		ArrangementsApiRoute(),

		object : ScenariosApiRoute() {
			override fun getUserIds() = User.mapper.ids
			override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)
			override val name = "survey"
			override fun getStorage() = polpharmaPrmSurveyStorage
			override fun getBearer() = polpharmaServerKey
			override fun getUserRoleById (id: String?) = "USER"
			override fun onScenarioFinish(userId: String?, subjectId: String?, scenarioId: String?, firstTime: Boolean, currentTimeScore: Int) {}
			override fun notifyScenario(subjectId: String, scenarioId: String) {
				val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
				val requestJson = JSONObject()
				requestJson.put("subjectId", subjectId)
				requestJson.put("scenarioId", scenarioId)
				requestJson.put("title", scenario.title)
				requestJson.put("roles", toJsonArray<String, String>(scenario.roles) { role -> role })
				try {
					HttpClient.request(HttpRequest(
						"https://www.corezoid.com/api/1/json/public/544093/b445a07003dfa5e4ea7b87eaa082a0136ba03107", "POST",
						Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
					))
				} catch (e: IOException) {
					throw IOException.Runtime(e)
				} catch (e: HttpException) {
					throw HttpException.Runtime(e)
				}
			}
		},

		/*object : FolderHttpRoute() { override val name = "admin"

			override fun initChildren() = List.Default<HttpRoute>(

				object : ScenariosAdminRoute() {

					override val name = "survey"
					override fun getPassword(): String 			= polpharmaSuperadminPassword
					override fun getStorage(): BindingStorage 	= polpharmaPrmSurveyStorage
					override fun getUserIds(): Collection<String> = User.mapper.ids

					override fun notifyScenario(subjectId: String, scenarioId: String) {
						val scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId)
						val requestJson = JSONObject()
						requestJson.put("subjectId", subjectId)
						requestJson.put("scenarioId", scenarioId)
						requestJson.put("title", scenario.title)
						requestJson.put("roles", toJsonArray<String, String>(scenario.roles) { role -> role })
						try {
							HttpClient.request(HttpRequest(
								"https://www.corezoid.com/api/1/json/public/544093/b445a07003dfa5e4ea7b87eaa082a0136ba03107", "POST",
								Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
							))
						} catch (e: IOException) {
							throw IOException.Runtime(e)
						} catch (e: HttpException) {
							throw HttpException.Runtime(e)
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

					override fun getImagesPageUrl(): String = "https://images.bd-polpharma.com.ua/upload/adminka"

					override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

				}

			)

		},*/

		EmailBoughtCouponApiMethod(),

		object : StorageHttpRoute() {
			override fun getStorage(path: String): StreamStorage = resources
		},

		FolderHttpRoute.Final("proxy", List.Default<HttpRoute>(ProxyRoute()))

	) }


}