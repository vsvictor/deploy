package d2.modules.points.api


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.Storage
import ic.struct.collection.Collection
import ic.struct.list.List


abstract class PointsApiRoute : FolderHttpRoute() {

	abstract fun checkServerKey (serverKey: String)
	abstract fun checkUserAuth (auth: BasicHttpAuth)

	abstract val storage: Storage
	abstract val userIds : Collection<String>

	override val children = List.Default<HttpRoute>(

		object : GetPointsApiEndpoint() {
			override val storage get() = this@PointsApiRoute.storage
			override fun checkServerKey(serverKey: String) = this@PointsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@PointsApiRoute.checkUserAuth(auth)
		},

		object : AddPointsApiEndpoint() {
			override val storage get() = this@PointsApiRoute.storage
			override fun checkServerKey(serverKey: String) = this@PointsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@PointsApiRoute.checkUserAuth(auth)
		},

		object : PointsDistributionApiEndpoint() {
			override val storage get() = this@PointsApiRoute.storage
			override val userIds get() = this@PointsApiRoute.userIds
			override fun checkServerKey(serverKey: String) = this@PointsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@PointsApiRoute.checkUserAuth(auth)
		}

	)

}