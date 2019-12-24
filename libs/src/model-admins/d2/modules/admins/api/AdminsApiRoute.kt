package d2.modules.admins.api


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.Storage
import ic.struct.list.List


abstract class AdminsApiRoute : FolderHttpRoute() {

	abstract fun checkSuperadminAuth (auth: BasicHttpAuth)

	abstract val storage : Storage

	override val children = List.Default<HttpRoute>(

		object : CreateAdminApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		},

		object : GetMyselfApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		},

		object : ListAdminsApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		},

		object : DeleteAdminApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		},

		object : ModifyAdminProfileApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		},

		object : SetAdminPasswordApiEndpoint() {
			override val storage = this@AdminsApiRoute.storage
			override fun checkSuperadminAuth (auth: BasicHttpAuth) = this@AdminsApiRoute.checkSuperadminAuth(auth)
		}

	)

}