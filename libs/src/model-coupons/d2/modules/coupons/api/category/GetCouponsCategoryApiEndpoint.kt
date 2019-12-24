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


abstract class GetCouponsCategoryApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "get"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val couponsCategoryId = requestJson.getString("id")

		val couponsCategory = CouponsCategory.getMapper(storage)[couponsCategoryId]

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		synchronized (couponsCategory) {
			responseJson.putOpt("name", 	couponsCategory.name)
			responseJson.putOpt("imageUrl", couponsCategory.imageUrl)
			responseJson.putOpt("price", 	couponsCategory.price)
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}