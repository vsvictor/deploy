package ic


import ic.Storages.getCommonDataStorage
import ic.apps.ic.UiCallback
import ic.url.Url.separateAuth


fun getAuth (destination: String, mode: String, authAsker: (String, Pair<String, String>) -> Pair<String, String>): Pair<String, String> {

	val urlAndAuth = separateAuth(destination)

	return (
		getCommonDataStorage()
		.createFolderIfNotExists("ic")
		.createFolderIfNotExists("auth")
		.createFolderIfNotExists(mode)
	).createIfNull(destination) {
		authAsker.invoke(urlAndAuth.first, urlAndAuth.second)
	}

}

fun getAuth (destination: String, mode: String, uiCallback: UiCallback): Pair<String, String> {

	return getAuth(destination, mode) { url, existingAuth -> uiCallback.askAuth(url, existingAuth) }

}