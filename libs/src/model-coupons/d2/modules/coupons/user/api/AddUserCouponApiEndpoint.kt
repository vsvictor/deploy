package d2.modules.coupons.user.api


import d2.modules.coupons.model.Coupon
import d2.modules.coupons.user.model.UserCoupons
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.AlreadyExists
import ic.throwables.NotExists
import org.json.JSONObject


abstract class AddUserCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "add"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId 				= requestJson.getString("userId")
		val couponId 			= requestJson.getString("couponId")
		val maxCouponAddedCount = requestJson.optInt("maxCouponAddedCount", Int.MAX_VALUE)

		val coupon = try {
			Coupon.getMapper(storage).getOrThrow(couponId)
		} catch (notExists : NotExists) {
			val responseJson = JSONObject()
			responseJson.put("status", "COUPON_NOT_EXISTS")
			return JsonResponse.Final(responseJson)
		}

		val userCoupons = UserCoupons.byUserId(storage, userId)

		synchronized (coupon) {

			if (coupon.addedCount >= maxCouponAddedCount) {
				val responseJson = JSONObject()
				responseJson.put("status", "MAX_COUPON_ADDED_COUNT_EXCEEDED")
				return JsonResponse.Final(responseJson)
			}

			try {
				userCoupons.add(couponId)
			} catch (alreadyExists : AlreadyExists) {
				val responseJson = JSONObject()
				responseJson.put("status", "ALREADY_EXISTS")
				return JsonResponse.Final(responseJson)
			}

			coupon.addedCount++

			val responseJson = JSONObject()
			responseJson.put("status", "SUCCESS")
			return JsonResponse.Final(responseJson)

		}

	}

}