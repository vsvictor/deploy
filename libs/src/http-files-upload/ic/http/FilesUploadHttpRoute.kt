package ic.http


import ic.annotations.Redirect
import ic.network.http.FolderHttpRoute
import ic.network.http.HttpRoute
import ic.storage.StreamStorage
import ic.struct.list.List


abstract class FilesUploadHttpRoute : FolderHttpRoute() {

	protected abstract val storage: StreamStorage

	override val children = List.Default<HttpRoute>(

		object : FilesUploadHttpEndpoint() {
			@Redirect
			override fun getStorage(): StreamStorage {
				return this@FilesUploadHttpRoute.storage
			}
		},

		object : FilesUploadBase64HttpEndpoint() {
			@Redirect
			override fun getStorage(): StreamStorage {
				return this@FilesUploadHttpRoute.storage
			}
		},

		object : FilesLoadHttpEndpoint() {
			@Redirect
			override fun getStorage(): StreamStorage {
				return this@FilesUploadHttpRoute.storage
			}
		},

		object : FilesDownloadHttpRoute() {
			@Redirect
			override fun getStorage(): StreamStorage {
				return this@FilesUploadHttpRoute.storage
			}
		},

		object : FilesGetBase64Endpoint() {
			@Redirect
			override fun getStorage(): StreamStorage {
				return this@FilesUploadHttpRoute.storage
			}
		}

	)

}
