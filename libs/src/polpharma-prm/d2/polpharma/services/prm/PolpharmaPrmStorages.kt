package d2.polpharma.services.prm


import ic.mongodb.localMongoDbStorage
import ic.storage.BindingStorage


val polpharmaPrmStorage = localMongoDbStorage.createFolderIfNotExists("polpharma-prm")

val polpharmaPrmAdminsStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("admins"))

val polpharmaPrmSurveyStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("survey"))

val polpharmaPrmEventsStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("events"))

val polpharmaPrmCouponsStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("coupons"))

val polpharmaPrmPointsStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("points"))
val polpharmaPrmFishkiStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("fishki"))

val polpharmaPrmArrangementsStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("arrangements"))
val polpharmaPrmIndividualStorage =  BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("individual_events"))
val polpharmaPrmAgreementStorage =  BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("agreement"))
val polpharmaPrmNewFishkiStorage = BindingStorage(polpharmaPrmStorage.createFolderIfNotExists("prmfishki"))