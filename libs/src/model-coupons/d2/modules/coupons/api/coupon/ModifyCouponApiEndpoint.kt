package d2.modules.coupons.api.coupon


import d2.modules.coupons.model.Coupon
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


abstract class ModifyCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "modify"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val couponId	= requestJson.getString("id")

		val categoryId 		= requestJson.optString("categoryId", 	null)
		val code 			= requestJson.optString("code", 		null)
		val codeImageUrl 	= requestJson.optString("codeImageUrl", null)

		val coupon = try {
			Coupon.getMapper(storage).getOrThrow(couponId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NOT_EXISTS")
			return object : JsonResponse() { override val json = responseJson }
		}

		synchronized (coupon) {
			if (categoryId != null)		coupon.categoryId 	= categoryId
			if (code != null) 			coupon.code 		= code
			if (codeImageUrl != null)	coupon.codeImageUrl = codeImageUrl
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return object : JsonResponse() { override val json = responseJson }

	}

}