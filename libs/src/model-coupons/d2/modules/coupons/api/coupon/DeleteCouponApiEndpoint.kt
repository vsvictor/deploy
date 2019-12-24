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


abstract class DeleteCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "delete"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val couponId = requestJson.getString("id")

		try {
			Coupon.getMapper(storage).removeOrThrow(couponId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NOT_EXISTS")
			return object : JsonResponse() { override val json = responseJson }
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return object : JsonResponse() { override val json = responseJson }

	}

}