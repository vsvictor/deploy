package d2.modules.coupons.user.model


import ic.annotations.Necessary
import ic.event.Event
import ic.interfaces.changeable.Changeable
import ic.serial.json.JsonSerializable
import ic.serial.json.JsonSerializer
import ic.storage.BindingStorage
import ic.struct.map.CountableMap
import ic.struct.map.EditableMap
import ic.throwables.AlreadyExists
import ic.throwables.AlreadyExists.ALREADY_EXISTS
import ic.throwables.NotExists
import ic.throwables.WrongState
import ic.throwables.WrongState.WRONG_STATE
import org.json.JSONObject


class UserCoupons : JsonSerializable, Changeable {

	private val couponsValue : EditableMap<UserCoupon, String>

	val coupons : CountableMap<UserCoupon, String> get() = CountableMap.Default(couponsValue)

	@Throws(AlreadyExists::class)
	fun add(couponId: String) {
		synchronized (this) {
			if (couponsValue.keys.contains(couponId)) throw ALREADY_EXISTS
			couponsValue.set(couponId, UserCoupon(false))
			changeEventTrigger.run()
		}
	}

	@Throws(NotExists::class, WrongState::class)
	fun use(couponId: String) {
		synchronized (this) {
			val coupon = couponsValue.getOrThrow(couponId)
			if (coupon.isUsed) throw WRONG_STATE
			coupon.isUsed = true
			changeEventTrigger.run()
		}
	}

	private val changeEventTrigger = Event.Trigger(); override val changeEvent get() = changeEventTrigger

	override val classToDeclare = UserCoupons::class.java
	override fun serialize (json: JSONObject) {
		json.put("coupons", JsonSerializer.serialize(couponsValue, false))
	}
	@Necessary constructor(json: JSONObject) {
		couponsValue = JsonSerializer.parse(json.get("coupons"))
	}

	private constructor() {
		couponsValue = EditableMap.Default()
	}

	companion object {

		fun byUserId(storage: BindingStorage, userId : String) : UserCoupons {
			val userCouponsStorage = storage.createFolderIfNotExists("coupons")
			synchronized (UserCoupons) {
				return userCouponsStorage.createIfNull(userId) { UserCoupons() }
			}
		}

	}

}