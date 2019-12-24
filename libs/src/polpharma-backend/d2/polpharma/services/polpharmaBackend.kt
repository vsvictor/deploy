package d2.polpharma.services


import ic.network.http.BasicHttpAuth
import ic.throwables.NotExists
import ic.throwables.NotExists.NOT_EXISTS
import ic.throwables.WrongValue
import ic.throwables.WrongValue.WRONG_VALUE


const val polpharmaCertEmail = "Polpharma.Bot@gmail.com"


const val polpharmaServerKey = "06bc983a9307c501"

@Throws(WrongValue::class)
fun checkPolpharmaServerKey (serverKey : String) {
	if (serverKey != polpharmaServerKey) throw WRONG_VALUE
}


const val quadrasoftServerKey = "06bc983a9307c501"


const val polpharmaSuperadminUserName = "superadmin"
const val polpharmaSuperadminPassword = "1e7082351284c8af"

@Throws(NotExists::class, WrongValue::class)
fun checkPolpharmaSuperadminAuth(auth : BasicHttpAuth) {
	if (auth.username != polpharmaSuperadminUserName) throw NOT_EXISTS
	if (auth.password != polpharmaSuperadminPassword) throw WRONG_VALUE
}


const val polpharmaMonitorTelegramToken = "837158622:AAHDpKJr-5VhnPG9_Zi5TGijU5RBSTx6yPM"
const val polpharmaMonitorHost 			= "99.80.94.209"
const val polpharmaMonitorKey 			= "polpharma"