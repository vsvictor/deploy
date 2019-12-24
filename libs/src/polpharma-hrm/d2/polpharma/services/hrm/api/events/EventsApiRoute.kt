package d2.polpharma.services.hrm.api.events

import d2.modules.events.api.NotifyEventApiEndpoint
import d2.modules.events.model.Event
import d2.modules.events.model.StatisticsEvent
import d2.polpharma.services.hrm.model.*
import d2.polpharma.services.hrm.polpharmaHrmEventsStorage
import ic.network.http.BasicHttpAuth
import ic.struct.collection.FilterCollection
import ic.struct.list.List
import org.json.JSONObject


class EventsApiRoute : d2.modules.events.api.EventsApiRoute() {

	override val name = "events"

	override fun checkServerKey(serverKey: String) 	= d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth(auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override val storage = polpharmaHrmEventsStorage

	override fun getUserIds() = User.mapper.ids

	override fun initNotifyEventEndpoints() = List.Default<NotifyEventApiEndpoint<*>>(

		object : NotifyEventApiEndpoint<StatisticsEvent>() {

			override val name = "notify"

			override fun checkServerKey(serverKey: String) 	= d2.polpharma.services.checkPolpharmaServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

			override fun getStorage() = polpharmaHrmEventsStorage

			override fun createEvent (userId: String, requestJson: JSONObject) : StatisticsEvent {
				val event = StatisticsEvent(
					userId,
					requestJson.getString("type"),
					requestJson.optString("content", "")
				)
				if (event.type == EVENT_WIKI) {

					val wikiEventsCount = FilterCollection<Event>(
						Event.getMapper(polpharmaHrmEventsStorage).items
					) {
						if (it is StatisticsEvent) {
							it.type == EVENT_WIKI
						} else false
					}.count

					if (wikiEventsCount >= 100 - 1) pushAchievement(userId, ACHIEVEMENT_WIKI_100, false, Notification.CATEGORY_WIKI) else
						if (wikiEventsCount >= 30 - 1) 	pushAchievement(userId, ACHIEVEMENT_WIKI_30, false, Notification.CATEGORY_WIKI)
						else							pushAchievement(userId, ACHIEVEMENT_WIKI_1, false, Notification.CATEGORY_WIKI)

				}
				return event
			}

		}

	)

}