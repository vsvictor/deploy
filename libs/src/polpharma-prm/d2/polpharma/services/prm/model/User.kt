package d2.polpharma.services.prm.model


import d2.polpharma.services.prm.polpharmaPrmStorage
import ic.event.Event
import ic.id.IdGenerator
import ic.id.SecureStringIdGenerator
import ic.id.StorageMapper
import ic.interfaces.changeable.Changeable
import ic.loop
import ic.math.randomInt
import ic.serial.json.JsonSerializable
import ic.storage.BindingStorage
import ic.struct.order.OrderedCountableSet
import ic.struct.set.CountableSet
import ic.text.TextBuffer
import ic.throwables.AlreadyExists
import ic.throwables.Break.BREAK
import ic.throwables.NotExists
import org.json.JSONObject


class User : JsonSerializable, Changeable {


	val quadrasoftId : String


	private var active : Boolean

	@Synchronized fun isActive() : Boolean = active

	@Synchronized fun setActive(active : Boolean) {
		this.active = active
		changeEventTrigger.run()
	}


	private var newPoints : Int

	@Synchronized fun getNewPoints() : Int = newPoints

	@Synchronized fun setNewPoints(newPoints : Int) {
		this.newPoints = newPoints
		changeEventTrigger.run()
	}


	private var month : Int

	@Synchronized fun getMonth() : Int = month

	@Synchronized fun setMonth(month : Int) {
		this.month = month
		changeEventTrigger.run()
	}


	private var _line : String

	var line : String
		get() = _line
		set(value) {
			_line = value
			changeEventTrigger.run()
		}


	private var _category : String

	var category : String
		get() = _category
		set(value) {
			_category = value
			changeEventTrigger.run()
		}


	private var emailValue : String?
	var email : String? get() = emailValue; set(value) { emailValue = value; changeEventTrigger.run() }


	// Changeable implementation:

	private val changeEventTrigger = Event.Trigger(); override val changeEvent : Event = changeEventTrigger


	// JsonSerializable implementation:

	override val classToDeclare = User::class.java

	override fun serialize (json : JSONObject) {
		json.put("quadrasoftId", 	quadrasoftId)
		json.put("newPoints",		newPoints)
		json.put("month",			month)
		json.put("active",			active)
		json.put("line",			_line)
		json.put("category",		_category)
		json.putOpt("email", 		emailValue)
	}

	constructor (json: JSONObject) {
		quadrasoftId 	= json.getString("quadrasoftId")
		newPoints		= json.optInt("newPoints", 0) // Backward compat
		month			= json.optInt("month", 0)
		active			= json.optBoolean("active", true)
		_line			= json.optString("line", "")
		_category		= json.optString("category", "")
		emailValue		= json.optString("email", null)
	}


	// Constructors:

	@Throws(AlreadyExists::class)
	constructor (quadrasoftId: String) {

		synchronized (usersByQuadrasoftId) {

			try {
				throw AlreadyExists(
					usersByQuadrasoftId.getOrThrow(quadrasoftId) as Any
				)
			} catch (notExists : NotExists) {}

			this.quadrasoftId = quadrasoftId

			this.newPoints = 0
			this.month = 0
			this.active = true
			_line = ""
			_category = ""
			emailValue = null

			val id = mapper.getId(this)

			usersByQuadrasoftId.set(quadrasoftId, id)

			loop {

				synchronized (usersByRegistrationCode) {

					val registrationCode = run {
						val textBuffer = TextBuffer()
						repeat (6) {
							textBuffer.write(
								Integer.toString(randomInt(36), 36)
							)
						}
						textBuffer.toString()
					}

					if (usersByRegistrationCode.contains(registrationCode)) return@loop

					usersByRegistrationCode.set(registrationCode, id)

					throw BREAK

				}

			}

		}

	}


	companion object {


		val mapper = object : StorageMapper<User>(
			BindingStorage(
				polpharmaPrmStorage.createFolderIfNotExists("users")
			)
		) {
			override fun initIdGenerator() : IdGenerator<String> { return SecureStringIdGenerator() }
		}


		private val usersByQuadrasoftId = polpharmaPrmStorage.createFolderIfNotExists("usersByQuadrasoftId")

		@Throws(NotExists::class)
		fun idByQuadrasoftId (quadrasoftId: String) : String {
			return usersByQuadrasoftId.getOrThrow(quadrasoftId)
		}

		fun getQuadrasoftIds () : OrderedCountableSet<String>? {
			return usersByQuadrasoftId.keys
		}

		@Throws(NotExists::class)
		fun byQuadrasoftId (quadrasoftId: String) : User {
			return mapper.getItem(idByQuadrasoftId(quadrasoftId))
		}


		private val usersByRegistrationCode = polpharmaPrmStorage.createFolderIfNotExists("registrationCode")

		fun getRegistrationCodes() : CountableSet<String> = usersByRegistrationCode.keys

		@Throws(NotExists::class)
		fun idByRegistrationCode (registrationCode: String) : String {
			return usersByRegistrationCode.getOrThrow(registrationCode)
		}

		@Throws(NotExists::class)
		fun idByRegistrationCodeAndRemove (registrationCode: String) : String {
			synchronized (usersByRegistrationCode) {
				val userId : String = usersByRegistrationCode.getOrThrow(registrationCode)
				usersByRegistrationCode.remove(registrationCode)
				return userId
			}
		}

		@Throws(NotExists::class)
		fun byRegistrationCode (registrationCode: String) : User {
			return mapper.getItem(idByRegistrationCode(registrationCode))
		}


	}


}