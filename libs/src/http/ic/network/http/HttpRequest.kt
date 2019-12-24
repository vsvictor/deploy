package ic.network.http


import ic.stream.ByteSequence
import ic.struct.map.CountableMap
import ic.text.Charset
import ic.throwables.UnableToParse


class HttpRequest {


	@JvmField val url: String

	@JvmField val urlParams: CountableMap<String, String>

	@JvmField val method: String

	@JvmField val path: String

	@JvmField val headers: CountableMap<String, String>

	@JvmField val auth: HttpAuth?

	@JvmField val body: ByteSequence


	private fun parseAuth(headers: CountableMap<String, String>): HttpAuth? {
		var authorizationValue = headers.get("Authorization")
		if (authorizationValue == null) authorizationValue = headers.get("authorization")
		if (authorizationValue == null) return null
		try {
			return HttpAuth.parse(authorizationValue)
		} catch (unableToParse: UnableToParse) {
			throw UnableToParse.Runtime(unableToParse)
		}

	}


	constructor(
		url: String,
		path: String,
		urlParams: CountableMap<String, String>,
		method: String,
		headers: CountableMap<String, String>,
		body: ByteSequence
	) {

		this.url = url
		this.path = path
		this.urlParams = urlParams
		this.method = method
		this.headers = headers
		this.auth = parseAuth(headers)
		this.body = body

	}

	constructor(
		url: String,
		pathWithParams: String,
		method: String,
		headers: CountableMap<String, String>,
		body: ByteSequence
	) {

		this.url = url

		val (first, second) = parsePathAndParams(pathWithParams)

		this.path = first
		this.urlParams = second

		this.method = method
		this.headers = headers
		this.auth = parseAuth(headers)
		this.body = body

	}

	constructor(
		url: String,
		method: String,
		headers: CountableMap<String, String>,
		body: ByteSequence
	) {

		this.url = url
		this.method = method

		val pathWithParams: String
		run {
			val schemeEnd = url.indexOf("//")
			if (schemeEnd == -1) {
				pathWithParams = url
			} else {
				pathWithParams = url.substring(schemeEnd + "//".length)
			}
		}

		val (first, second) = parsePathAndParams(pathWithParams)

		this.path = first
		this.urlParams = second

		this.auth = parseAuth(headers)
		this.headers = headers
		this.body = body

	}

	constructor(url: String, method: String, body: ByteSequence) : this(url, method, CountableMap.Default<String, String>(), body) {}

	companion object {

		fun parseParams(paramsString: String): CountableMap<String, String> {
			if (paramsString.isEmpty()) return CountableMap.Default()
			val params = java.util.HashMap<String, String>()
			for (paramString in paramsString.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
				val paramSplit = paramString.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				if (paramSplit.size == 1) {
					params[paramSplit[0]] = ""
				} else if (paramSplit.size == 2) {
					params[paramSplit[0]] = Charset.DEFAULT_HTTP.decodeUrl(paramSplit[1])
				} else
					throw UnableToParse.Runtime("Params malformed - \"$paramsString\"")
			}
			return CountableMap.Default(params)
		}

		fun parsePathAndParams(pathWithParams: String): Pair<String, CountableMap<String, String>> {
			val pathEnd = pathWithParams.indexOf('?')
			if (pathEnd == -1) {
				return Pair<String, CountableMap<String, String>>(
					pathWithParams,
					CountableMap.Default()
				)
			} else {
				val paramsString = pathWithParams.substring(pathEnd + 1)
				return Pair(
					pathWithParams.substring(0, pathEnd),
					parseParams(paramsString)
				)
			}
		}
	}


}
