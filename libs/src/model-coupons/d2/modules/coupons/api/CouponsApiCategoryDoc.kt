package d2.modules.coupons.api


import d2.modules.docs.api.ApiCategoryDoc
import d2.modules.docs.api.ApiRequestDoc
import ic.TextResources.getResText
import ic.struct.list.List


abstract class CouponsApiCategoryDoc : ApiCategoryDoc() {

	abstract val route : String

	override val requests = List.Default<ApiRequestDoc>(

		object : ApiRequestDoc() {
			override val name = "List coupons"
			override val url = "$route/list"
			override val httpMethod = "GET | POST"
			override val requestExample = getResText("model-coupons/api/list-request")
			override val responseExample = getResText("model-coupons/api/list-response")
		},

		object : ApiRequestDoc() {
			override val name = "Create coupon"
			override val url = "$route/create"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/create-request")
			override val responseExample = getResText("model-coupons/api/create-response")
		},

		object : ApiRequestDoc() {
			override val name = "Modify coupon"
			override val url = "$route/modify"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/modify-request")
			override val responseExample = getResText("model-coupons/api/modify-response")
		},

		object : ApiRequestDoc() {
			override val name = "Get coupon by id"
			override val url = "$route/get"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/get-request")
			override val responseExample = getResText("model-coupons/api/get-response")
		},

		object : ApiRequestDoc() {
			override val name = "Delete coupon by id"
			override val url = "$route/delete"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/delete-request")
			override val responseExample = getResText("model-coupons/api/delete-response")
		},

		object : ApiRequestDoc() {
			override val name = "List coupons categories"
			override val url = "$route/category/list"
			override val httpMethod = "GET"
			override val requestExample = null
			override val responseExample = getResText("model-coupons/api/categories/list-response")
		},

		object : ApiRequestDoc() {
			override val name = "Create coupons category"
			override val url = "$route/category/create"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/categories/create-request")
			override val responseExample = getResText("model-coupons/api/categories/create-response")
		},

		object : ApiRequestDoc() {
			override val name = "Modify coupons category"
			override val url = "$route/category/modify"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/categories/modify-request")
			override val responseExample = getResText("model-coupons/api/categories/modify-response")
		},

		object : ApiRequestDoc() {
			override val name = "Get coupons category by id"
			override val url = "$route/category/get"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/categories/get-request")
			override val responseExample = getResText("model-coupons/api/categories/get-response")
		},

		object : ApiRequestDoc() {
			override val name = "Delete coupons category by id"
			override val url = "$route/category/delete"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/api/categories/delete-request")
			override val responseExample = getResText("model-coupons/api/categories/delete-response")
		}

	)

}