package d2.modules.events.api


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.Storage
import ic.struct.list.JoinList
import ic.struct.list.List
import ic.struct.set.CountableSet
import ic.throwables.NotExists
import ic.throwables.WrongValue


abstract class EventsApiRoute : FolderHttpRoute() {


	open fun initNotifyEventEndpoints() : List<NotifyEventApiEndpoint<*>> = List.Default()

	@Throws(WrongValue::class)
	protected abstract fun checkServerKey(serverKey: String)

	@Throws(WrongValue::class, NotExists::class)
	protected abstract fun checkUserAuth (auth: BasicHttpAuth)

	protected abstract val storage : Storage

	protected abstract fun getUserIds() : CountableSet<String>


	override val children = JoinList<HttpRoute>(
		List.Default(
			object : NotifyStatisticsEventApiEndpoint() {
				override fun checkServerKey(serverKey: String) 	= this@EventsApiRoute.checkServerKey(serverKey)
				override fun getStorage() 						= this@EventsApiRoute.storage
			},
			object : GetStatisticsEventsCountByTypesApiMethod() {
				override fun getStorage() : Storage = this@EventsApiRoute.storage
			},
			object : ListEventsApiEndpoint() {
				override fun checkUserAuth(auth: BasicHttpAuth) = this@EventsApiRoute.checkUserAuth(auth)
				override fun checkServerKey(serverKey: String) 	= this@EventsApiRoute.checkServerKey(serverKey)
				override fun getStorage() 						= this@EventsApiRoute.storage
				override fun getUserIds() 						= this@EventsApiRoute.getUserIds()
			}
		),
		initNotifyEventEndpoints()
	)


}