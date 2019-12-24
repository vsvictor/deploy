package d2.polpharma.services.hrm


import d2.polpharma.services.hrm.api.PolpharmaHrmApiServer
import d2.polpharma.services.polpharmaMonitorHost
import d2.polpharma.services.polpharmaMonitorKey
import d2.polpharma.services.polpharmaMonitorTelegramToken
import ic.ServiceApp
import ic.service.monitoring.TelegramMonitorService
import ic.text.Text
import ic.throwables.NotSupported.NOT_SUPPORTED


class PolpharmaHrmServiceApp (args : Text) : ServiceApp (args) {


	override val packageName get() = "polpharma-hrm"

	override fun initMonitorService() = object : TelegramMonitorService() {

		override fun getTelegramToken() = polpharmaMonitorTelegramToken
		override fun getHost() 			= polpharmaMonitorHost
		override fun getMonitorKey() 	= polpharmaMonitorKey

		override fun getDomainName() : String 	{ throw NOT_SUPPORTED }
		override fun getCertEmail() : String 	{ throw NOT_SUPPORTED }

	}

	override val status get() = null
	override fun getBackup() = null


	override fun initService (args: Text) = PolpharmaHrmApiServer()


}