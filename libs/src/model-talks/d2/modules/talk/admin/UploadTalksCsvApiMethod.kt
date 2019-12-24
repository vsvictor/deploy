package d2.modules.talk.admin


import d2.modules.talk.model.Speaker
import d2.modules.talk.model.Stage
import d2.modules.talk.model.Talk
import ic.interfaces.condition.Condition1
import ic.network.SocketAddress
import ic.storage.Storage
import ic.struct.set.EditableSet
import ic.text.Charset
import ic.throwables.AlreadyExists
import ic.throwables.NotExists
import ic.throwables.UnableToParse
import org.json.JSONObject

import java.util.Date

import d2.modules.talk.model.Speaker.getSpeakersMapper
import d2.modules.talk.model.Stage.getStagesMapper
import d2.modules.talk.model.Talk.getTalksMapper
import d2.modules.talk.model.TalkEvent.getTalkEventsMapper
import ic.csv.Csv.parseCsv
import ic.date.DateFormat.*
import ic.network.http.*
import ic.throwables.AccessDenied.ACCESS_DENIED
import java.util.Objects.equals
import java.lang.Integer.parseInt


abstract class UploadTalksCsvApiMethod : ProtectedHttpEndpoint() {

	protected abstract val storage: Storage

	override val name = "upload_talks_csv"

	override fun checkServerKey(serverKey: String) { throw ACCESS_DENIED }

	@Throws(UnableToParse::class)
	override fun implementSafeEndpoint(socketAddress: SocketAddress, request: HttpRequest): HttpResponse {

		val talkEventId = request.urlParams.getNotNull("talk_event_id")

		val storage = storage

		val talkEvent = getTalkEventsMapper(storage)[talkEventId]

		val data = parseCsv(Charset.DEFAULT_HTTP.bytesToText(request.body))

		val stagesMapper = getStagesMapper(storage)
		val speakersMapper = getSpeakersMapper(storage)
		val talksMapper = getTalksMapper(storage)

		data.forEach { row, rowIndex ->

			if (rowIndex == 0) return@forEach

			val type = row.get(1).trim { it <= ' ' }

			val name = row.get(3).trim { it <= ' ' }
			val shortDescription = row.get(4).trim { it <= ' ' }
			val description = row.get(5).trim { it <= ' ' }

			val startDate: Date?
			val endDate: Date?
			run {
				val dateString = row.get(0).trim { it <= ' ' }
				val startTimeString = row.get(8).trim { it <= ' ' }
				val endTimeString = row.get(9).trim { it <= ' ' }
				startDate = safeParseNullableDate("$dateString $startTimeString", "dd.MM.yyyy HH:mm", "M/d/y HH:mm")
				endDate = safeParseNullableDate("$dateString $endTimeString", "dd.MM.yyyy HH:mm", "M/d/y HH:mm")
			}

			val placesCount: Int?
			run {
				val placesCountString = row.get(10).trim { it <= ' ' }
				if (placesCountString.isEmpty()) {
					placesCount = null
				} else {
					placesCount = parseInt(placesCountString)
				}
			}

			val stageId: String
			run {
				val stageName = row.get(2).trim { it <= ' ' }
				var value: String
				try {
					value = talkEvent.stageIds.findOrThrow { stageId ->
						val stage = stagesMapper[stageId]
						equals(stage.name, stageName)
					}
				} catch (notExists: NotExists) {
					val stage = Stage(stageName)
					value = stagesMapper.id(stage)
					try {
						talkEvent.addStage(value)
					} catch (alreadyExists: AlreadyExists) {
						throw AlreadyExists.Error(alreadyExists)
					}

				}

				stageId = value
			}

			val speakerIds = EditableSet.Default<String>()
			run {
				val speakerName = row[6].trim { it <= ' ' }
				val speakerSurname = row[7].trim { it <= ' ' }
				if (speakerName.isEmpty() && speakerSurname.isEmpty()) return@run
				if (speakerName.isEmpty()) {
					for (speakerNameWithSurname in speakerSurname.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
						val speakerNameAndSurname = speakerNameWithSurname.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
						var speakerId: String
						try {
							speakerId = talkEvent.speakerIds.findOrThrow(Condition1 {
								val speaker = speakersMapper[it]
								if (!equals(speaker.name, speakerNameAndSurname[0])) return@Condition1 false
								equals(speaker.surname, speakerNameAndSurname[1])
							})
						} catch (notExists: NotExists) {
							val speaker = Speaker(speakerNameAndSurname[0], speakerNameAndSurname[1])
							speakerId = speakersMapper.id(speaker)
							try {
								talkEvent.addSpeaker(speakerId)
							} catch (alreadyExists: AlreadyExists) {
								throw AlreadyExists.Error(alreadyExists)
							}

						}

						speakerIds.add(speakerId)
					}
				} else {
					var speakerId: String
					try {
						speakerId = talkEvent.speakerIds.findOrThrow(Condition1 {
							val speaker = speakersMapper[it]
							if (!equals(speaker.name, speakerName)) return@Condition1 false
							equals(speaker.surname, speakerSurname)
						})
					} catch (notExists: NotExists) {
						val speaker = Speaker(speakerName, speakerSurname)
						speakerId = speakersMapper.id(speaker)
						try {
							talkEvent.addSpeaker(speakerId)
						} catch (alreadyExists: AlreadyExists) {
							throw AlreadyExists.Error(alreadyExists)
						}

					}

					speakerIds.add(speakerId)
				}
			}

			val talk = Talk(
				type,
				name,
				shortDescription,
				description,
				startDate,
				endDate,
				placesCount,
				stageId,
				speakerIds
			)

			val talkId = talksMapper.id(talk)

			try {
				talkEvent.addTalk(talkId)
			} catch (alreadyExists: AlreadyExists) {
				throw AlreadyExists.Error(alreadyExists)
			}


		}

		return object : JsonResponse() {
			override val json : JSONObject get() {
				val responseJson = JSONObject()
				responseJson.put("status", "SUCCESS")
				return responseJson
			}
		}

	}


}
