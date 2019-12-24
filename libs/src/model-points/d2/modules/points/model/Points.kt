package d2.modules.points.model


import ic.storage.Storage
import ic.throwables.NotEnough
import ic.throwables.NotEnough.NOT_ENOUGH


private fun getPointsStorage (storage : Storage) : Storage{
	return storage.createFolderIfNotExists("user-points")
}


fun getPoints (storage : Storage, userId: String) : Int {
	return getPointsStorage(storage).get(userId) { 0 }
}


@Throws(NotEnough::class)
fun addPoints (storage: Storage, userId: String, points: Int, creditLimit: Int) {
	val currentPoints = getPoints(storage, userId)
	val newPoints = currentPoints + points
	if (points < 0) {
		if (newPoints < -creditLimit) throw NOT_ENOUGH
	}
	getPointsStorage(storage).set(userId, newPoints)
}

@Throws(NotEnough::class)
fun addPoints (storage: Storage, userId: String, points: Int) = addPoints(storage, userId, points, 0)