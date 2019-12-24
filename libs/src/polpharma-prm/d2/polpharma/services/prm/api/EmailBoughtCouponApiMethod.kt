package d2.polpharma.services.prm.api


import ic.email.Email.sendEmail
import ic.email.EmailAccount
import ic.email.EmailMessage
import ic.email.SMTPConfig
import ic.network.SocketAddress
import ic.network.http.*
import ic.text.Charset
import org.json.JSONObject


class EmailBoughtCouponApiMethod : ProtectedHttpEndpoint() {

	override val name = "email_bought_coupon"

	override fun checkServerKey (serverKey: String) = d2.polpharma.services.checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = d2.polpharma.services.checkPolpharmaSuperadminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val email 			= requestJson.getString("email")
		val codeImageUrl 	= requestJson.getString("codeImageUrl")
		val couponName 		= requestJson.getString("couponName")

		sendEmail(
			email,
			EmailMessage(
				"Ваш купон - $couponName",
				"<img src=\"$codeImageUrl\"></img>"
			),
			SMTPConfig.Gmail(EmailAccount("we4you.kupon@gmail.com", "slbiy3ok76"))
		)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		return object : JsonResponse() { override val json = responseJson }

	}

}