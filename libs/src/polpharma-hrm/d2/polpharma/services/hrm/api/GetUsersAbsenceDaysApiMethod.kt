package d2.polpharma.services.hrm.api


import d2.modules.events.model.Event
import d2.polpharma.services.hrm.model.User
import d2.polpharma.services.hrm.polpharmaHrmEventsStorage
import ic.date.Millis.dateToMillis
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.*
import ic.struct.order.ConvertOrder
import ic.struct.order.Order.LONG_ORDER
import ic.struct.order.OrderedCountableSet
import ic.struct.order.ReverseOrder
import ic.throwables.AccessDenied.ACCESS_DENIED
import org.json.JSONObject
import java.lang.System.currentTimeMillis


class GetUsersAbsenceDaysApiMethod : ProtectedHttpEndpoint() {


	override val name = "get_users_absence_days"

	override fun checkServerKey (serverKey: String) { throw ACCESS_DENIED }

	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)


	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject()

		responseJson.put("users", toJsonArray(User.mapper.ids) { userId ->

			val now = currentTimeMillis()

			val events = Event.byUser(polpharmaHrmEventsStorage, userId)

			val absenceDays = if (events.isEmpty) {
				0
			} else {
				(
					now - dateToMillis(
						OrderedCountableSet.Default<Event>(
							ReverseOrder(
								ConvertOrder<Event, Long>(LONG_ORDER) { event -> dateToMillis(event.date) }
							),
							events
						).first.date
					)
				) / (1000L * 60 * 60 * 24)
			}

			val userAbsenceTimeJson = JSONObject()

			userAbsenceTimeJson.put("userId", userId)
			userAbsenceTimeJson.put("absenceDays", absenceDays)

			userAbsenceTimeJson

		})

		return object : JsonResponse() {
			override val json = responseJson
		}

	}


}