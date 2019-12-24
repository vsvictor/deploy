package d2.polpharma.services.ophtik


import ic.mongodb.localMongoDbStorage
import ic.storage.BindingStorage


private val ophtikStorage = try {
	localMongoDbStorage.createFolderIfNotExists("polpharma-ophtik")
} catch (throwable : Throwable) {
	throwable.printStackTrace()
	throw throwable
}


@JvmField val ophtikUsersStorage = BindingStorage(ophtikStorage.createFolderIfNotExists("users"))

@JvmField val ophtikEducationStorage = BindingStorage(ophtikStorage.createFolderIfNotExists("education"))
@JvmField val ophtikSurveyStorage = BindingStorage(ophtikStorage.createFolderIfNotExists("survey"))

@JvmField val ophtikQuestionsStorage = BindingStorage(ophtikStorage.createFolderIfNotExists("questions"))

@JvmField val ophtikEventsStorage = ophtikStorage.createFolderIfNotExists("events")

@JvmField val ophtikAchievementsStorage = ophtikStorage.createFolderIfNotExists("achievements")