package ic.network.http


import ic.mimetypes.MimeType
import ic.stream.ByteSequence
import ic.struct.map.CountableMap
import ic.text.Charset


interface HttpResponse {


	@JvmDefault val status: Int get() = STATUS_OK


	@JvmDefault
	val statusMessage : String get() {
		val status = status
		if (status == STATUS_OK) 				return "200 OK"
		if (status == STATUS_NO_CONTENT) 		return "204 No Content"
		if (status == STATUS_MOVED_PERMANENTLY) return "301 Moved Permanently"
		else return Integer.toString(status)
	}


	@JvmDefault
	val contentType: MimeType get() = MimeType.HTML

	@JvmDefault
	val charset: Charset get() = Charset.DEFAULT_HTTP

	@JvmDefault
	val headers: CountableMap<String, String> get() = CountableMap.Default()

	@JvmDefault
	val body: ByteSequence get() = ByteSequence.EMPTY


	companion object {

		const val STATUS_OK : Int = 200
		const val STATUS_NO_CONTENT = 204
		const val STATUS_MOVED_PERMANENTLY = 301
		const val STATUS_BAD_REQUEST = 400
		const val STATUS_UNAUTHORIZED = 401
		const val STATUS_FORBIDDEN = 403
		const val STATUS_NOT_FOUND = 404
		const val STATUS_SERVICE_UNAVAILABLE = 503

	}


}
