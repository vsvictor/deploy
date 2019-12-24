package d2.modules.coupons.api.category


import d2.modules.coupons.model.Coupon
import d2.modules.coupons.model.CouponsCategory
import ic.json.JsonArrays.toJsonArray
import ic.network.SocketAddress
import ic.network.http.HttpRequest
import ic.network.http.HttpResponse
import ic.network.http.JsonResponse
import ic.network.http.ProtectedHttpEndpoint
import ic.storage.BindingStorage
import ic.struct.collection.Collection
import ic.struct.collection.FilterCollection
import ic.text.Charset
import ic.throwables.Skip.SKIP
import org.json.JSONObject


abstract class ListCouponsCategoriesApiEndpoint : ProtectedHttpEndpoint() {

	override val name = "list"

	abstract val storage : BindingStorage

	override fun implementSafeEndpoint (socketAddress: SocketAddress, request: HttpRequest) : HttpResponse {

		val hasNotAddedCoupons : Boolean?; run {
			if (request.method == "GET") {
				hasNotAddedCoupons = null
			} else {
				val requestJson = JSONObject(Charset.DEFAULT_HTTP.bytesToString(request.body))
				if (requestJson.has("hasNotAddedCoupons")) {
					hasNotAddedCoupons = requestJson.getBoolean("hasNotAddedCoupons")
				} else {
					hasNotAddedCoupons = null
				}
			}
		}

		val couponsCategoriesMapper = CouponsCategory.getMapper(storage)

		val responseJson = JSONObject(); run {
			responseJson.put("status", "SUCCESS")
		}

		responseJson.put("coupons", toJsonArray(couponsCategoriesMapper.ids) { couponsCategoryId ->

			if (hasNotAddedCoupons != null) {
				if (
					hasNotAddedCoupons != !FilterCollection(
						FilterCollection(Coupon.getMapper(storage).items) { it.categoryId == couponsCategoryId }
					) { it.addedCount == 0 }.isEmpty
				) throw SKIP
			}

			val couponJson = JSONObject(); run {
				couponJson.put("id", couponsCategoryId)
			}

			val couponsCategory = CouponsCategory.getMapper(storage)[couponsCategoryId]

			synchronized (couponsCategory) {
				couponJson.putOpt("name", 		couponsCategory.name)
				couponJson.putOpt("imageUrl", 	couponsCategory.imageUrl)
				couponJson.putOpt("price", 		couponsCategory.price)
			}

		})

		return object : JsonResponse() {
			override val json = responseJson
		}

	}

}