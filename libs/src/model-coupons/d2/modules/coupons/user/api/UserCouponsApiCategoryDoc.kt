package d2.modules.coupons.user.api


import d2.modules.docs.api.ApiCategoryDoc
import d2.modules.docs.api.ApiRequestDoc
import ic.TextResources.getResText
import ic.struct.list.List


abstract class UserCouponsApiCategoryDoc : ApiCategoryDoc() {

	abstract val route : String

	override val requests = List.Default<ApiRequestDoc>(

		object : ApiRequestDoc() {
			override val name = "List user coupons"
			override val url = "$route/list"
			override val httpMethod = "GET"
			override val requestExample = getResText("model-coupons/user/api/list-request")
			override val responseExample = getResText("model-coupons/user/api/list-response")
		},

		object : ApiRequestDoc() {
			override val name = "Add user coupon"
			override val url = "$route/add"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/user/api/add-request")
			override val responseExample = getResText("model-coupons/user/api/add-response")
		},

		object : ApiRequestDoc() {
			override val name = "Use coupon"
			override val url = "$route/use"
			override val httpMethod = "POST"
			override val requestExample = getResText("model-coupons/user/api/use-request")
			override val responseExample = getResText("model-coupons/user/api/use-response")
		}

	)

}