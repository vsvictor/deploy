package d2.polpharma.services.prm


import ic.Service
import ic.ServiceApp
import ic.service.monitoring.TelegramMonitorService
import ic.service.monitoring.monitor.MonitorService
import ic.stream.ByteSequence
import ic.text.Text
import ic.throwables.NotSupported.NOT_SUPPORTED


class PolpharmaPrmServiceApp (args : Text) : ServiceApp(args) {


	override val packageName get() = "polpharma-prm"


	override fun initService(args : Text) : Service {

		return PolpharmaPrmHttpsServer()

	}


	override fun initMonitorService() : MonitorService {

		return object : TelegramMonitorService() {
			override fun getCertEmail(): String 		{ throw NOT_SUPPORTED; 										}
			override fun getDomainName() : String 		{ throw NOT_SUPPORTED; 										}
			override fun getHost() : String 			{ return "99.80.94.209" 									}
			override fun getMonitorKey() : String 		{ return "polpharma" 										}
			override fun getTelegramToken() : String 	{ return "837158622:AAHDpKJr-5VhnPG9_Zi5TGijU5RBSTx6yPM" 	}
		}

	}


	override val status get() = null

	override fun getBackup() : ByteSequence? {
		return null
	}


}