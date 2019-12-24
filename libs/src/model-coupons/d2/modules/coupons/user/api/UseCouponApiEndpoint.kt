package d2.modules.coupons.user.api


import d2.modules.coupons.user.model.UserCoupons
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.text.Charset
import ic.throwables.NotExists
import ic.throwables.WrongState
import org.json.JSONObject


abstract class UseCouponApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "use"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val userId 		= requestJson.getString("userId")
		val couponId 	= requestJson.getString("couponId")

		val userCoupons = UserCoupons.byUserId(storage, userId)

		val responseJson = JSONObject();

		try {
			userCoupons.use(couponId)
			responseJson.put("status", "SUCCESS")
		} catch (notExists : NotExists) {
			responseJson.put("status", "USER_COUPON_NOT_EXISTS")
		} catch (wrongState : WrongState) {
			responseJson.put("status", "ALREADY_USED")
		}

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}