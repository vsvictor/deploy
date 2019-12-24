package d2.modules.questions.model


import ic.annotations.Necessary
import ic.event.Event
import ic.id.IdGenerator
import ic.id.SimpleStringIdGenerator
import ic.id.StorageMapper
import ic.interfaces.changeable.Changeable
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import ic.struct.map.EditableMap
import ic.throwables.AccessDenied
import ic.throwables.WrongState
import org.json.JSONException
import org.json.JSONObject

import java.util.Date

import ic.date.Millis.*
import ic.util.Hex.*


class Question : JsonSerializable, Changeable {


	val askerId: String

	val askedAt: Date

	val questionText: String

	val questionImageUrl: String?

	val category: String?

	var answererId: String? = null
		private set

	var answeredAt: Date? = null
		private set

	var answerText: String? = null
		private set

	var answerImageUrl: String? = null
		private set

	val answered : Boolean get() = answeredAt != null


	override val classToDeclare get() = Question::class.java


	override val changeEvent = Event.Trigger()

	@Synchronized
	@Throws(JSONException::class)
	override fun serialize(json: JSONObject) {
		json.put("askerId", askerId)
		json.put("askedAt", longToFixedSizeHexString(dateToMillis(askedAt)))
		json.put("questionText", questionText)
		json.putOpt("questionImageUrl", questionImageUrl)
		json.putOpt("category", category)
		json.putOpt("answererId", answererId)
		json.putOpt("answeredAt", nullableLongToFixedSizeHexString(nullableDateToMillis(answeredAt)))
		json.putOpt("answerText", answerText)
		json.putOpt("answerImageUrl", answerImageUrl)
	}

	@Necessary
	@Throws(JSONException::class)
	constructor(json: JSONObject) {
		this.askerId = json.getString("askerId")
		this.askedAt = millisToDate(hexStringToLong(json.getString("askedAt")))
		this.questionText = json.getString("questionText")
		this.questionImageUrl = json.optString("questionImageUrl", null)
		this.category = json.optString("category", null)
		this.answererId = json.optString("answererId", null)
		this.answeredAt = nullableMillisToDate(nullableHexStringToLong(json.optString("answeredAt", null)))
		this.answerText = json.optString("answerText")
		this.answerImageUrl = json.optString("answerImageUrl")
	}

	constructor(
		storage: BindingStorage,
		askerId: String,
		questionText: String,
		questionImageUrl: String?,
		category: String?,
		answererId: String?
	) {

		this.askerId = askerId
		this.askedAt = now()
		this.questionText = questionText
		this.questionImageUrl = questionImageUrl
		this.category = category
		this.answererId = answererId

		getMapper(storage).getId(this)

	}


	@Synchronized
	@Throws(WrongState::class, AccessDenied::class)
	fun answer(

		answererId: String,
		answerText: String,
		answerImageUrl: String?

	) {

		if (this.answeredAt != null) throw WrongState()

		this.answererId = answererId
		this.answeredAt = now()
		this.answerText = answerText
		this.answerImageUrl = answerImageUrl

		changeEvent.run()

	}

	companion object {

		private val MAPPERS = EditableMap.Default<StorageMapper<Question>, BindingStorage>()

		@JvmStatic
		fun getMapper (storage: BindingStorage) : StorageMapper<Question> {
			return MAPPERS.createIfNull(storage) {
				object : StorageMapper<Question>(storage) {
					override fun initIdGenerator(): IdGenerator<String> {
						return SimpleStringIdGenerator()
					}
				}
			}
		}

	}


}
