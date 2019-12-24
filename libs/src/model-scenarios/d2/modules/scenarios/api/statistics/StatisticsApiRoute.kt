package d2.modules.scenarios.api.statistics


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.BindingStorage
import ic.struct.collection.Collection
import ic.struct.list.List
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import ic.throwables.WrongValue


abstract class StatisticsApiRoute : FolderHttpRoute() { override val name = "statistics"

	@Throws(WrongValue::class, AccessDenied::class)
	protected abstract fun checkServerKey(serverKey: String)

	@Throws(NotExists::class, WrongValue::class, AccessDenied::class)
	protected abstract fun checkUserAuth(auth: BasicHttpAuth)

	protected abstract val storage: BindingStorage
	protected abstract val userIds : Collection<String>

	override val children = List.Default<HttpRoute>(

		object : HistoryStatisticsApiEndpoint() {
			override fun checkServerKey(serverKey: String) = this@StatisticsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@StatisticsApiRoute.checkUserAuth(auth)
			override val userIds get() = this@StatisticsApiRoute.userIds
			override val storage get() = this@StatisticsApiRoute.storage
		}

	)

}