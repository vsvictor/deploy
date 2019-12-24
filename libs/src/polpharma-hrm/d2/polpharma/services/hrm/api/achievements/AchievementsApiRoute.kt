package d2.polpharma.services.hrm.api.achievements


import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.struct.list.List


class AchievementsApiRoute : FolderHttpRoute() {

	override val name = "achievements"

	override val children = List.Default<HttpRoute>(
		ListAchievementsApiEndpoint(),
		NotifyAchievementApiEndpoint()
	)

}