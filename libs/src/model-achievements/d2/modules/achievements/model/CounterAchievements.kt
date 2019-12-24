package d2.modules.achievements.model


import d2.modules.counter.incrementCounter
import ic.storage.Storage
import ic.struct.collection.Collection
import ic.throwables.AlreadyExists


fun incrementAchievementCounter (
	storage:		Storage,
	counterName: 	String,
	thresholds: 	Collection<Int>,
	userId: 		String,
	callback:		(String) -> Unit
) = incrementCounter (
	storage 	= storage.createFolderIfNotExists(".counter"),
	userId 		= userId,
	counterName = counterName
) { counter ->
	thresholds.forEach { threshold ->
		if (counter != threshold) return@forEach
		val achievementName = "${ counterName }_$threshold"
		try {
			pushAchievement(
				storage 		= storage,
				userId 			= userId,
				achievementName = achievementName,
				toStack 		= false
			) {}
			callback(achievementName)
		} catch (alreadyExists : AlreadyExists) {}
	}
}