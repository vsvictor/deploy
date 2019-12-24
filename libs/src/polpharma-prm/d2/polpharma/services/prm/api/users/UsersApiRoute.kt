package d2.polpharma.services.prm.api.users


import d2.polpharma.services.prm.model.PRMFishki
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.struct.list.List


class UsersApiRoute : FolderHttpRoute() {


	override val name = "users"


	override val children = List.Default<HttpRoute>(

		UpdateQuadrasoftUsersEndpoint(),
		GetRegistrationCodesCsvEndpoint(),
		RegisterUserEndpoint(),
		GetUserEndpoint(),
		ListUsersApiEndpoint(),
		ListUsersCsvApiEndpoint(),
		ModifyUserEndpoint(),
		AddIndividual(),
		GetIndividual(),
		GetAllIndividual(),
		ModifyIndividual(),
		AddAgreement(),
		GetAgreement(),
		ModifyAgreement(),
		GetConfirmAgreement(),
		GetIDFromQuadroID(),
		PutFishki(),
		GetFishki(),
		GetAllFishki()
	)


}