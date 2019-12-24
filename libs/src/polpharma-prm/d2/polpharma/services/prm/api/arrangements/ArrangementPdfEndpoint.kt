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
import ic.text.TextBuffer
import ic.throwables.AccessDenied.ACCESS_DENIED
import ic.throwables.NotExists


class ArrangementPdfEndpoint : HttpEndpoint() {

	override val name = "arrangement.pdf"

	override fun implementEndpoint (socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val arrangementId = request.urlParams("id")

		val arrangement = try {
			Arrangement.mapper.getItemOrThrow(arrangementId)
		} catch (notExists : NotExists) {
			return object : HttpResponse {
				override val status = STATUS_NOT_FOUND
			}
		}

		val html = TextBuffer()

		html.writeLine("${ arrangement.name }<br>")
		html.writeLine("From: ${ formatNullableDate(arrangement.startDate, "yyyy.MM.dd") }<br>")
		html.writeLine("To: ${ formatNullableDate(arrangement.endDate, "yyyy.MM.dd") }<br>")
		html.writeLine("Cost points: ${ arrangement.costPoints }<br>")
		html.writeLine("Place: ${ arrangement.place }<br>")

		html.writeLine("Users:<br>")
		html.writeLine("<table>")

		html.writeLine("<tr>")
		html.writeLine("<td>Quadrasoft id</td>")
		html.writeLine("<td>Points</td>")
		html.writeLine("<td>Line</td>")
		html.writeLine("<td>Category</td>")
		html.writeLine("</tr>")

		val arrangementUsers = arrangement.users

		arrangementUsers.keys.forEach { userId ->

			if (arrangementUsers.get(userId) != UserArrangementState.CONFIRMED) return@forEach

			val user = User.mapper.getItem(userId)

			html.writeLine("<tr>")
			html.writeLine("<td>${ user.quadrasoftId }</td>")
			html.writeLine("<td>${ getPoints(polpharmaPrmPointsStorage, userId) }</td>")
			html.writeLine("<td>${ user.line }</td>")
			html.writeLine("<td>${ user.category }</td>")
			html.writeLine("</tr>")

		}

		html.writeLine("</table>")

		return object : HttpResponse {
			override val contentType = MimeType.PDF
			override val body = htmlToPdf(html)
		}

	}


}