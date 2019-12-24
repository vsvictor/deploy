package d2.polpharma.services.prm.api.users

import d2.modules.points.model.getPoints
import d2.polpharma.services.checkPolpharmaServerKey
import d2.polpharma.services.checkPolpharmaSuperadminAuth
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.csv.Csv.formatCsv
import ic.mimetypes.MimeType
import ic.network.SocketAddress
import ic.network.http.BasicHttpAuth
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.struct.list.List
import ic.struct.list.ConvertList
import ic.struct.list.JoinList

class ListUsersCsvApiEndpoint : ProtectedHttpEndpoint() {

	override val name get() = "list.csv"

	override fun checkServerKey (serverKey: String) = checkPolpharmaServerKey(serverKey)
	override fun checkUserAuth (auth: BasicHttpAuth) = checkPolpharmaPrmAdminAuth(auth)

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val csv = formatCsv(
			JoinList<List<String>>(
				List<List<String>>(
					List<String>("id", "points")
				),
				ConvertList<List<String>, String>(List(User.mapper.ids)) {
					List(it, getPoints(polpharmaPrmPointsStorage, it).toString())
				}
			)
		)

		return object : HttpResponse {
			override val contentType get() = MimeType.CSV
			override val body = charset.textToByteSequence(csv)
		}

	}

}