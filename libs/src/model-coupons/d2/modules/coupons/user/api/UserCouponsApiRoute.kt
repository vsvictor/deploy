package d2.modules.coupons.user.api


import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.BindingStorage
import ic.struct.list.List


abstract class UserCouponsApiRoute : FolderHttpRoute() {

	abstract fun checkServerKey(serverKey: String)
	abstract fun checkUserAuth(auth: BasicHttpAuth)

	abstract val storage : BindingStorage

	override val children = List.Default<HttpRoute>(

		object : ListUserCouponsApiEndpoint() {
			override val storage = this@UserCouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) = this@UserCouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@UserCouponsApiRoute.checkUserAuth(auth)
		},

		object : AddUserCouponApiEndpoint() {
			override val storage = this@UserCouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) = this@UserCouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@UserCouponsApiRoute.checkUserAuth(auth)
		},

		object : UseCouponApiEndpoint() {
			override val storage = this@UserCouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) = this@UserCouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@UserCouponsApiRoute.checkUserAuth(auth)
		}

	)

}