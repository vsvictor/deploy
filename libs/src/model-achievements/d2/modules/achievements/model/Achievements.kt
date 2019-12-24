package d2.modules.achievements.model


import ic.storage.Storage
import ic.struct.map.CountableMap
import ic.struct.map.EditableMap
import ic.throwables.AlreadyExists
import ic.throwables.AlreadyExists.ALREADY_EXISTS


fun getAchievements (storage : Storage, userId: String) : CountableMap<Int, String> {
	return storage.createIfNull(userId) { CountableMap.Default() }
}


@Throws(AlreadyExists::class)
fun pushAchievement (
	storage 		: Storage,
	userId 			: String,
	achievementName : String,
	toStack 		: Boolean,
	callback		: (Int) -> Unit
) {
	val achievements = EditableMap.Default(getAchievements(storage, userId))
	val existingCount = achievements.createIfNull(achievementName) { 0 }
	if (!toStack && existingCount > 0) throw ALREADY_EXISTS
	val newCount = existingCount + 1
	achievements.set(achievementName, newCount)
	storage.set(userId, achievements)
	callback.invoke(newCount)
}