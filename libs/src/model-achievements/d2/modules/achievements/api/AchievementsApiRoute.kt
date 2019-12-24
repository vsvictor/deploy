package d2.modules.achievements.api


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.Storage
import ic.struct.collection.Collection
import ic.struct.list.List
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import ic.throwables.WrongValue


abstract class AchievementsApiRoute : FolderHttpRoute() {


	@Throws(WrongValue::class, AccessDenied::class)
	abstract fun checkServerKey (serverKey: String)

	@Throws(NotExists::class, WrongValue::class, AccessDenied::class)
	abstract fun checkUserAuth (auth: BasicHttpAuth)


	abstract val storage : Storage

	abstract val userIds : Collection<String>


	abstract fun notifyAchievement (userId: String, achievementName: String)


	override val children = List.Default<HttpRoute>(

		object : IncrementAchievementCounterApiEndpoint() {
			override fun checkServerKey	(serverKey: String) = this@AchievementsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@AchievementsApiRoute.checkUserAuth(auth)
			override val storage get() 						= this@AchievementsApiRoute.storage
			override fun notifyAchievement(userId: String, achievementName: String)
				= this@AchievementsApiRoute.notifyAchievement(userId, achievementName)
			;
		},

		object : PushAchievementApiEndpoint() {
			override fun checkServerKey	(serverKey: String) = this@AchievementsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@AchievementsApiRoute.checkUserAuth(auth)
			override val storage get() 						= this@AchievementsApiRoute.storage
		},

		object : GetAchievementsCountByTypeApiEndpoint() {
			override val userIds get() 						= this@AchievementsApiRoute.userIds
			override fun checkServerKey	(serverKey: String) = this@AchievementsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@AchievementsApiRoute.checkUserAuth(auth)
			override val storage get() 						= this@AchievementsApiRoute.storage
		}

	)


}