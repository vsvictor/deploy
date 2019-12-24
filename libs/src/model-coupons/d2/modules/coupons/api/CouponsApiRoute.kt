package d2.modules.coupons.api


import d2.modules.coupons.api.category.ListCouponsCategoriesApiEndpoint
import d2.modules.coupons.api.coupon.*
import ic.network.http.BasicHttpAuth
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.BindingStorage
import ic.struct.list.List


abstract class CouponsApiRoute : FolderHttpRoute() {

	abstract fun checkServerKey(serverKey: String)
	abstract fun checkUserAuth(auth: BasicHttpAuth)

	abstract val storage : BindingStorage

	override val children = List.Default<HttpRoute>(

		FolderHttpRoute.Final( "category",

			List.Default<HttpRoute>(

				object : CreateCouponCategoryApiEndpoint() {
					override val storage get() = this@CouponsApiRoute.storage
					override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
				},

				object : ModifyCouponsCategoryApiEndpoint() {
					override val storage get() = this@CouponsApiRoute.storage
					override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
				},

				object : GetCouponsCategoryApiEndpoint() {
					override val storage get() = this@CouponsApiRoute.storage
					override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
				},

				object : ListCouponsCategoriesApiEndpoint() {
					override val storage get() = this@CouponsApiRoute.storage
					override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
				},

				object : DeleteCouponCategoryApiEndpoint() {
					override val storage get() = this@CouponsApiRoute.storage
					override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
					override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
				}

			)

		),

		object : CreateCouponApiEndpoint() {
			override val storage get() = this@CouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
		},

		object : ModifyCouponApiEndpoint() {
			override val storage get() = this@CouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
		},

		object : GetCouponApiEndpoint() {
			override val storage get() = this@CouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
		},

		object : ListCouponsApiEndpoint() {
			override val storage get() = this@CouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
		},

		object : DeleteCouponApiEndpoint() {
			override val storage get() = this@CouponsApiRoute.storage
			override fun checkServerKey(serverKey: String) 	= this@CouponsApiRoute.checkServerKey(serverKey)
			override fun checkUserAuth(auth: BasicHttpAuth) = this@CouponsApiRoute.checkUserAuth(auth)
		}

	)

}