package d2.modules.coupons.api.coupon


import d2.modules.coupons.model.CouponsCategory
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


abstract class ModifyCouponsCategoryApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "modify"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val couponsCategoryId	= requestJson.getString("id")

		val name 		= requestJson.optString("name", 	null)
		val imageUrl 	= requestJson.optString("imageUrl", null)
		val price		= requestJson.opt("price") as Int?

		val couponsCategory = try {
			CouponsCategory.getMapper(storage).getOrThrow(couponsCategoryId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "NOT_EXISTS")
			return object : JsonResponse() { override val json = responseJson }
		}

		synchronized (couponsCategory) {
			if (name != null)			couponsCategory.name = name
			if (imageUrl != null)		couponsCategory.imageUrl = imageUrl
			if (price != null)			couponsCategory.price = price
		}

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return JsonResponse.Final(responseJson)

	}

}