package d2.modules.events.api


import d2.modules.events.model.StatisticsEvent
import org.json.JSONObject


abstract class NotifyStatisticsEventApiEndpoint : NotifyEventApiEndpoint<StatisticsEvent>() {

	override val name = "notify"

	override fun createEvent(userId: String, requestJson: JSONObject): StatisticsEvent {
		return StatisticsEvent(
			userId,
			requestJson.getString("type"),
			requestJson.optString("content", "")
		)
	}

}