package ic


import ic.Storages.getUserIcDir
import ic.storage.Directory


private var dataStorageValue: Directory? = null

val dataStorage : Directory @Synchronized get() {
	if (dataStorageValue == null) {
		dataStorageValue = getUserIcDir().createFolderIfNotExists("data").createFolderIfNotExists(app.packageName)
	}
	return dataStorageValue!!
}