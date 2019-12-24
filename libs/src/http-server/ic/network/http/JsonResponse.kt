package ic.network.http


import ic.mimetypes.MimeType
import ic.stream.ByteSequence
import org.json.JSONObject


abstract class JsonResponse : HttpResponse {

	protected abstract val json: Any

	override val contentType = MimeType.JSON

	override val body : ByteSequence get() {
		return ByteSequence.FromByteArray(charset.stringToByteArray(json.toString()))
	}

	class Final (override val json: JSONObject) : JsonResponse()

}
