package d2.modules.admins


import d2.modules.admins.model.Admin
import ic.network.http.BasicHttpAuth
import ic.storage.Storage
import ic.throwables.AccessDenied
import ic.throwables.NotExists
import ic.throwables.WrongValue


@Throws(AccessDenied::class, NotExists::class, WrongValue::class)
fun checkAdminAuth (
	checkSuperadminAuth : (BasicHttpAuth) -> Unit,
	adminsStorage : Storage,
	auth : BasicHttpAuth
) {
	try {
		checkSuperadminAuth(auth)
	} catch (notExists : NotExists) {
		val admin : Admin = adminsStorage.getOrThrow(auth.username)
		if (auth.password != admin.password) throw WrongValue.WRONG_VALUE
	}
}