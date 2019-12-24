package d2.modules.coupons.user.api


import d2.modules.coupons.model.Coupon
import d2.modules.coupons.user.model.UserCoupons
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.NotExists
import org.json.JSONObject


abstract class ListUserCouponsApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "list"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId = requestJson.getString("userId")

		val userCouponsMap = UserCoupons.byUserId(storage, userId).coupons

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		val couponsMapper = Coupon.getMapper(storage)

		responseJson.put("userCoupons", toJsonArray(userCouponsMap.keys) { couponId ->
			val userCouponJson = JSONObject()
			userCouponJson.put("id", couponId)
			val userCoupon = userCouponsMap.getNotNull(couponId)
			userCouponJson.put("isUsed", userCoupon.isUsed)
			try {
				val coupon = couponsMapper.getOrThrow(couponId)
				userCouponJson.putOpt("categoryId", 	coupon.categoryId)
				userCouponJson.putOpt("code", 			coupon.code)
				userCouponJson.putOpt("codeImageUrl", 	coupon.codeImageUrl)
			} catch (notExists: NotExists) {}
			userCouponJson
		})

		return JsonResponse.Final(responseJson)

	}

}