package d2.modules.coupons.user.model


import ic.annotations.Necessary
import ic.serial.json.JsonSerializable
import org.json.JSONObject


class UserCoupon : JsonSerializable {

	private var isUsedValue : Boolean
	var isUsed : Boolean; get() = isUsedValue; internal set(value) { isUsedValue = value; }

	override val classToDeclare = UserCoupon::class.java
	override fun serialize (json: JSONObject) {
		json.put("isUsed", 		isUsedValue)
	}
	@Necessary constructor(json: JSONObject) {
		isUsedValue = json.getBoolean("isUsed")
	}

	constructor(
		isUsed: Boolean
	) {
		isUsedValue = isUsed
	}

}