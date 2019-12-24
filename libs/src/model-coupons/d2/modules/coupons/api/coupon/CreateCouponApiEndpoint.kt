package d2.modules.coupons.api.coupon


import d2.modules.coupons.model.Coupon
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import org.json.JSONObject


abstract class CreateCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "create"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val categoryId 		= requestJson.optString("categoryId", 	null)
		val code 			= requestJson.optString("code", 		null)
		val codeImageUrl 	= requestJson.optString("codeImageUrl", null)

		val coupon = Coupon(categoryId, code, codeImageUrl)

		val couponId = Coupon.getMapper(storage).id(coupon)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
			responseJson.put("id", couponId)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}