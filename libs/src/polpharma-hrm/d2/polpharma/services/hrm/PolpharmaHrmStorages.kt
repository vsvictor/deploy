package d2.polpharma.services.hrm


import ic.mongodb.localMongoDbStorage
import ic.storage.BindingStorage


private val polpharmaHrmStorage = localMongoDbStorage.createFolderIfNotExists("polpharma-hrm")


@JvmField val polpharmaHrmUsersStorage = BindingStorage(polpharmaHrmStorage.createFolderIfNotExists("users"))

val polpharmaHrmAchievementsStorage = BindingStorage(polpharmaHrmStorage.createFolderIfNotExists("achievements"))

@JvmField val polpharmaHrmEventsStorage = polpharmaHrmStorage.createFolderIfNotExists("events")

val polpharmaHrmEducationStorage 	= BindingStorage(polpharmaHrmStorage.createFolderIfNotExists("education"))
val polpharmaHrmSurveyStorage 		= BindingStorage(polpharmaHrmStorage.createFolderIfNotExists("survey"))

val polpharmaHrmQuestionsStorage = BindingStorage(polpharmaHrmStorage.createFolderIfNotExists("questions"))