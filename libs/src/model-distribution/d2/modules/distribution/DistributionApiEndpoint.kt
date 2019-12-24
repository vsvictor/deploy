package d2.modules.distribution


import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.struct.collection.Collection
import ic.struct.list.IntRange
import ic.struct.map.EditableMap
import ic.text.Charset
import ic.throwables.WrongValue
import org.json.JSONObject
import java.lang.Math.exp
import java.lang.Math.log


abstract class DistributionApiEndpoint : ProtectedHttpEndpoint() {

	abstract val data : Collection<Double>

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))

		val mode = requestJson.getString("mode")
		val min = requestJson.getDouble("min")
		val max = requestJson.getDouble("max")
		val step = requestJson.getDouble("step")

		val convert : (Double) -> Double
		val unconvert : (Double) -> Double; run {

			if (mode == "LINEAR") {
				convert = { it }
				unconvert = { it }
			} else

			if (mode == "LOGARITHMIC") {
				convert = { log(it) }
				unconvert = { exp(it) }
			} else

			if (mode == "SYMMETRIC_LOG") {
				convert = {
					if (it > 1) {
						log(it + 1)
					} else
					if (it < 1) {
						-log(-it + 1)
					} else 0.0
				}
				unconvert = {
					if (it > 0) {
						exp(it) - 1
					} else
					if (it < 0) {
						-exp(-it) + 1
					}
					else 0.0
				}
			}

			else throw WrongValue(mode)

		}

		val groups = EditableMap.Default<Long, Int>()

		data.forEach {

			val group = ((convert(it) - min) / step).toInt()

			groups.set(group, groups.get(group, 0L) + 1)

		}

		val responseJson = JSONObject()
		responseJson.put("status", "SUCCESS")

		responseJson.put("groups", toJsonArray(IntRange(((max - min) / step).toInt() + 1)) { group ->

			val groupJson = JSONObject()
			groupJson.put("start", unconvert(min + group * step))
			groupJson.put("end", unconvert(min + (group + 1) * step))
			groupJson.put("count", groups.get(group, 0L))
			groupJson

		})

		return JsonResponse.Final(responseJson)

	}

}