package d2.modules.achievements.api


import d2.modules.achievements.model.getAchievements
import ic.network.SocketAddress
import ic.network.http.*
import ic.storage.Storage
import ic.struct.collection.Collection
import ic.struct.map.EditableMap
import org.json.JSONObject


abstract class GetAchievementsCountByTypeApiEndpoint : ProtectedHttpEndpoint() {

	override val name get() = "count_by_type"

	abstract val storage : Storage

	abstract val userIds : Collection<String>

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val responseJson = JSONObject()

		responseJson.put("status", "SUCCESS")

		val countByTypes = EditableMap.Default<Int, String>()

		userIds.forEach { userId ->
			val userAchievements = getAchievements(storage, userId)
			userAchievements.keys.forEach { achievementId ->
				countByTypes.set(
					achievementId,
					countByTypes.get(achievementId) { 0 } + userAchievements[achievementId]
				)
			}
		}

		val countByTypesJson = JSONObject()
		countByTypes.keys.forEach {
			countByTypesJson.put(it, countByTypes[it])
		}
		responseJson.put("achievementsCountByType", countByTypesJson)

		return JsonResponse.Final(responseJson)

	}

}