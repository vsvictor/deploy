package d2.polpharma.services.prm.api.arrangements


import d2.modules.points.model.getPoints
import d2.polpharma.services.prm.checkPolpharmaPrmAdminAuth
import d2.polpharma.services.prm.model.Arrangement
import d2.polpharma.services.prm.model.User
import d2.polpharma.services.prm.model.UserArrangementState
import d2.polpharma.services.prm.polpharmaPrmPointsStorage
import ic.date.DateFormat.formatNullableDate
import ic.mimetypes.MimeType
import ic.network.SocketAddress
import ic.network.http.*
import ic.network.http.HttpResponse
import ic.network.http.HttpResponse.Companion.STATUS_NOT_FOUND
import ic.pdf.htmlToPdf
import ic.stream.ByteBuffer
import ic.stream.ByteSequence
import ic.text.TextBuffer
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists
import java.nio.charset.Charset


class ArrangementCSVEndpoint : HttpEndpoint() {

	override val name = "arrangement.csv"

	override fun implementEndpoint (socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val arrangementId = request.urlParams("id")

		val arrangement = try {
			Arrangement.mapper.getItemOrThrow(arrangementId)
		} catch (notExists : NotExists) {
			return object : HttpResponse {
				override val status = STATUS_NOT_FOUND
			}
		}

		var html = ""; //TextBuffer()

		html += "${ arrangement.name }\n"
		html+="From: ${ formatNullableDate(arrangement.startDate, "yyyy.MM.dd") }\n"
		html+="To: ${ formatNullableDate(arrangement.endDate, "yyyy.MM.dd") }\n"
		html+="Cost points: ${ arrangement.costPoints }\n"
		html+="Place: ${ arrangement.place }\n"

		html+="Users:\n"
		//html.writeLine("<table>")

		//html.writeLine("<tr>")
		html+=" Quadrasoft_id"
		html+=("  Points ")
		html+="  Line  "
		html+="  Category  "
		html+="\n"

		val arrangementUsers = arrangement.users

		arrangementUsers.keys.forEach { userId ->

			if (arrangementUsers.get(userId) != UserArrangementState.CONFIRMED) return@forEach

			val user = User.mapper.getItem(userId)

			html+="\n"
			html+="  ${ user.quadrasoftId }  "
			html+="  ${ getPoints(polpharmaPrmPointsStorage, userId) }  "
			html+="  ${ user.line }  "
			html+="  ${ user.category }  "
			html+="\n"
		}

		return object : HttpResponse {
			override val contentType = MimeType.CSV
			override val body =  ByteSequence.FromByteArray(html.toByteArray(Charset.defaultCharset()))
		}

	}


}