package d2.modules.questions.api


import d2.modules.docs.api.ApiCategoryDoc
import d2.modules.docs.api.ApiRequestDoc
import ic.TextResources.getResText
import ic.struct.list.List


abstract class QuestionsApiCategoryDoc : ApiCategoryDoc() {


	override val name = "Questions"


	protected abstract val route : String


	override val requests = List.Default<ApiRequestDoc>(

		object : ApiRequestDoc() {
			override val name = "Ask question"
			override val url = "$route/ask"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-questions/api/ask-request")
			override val responseExample = getResText("model-questions/api/ask-response")
		},

		object : ApiRequestDoc() {
			override val name = "Answer question"
			override val url = "$route/answer"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-questions/api/answer-request")
			override val responseExample = getResText("model-questions/api/answer-response")
		},

		object : ApiRequestDoc() {
			override val name = "Get question"
			override val url = "$route/get"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-questions/api/get-request")
			override val responseExample = getResText("model-questions/api/get-response")
		},

		object : ApiRequestDoc() {
			override val name = "List questions"
			override val url = "$route/get_questions"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-questions/api/list-request")
			override val responseExample = getResText("model-questions/api/list-response")
		},

		object : ApiRequestDoc() {
			override val name = "Get questions count by category"
			override val url = "$route/get_questions_count_by_category"
			override val httpMethod = "GET"
			override val requestExample = null
			override val responseExample = getResText("model-questions/api/get-questions-count-by-category-response")
		},

		object : ApiRequestDoc() {
			override val name = "Delete question"
			override val url = "$route/delete"
			override val httpMethod = "GET"
			override val requestExample = getResText("model-questions/api/delete-request")
			override val responseExample = getResText("model-questions/api/delete-response")
		}

	)


}