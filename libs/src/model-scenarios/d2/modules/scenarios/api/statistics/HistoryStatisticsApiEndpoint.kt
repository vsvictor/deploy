@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")


package d2.modules.scenarios.api.statistics


import d2.modules.scenarios.model.History
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection
import ic.struct.collection.JoinCollection
import ic.throwables.NotExists
import ic.throwables.UnableToParse
import org.json.JSONObject

import d2.modules.scenarios.model.History.getHistory
import d2.modules.scenarios.model.Scenario.getScenariosMapper
import d2.modules.scenarios.model.Subject.getSubjectsMapper
import ic.date.DateFormat.formatDateOrThrow
import ic.interfaces.getter.Getter1
import ic.json.JsonArrays.toJsonArray
import ic.throwables.Skip.SKIP


abstract class HistoryStatisticsApiEndpoint : ProtectedHttpEndpoint() { override val name get() = "history"

	protected abstract val userIds: Collection<String>
	protected abstract val storage: BindingStorage

	@Throws(UnableToParse::class)
	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val responseJson = JSONObject()

		responseJson.put("status", "SUCCESS")

		val storage = storage

		responseJson.put("history", toJsonArray(

			JoinCollection<JSONObject>(
				ConvertCollection<Collection<JSONObject>, String>(userIds) { userId ->
					val subjectsMapper = getSubjectsMapper(storage)
					JoinCollection<JSONObject>(
						ConvertCollection<Collection<JSONObject>, String>(subjectsMapper.ids) { subjectId ->
							val subject = subjectsMapper[subjectId]
							val scenariosMapper = getScenariosMapper(storage, subjectId)
							JoinCollection<JSONObject>(
								ConvertCollection<Collection<JSONObject>, String>(scenariosMapper.ids) { scenarioId ->
									val scenario = scenariosMapper[scenarioId]
									ConvertCollection(
										getHistory(storage, userId, subjectId, scenarioId).items,
										object : Getter1<JSONObject, History.Item> {
											var previousItem : History.Item? = null
											override fun get (item : History.Item) : JSONObject {
												System.out.println("$userId $subjectId $scenarioId")
												if (previousItem == null) {
													previousItem = item
													throw SKIP
												}
												val itemJson = JSONObject()
												itemJson.put("userId", userId)
												itemJson.put("subject", subject.title)
												itemJson.put("scenario", scenario.title)
												try {
													val block = scenario.getBlockByIdOrThrow(previousItem!!.blockId)
													itemJson.put("blockText", block.content)
													itemJson.put(
														"way",
														(
															if (item.way == -1) "Back" else
															if (block.keyboardType.startsWith("AB")) {
																if (item.way == 0) "A" else
																if (item.way == 1) "B" else
																if (item.way == 2) "C" else
																if (item.way == 3) "D"
																else "?"
															}
															else "Next"
														)
													)
												} catch (notExists: NotExists) {
													itemJson.put("blockText", "blockId: " + previousItem!!.blockId)
													itemJson.put("way", item.way.toString())
												}
												try {
													itemJson.put("date", formatDateOrThrow(item.date, "yyyy.MM.dd HH:mm:ss"))
												} catch (notExists: NotExists) { throw SKIP }
												itemJson.put("score", item.score)
												previousItem = item
												return itemJson
											}
										}
									)
								}
							)
						}
					)
				}
			)

		) { json -> json })

		return JsonResponse.Final(responseJson)

	}

}
