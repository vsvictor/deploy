package d2.polpharma.services.ophtik


import ic.throwables.NotSupported.NOT_SUPPORTED
import ic.tier


private const val ophtikProdDomainName 	= "db.ophtik.com.ua"
private const val ophtikStageDomainName = "stage.db.ophtik.com.ua"

val ophtikDomainName : String get() {
	if (tier == "prod") 	return ophtikProdDomainName
	if (tier == "stage") 	return ophtikStageDomainName
	else throw NOT_SUPPORTED
}