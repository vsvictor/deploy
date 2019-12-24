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


abstract class GetCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "get"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val couponId = requestJson.getString("id")

		val coupon = Coupon.getMapper(storage)[couponId]

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		synchronized (coupon) {
			responseJson.putOpt("categoryId", 	coupon.categoryId)
			responseJson.putOpt("code", 		coupon.code)
			responseJson.putOpt("codeImageUrl", coupon.codeImageUrl)
			responseJson.put("addedCount", 		coupon.addedCount)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}