package d2.modules.coupons.api.coupon


import d2.modules.coupons.model.CouponsCategory
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import org.json.JSONObject


abstract class CreateCouponCategoryApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "create"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val name 			= requestJson.optString("name", 		null)
		val imageUrl 		= requestJson.optString("imageUrl", 	null)
		val price			= requestJson.opt("price") as Int?

		val couponCategory = CouponsCategory(name, imageUrl, price)

		val couponCategoryId = CouponsCategory.getMapper(storage).id(couponCategory)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("id", couponCategoryId)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}