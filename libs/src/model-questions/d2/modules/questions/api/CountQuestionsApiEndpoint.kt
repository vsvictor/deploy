@file:Suppress("RedundantIf")


package d2.modules.questions.api


import d2.modules.questions.model.Question
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.NotExists
import ic.throwables.UnableToParse
import ic.struct.collection.FilterCollection

import org.json.JSONObject


abstract class CountQuestionsApiEndpoint : ProtectedHttpEndpoint() {


	override val name get() = "count"


	protected abstract val storage: BindingStorage

	@Throws(NotExists::class)
	protected abstract fun userToString(userId: String?): String


	@Throws(UnableToParse::class)
	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val askerId: String?
		val category: String?
		val answererId: String?
		val answered: Boolean?; run {
			if (request.method == "GET") {
				askerId = null
				category = null
				answererId = null
				answered = null
			} else {
				val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
				askerId = requestJson.optString("askerId", null)
				category = requestJson.optString("category", null)
				answererId = requestJson.optString("answererId", null)
				answered = if (requestJson.has("answered")) requestJson.getBoolean("answered") else null
			}
		}

		val storage = storage

		val questionsMapper = Question.getMapper(storage)

		val responseJson = JSONObject().apply {
			put("status", "SUCCESS")
		}

		responseJson.put("questionsCount", FilterCollection(questionsMapper.items) { question ->

			if (askerId != null 	&& question.askerId != askerId) 		false else
			if (category != null 	&& question.category != category) 		false else
			if (answererId != null 	&& question.answererId != answererId) 	false else
			if (answered != null 	&& question.answered != answered) 		false

			else true

		}.count)


		return object : JsonResponse() {
			override val json: Any
				get() = responseJson
		}

	}


}
