package d2.polpharma.services.prm.api.users


import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.model.User.Companion.getRegistrationCodes
import d2.polpharma.services.polpharmaSuperadminPassword
import d2.polpharma.services.polpharmaSuperadminUserName
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import ic.csv.Csv.formatCsv
import ic.network.SocketAddress
import ic.network.http.*
import ic.struct.list.ConvertList
import ic.struct.list.JoinList
import ic.struct.list.List
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists.NOT_EXISTS
import ic.throwables.WrongValue.WRONG_VALUE


class GetRegistrationCodesCsvEndpoint : ProtectedHttpEndpoint() {


	override val name = "get-registration-codes.csv"


	override fun checkServerKey (serverKey: String) { throw ACCESS_DENIED }

	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)


	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val csv = formatCsv(
			JoinList<List<String>>(
				List.Default<List<String>>(
					List.Default<String>(
						"registrationCode",
						"quadrasoftId"
					)
				),
				ConvertList<List<String>, String>(
					List.Default<String>(getRegistrationCodes())
				) { registrationCode ->
					List.Default<String>(
						registrationCode,
						User.byRegistrationCode(registrationCode).quadrasoftId
					)
				}
			)
		)

		return object : HttpResponse {
			override val body = charset.textToByteSequence(csv)
		}

	}


}