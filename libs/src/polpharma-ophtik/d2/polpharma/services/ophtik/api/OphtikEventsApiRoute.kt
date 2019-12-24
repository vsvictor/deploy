package d2.polpharma.services.ophtik.api


import d2.modules.events.api.EventsApiRoute
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.ophtik.model.User
import d2.polpharma.services.ophtik.ophtikEventsStorage
import ic.network.http.BasicHttpAuth


class OphtikEventsApiRoute : EventsApiRoute() { override val name = "events"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = checkPolpharmaSuperadminAuth(auth)

	override val storage get() = ophtikEventsStorage

	override fun getUserIds() = User.mapper.ids

}