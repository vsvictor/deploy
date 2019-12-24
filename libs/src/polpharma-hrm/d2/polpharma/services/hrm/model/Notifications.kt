package d2.polpharma.services.hrm.model


import ic.network.http.HttpClient
import ic.network.http.HttpException
import ic.network.http.HttpRequest
import ic.struct.map.CountableMap
import ic.text.Charset
import ic.throwables.IOException
import org.json.JSONObject


fun pushNotification(userId: String, notification: Notification) {

	val requestJson = JSONObject()

	requestJson.put("userId", userId)

	run {
		val notificationJson = JSONObject()
		notificationJson.put("important", notification.important)
		notificationJson.put("category", notification.category)
		notificationJson.put("type", notification.type)
		notificationJson.put("content", notification.content)
		requestJson.put("notification", notificationJson)
	}

	try {
		HttpClient.request(
			HttpRequest(
				"https://www.corezoid.com/api/1/json/public/489278/40fe1e16e72ea400ba8d1e4014f9d88929dce61b", "POST",
				CountableMap.Default(),
				Charset.DEFAULT_HTTP.stringToByteSequence(requestJson.toString())
			)
		)
	} catch (e: IOException) {
		throw IOException.Runtime(e)
	} catch (e: HttpException) {
		throw HttpException.Runtime(e.response)
	}

}