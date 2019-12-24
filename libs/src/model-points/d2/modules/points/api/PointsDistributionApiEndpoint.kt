package d2.modules.points.api


import d2.modules.distribution.DistributionApiEndpoint
import d2.modules.points.model.getPoints
import ic.storage.Storage
import ic.struct.collection.Collection
import ic.struct.collection.ConvertCollection


abstract class PointsDistributionApiEndpoint : DistributionApiEndpoint() {

	override val name = "distribution"

	abstract val storage : Storage
	abstract val userIds : Collection<String>

	override val data get() = ConvertCollection<Double, String>(userIds) { getPoints(storage, it).toDouble() }

}