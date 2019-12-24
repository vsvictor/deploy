package d2.polpharma.services.ophtik.api.users


import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.struct.list.List


class UsersApiRoute : FolderHttpRoute() {

	override val name = "user"

	override val children = List.Default<HttpRoute>(

		CreateUserApiEndpoint(),
		ModifyUserApiEndpoint(),
		GetUserApiEndpoint(),
		ListUsersApiEndpoint(),
		CountUsersApiEndpoint(),
		DeleteUserApiEndpoint(),
		UserIdsApiEndpoint()

	)

}