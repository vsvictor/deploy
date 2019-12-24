package d2.modules.coupons.api.coupon


import d2.modules.coupons.model.Coupon
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.Skip.SKIP
import org.json.JSONObject


abstract class ListCouponsApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "list"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val categoryId : String?; run {
			if (request.method == "GET") {
				categoryId = null
			} else {
				val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
				categoryId = requestJson.optString("categoryId", null)
			}
		}

		val couponsMapper = Coupon.getMapper(storage)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("coupons", toJsonArray(couponsMapper.ids) { couponId ->

			val coupon = Coupon.getMapper(storage)[couponId]

			synchronized (coupon) {

				if (categoryId != null) {
					if (coupon.categoryId != categoryId) throw SKIP
				}

				val couponJson = JSONObject(); run {
					couponJson.put("id", couponId)
					couponJson.putOpt("categoryId", 	coupon.categoryId)
					couponJson.putOpt("code", 			coupon.code)
					couponJson.putOpt("codeImageUrl", 	coupon.codeImageUrl)
					couponJson.put("addedCount", 		coupon.addedCount)
				}

				couponJson

			}

		})

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}