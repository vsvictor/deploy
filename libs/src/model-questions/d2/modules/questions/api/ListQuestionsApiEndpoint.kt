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
import ic.throwables.Skip.SKIP
import ic.throwables.UnableToParse
import ic.date.Millis.dateToMillis
import ic.date.Millis.nullableDateToMillis
import ic.json.JsonArrays.toJsonArray

import org.json.JSONObject


abstract class ListQuestionsApiEndpoint : ProtectedHttpEndpoint() {


	override val name: String?
		get() = "list"


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

		val responseJson = JSONObject()
		run { responseJson.put("status", "SUCCESS") }

		responseJson.put("questions", toJsonArray(questionsMapper.ids) { questionId ->

			val question = questionsMapper[questionId]

			if (askerId != null && question.askerId != askerId) throw SKIP
			if (category != null && question.category != category) throw SKIP
			if (answererId != null && question.answererId != answererId) throw SKIP
			if (answered != null && question.answered != answered) throw SKIP

			val questionJson = JSONObject()
			questionJson.put("id", questionId)
			questionJson.put("askedAt", dateToMillis(question.askedAt))
			questionJson.put("askerId", question.askerId)
			try {
				questionJson.put("asker", userToString(question.askerId))
			} catch (notExists: NotExists) {
			}

			questionJson.put("questionText", question.questionText)
			questionJson.putOpt("questionImageUrl", question.questionImageUrl)
			questionJson.putOpt("category", question.category)
			questionJson.putOpt("answeredAt", nullableDateToMillis(question.answeredAt))
			questionJson.putOpt("answererId", question.answererId)
			try {
				questionJson.putOpt("answerer", if (question.answererId == null) null else userToString(question.answererId))
			} catch (notExists: NotExists) {
			}

			questionJson.putOpt("answerText", question.answerText)
			questionJson.putOpt("answerImageUrl", question.answerImageUrl)
			questionJson

		})


		return object : JsonResponse() {
			override val json: Any
				get() = responseJson
		}

	}


}
