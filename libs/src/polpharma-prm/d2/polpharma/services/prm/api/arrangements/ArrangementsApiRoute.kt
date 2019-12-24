package d2.polpharma.services.prm.api.arrangements


import d2.polpharma.services.prm.model.Arrangement
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.struct.list.List


class ArrangementsApiRoute : FolderHttpRoute() {

	override val name = "arrangements"

	override val children = List.Default<HttpRoute>(

		CreateArrangementApiEndpoint(),
		ModifyArrangementApiEndpoint(),
		DeleteArrangementApiEndpoint(),
		GetArrangementApiEndpoint(),
		ListArrangementsApiEndpoint(),

		GetArrangementUsersApiEndpoint(),
		GetUserArrangementsApiEndpoint(),

		AddArrangementUserApiEndpoint(),
		ConfirmArrangementUserApiEndpoint(),
		CancelArrangementUserApiEndpoint(),

		ArrangementPdfEndpoint(),
		ArrangementCSVEndpoint()

	)

}