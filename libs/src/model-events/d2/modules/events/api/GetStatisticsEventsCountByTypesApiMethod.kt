package d2.modules.events.api


import d2.modules.events.model.Event
import d2.modules.events.model.StatisticsEvent
import ic.network.SocketAddress
import ic.network.http.HttpEndpoint
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.storage.Storage
import ic.struct.map.EditableMap
import org.json.JSONObject


abstract class GetStatisticsEventsCountByTypesApiMethod : HttpEndpoint() {


	override val name = "get_statistics_events_count_by_types"

	protected abstract fun getStorage() : Storage


	override fun implementEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val eventsCountByTypes = EditableMap.Default<Int, String>(); run {
			Event.getMapper(getStorage()).items.forEach { event ->
				if (event is StatisticsEvent) {
					eventsCountByTypes.set(event.type, eventsCountByTypes.get(event.type) { 0 } + 1)
				}
			}
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		run {

			val eventsCountByTypeJson = JSONObject()

			eventsCountByTypes.keys.forEach { key ->
				eventsCountByTypeJson.put(key, eventsCountByTypes.getNotNull(key))
			}

			responseJson.put("eventsCountByTypes", eventsCountByTypeJson)

		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}


}