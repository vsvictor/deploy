package d2.polpharma.services.db.api.ophtalm

import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.tier

val ophtikDbHost : String get() {

	if (tier == "prod") 	return "https://db.ophtik.com.ua"
	if (tier == "stage") 	return "https://stage.db.ophtik.com.ua"
	else throw NOT_SUPPORTED

}