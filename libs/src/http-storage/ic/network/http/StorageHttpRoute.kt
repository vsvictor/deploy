package ic.network.http


import ic.annotations.ToOverride
import ic.mimetypes.MimeType
import ic.network.SocketAddress
import ic.storage.StreamStorage
import ic.stream.ByteInput
import ic.stream.ByteSequence
import ic.throwables.NotExists
import ic.throwables.NotSupported
import ic.throwables.UnableToParse

import ic.mimetypes.MimeType.MIME_TYPES


abstract class StorageHttpRoute : HttpRoute() {


	protected abstract fun getStorage(path: String): StreamStorage

	@ToOverride
	protected open fun modifyHead(head: String): String {
		return head
	}


	@Throws(NotSupported::class, UnableToParse::class)
	override fun implementRoute(socketAddress: SocketAddress, request: HttpRequest, root: Boolean): HttpResponse {

		try {

			val contentType: MimeType
			val hasExtension: Boolean
			run {
				var contentTypeValue: MimeType
				var hasExtensionValue: Boolean
				try {
					contentTypeValue = MIME_TYPES.findOrThrow { mimeType -> request.path.endsWith("." + mimeType.extension) }
					hasExtensionValue = true
				} catch (notFound: NotExists) {
					contentTypeValue = MimeType.HTML
					hasExtensionValue = false
				}

				contentType = contentTypeValue
				hasExtension = hasExtensionValue
			}

			var storage = getStorage(request.path)

			var modifiedHead = modifyHead(request.path)

			if (modifiedHead.isEmpty()) {
				modifiedHead = "index.html"
			} else if (!hasExtension) {
				modifiedHead += ".html"
			}

			val byteInput: ByteInput
			run {
				val pathSplit = modifiedHead.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
				for (i in 0 until pathSplit.size - 1) {
					storage = storage.getOrThrow<StreamStorage>(pathSplit[i])
				}
				byteInput = storage.getInput(pathSplit[pathSplit.size - 1])
			}

			val responseBody = byteInput.read()

			return object : HttpResponse {
				override val contentType: MimeType
					get() = contentType
				override val body: ByteSequence
					get() = responseBody
			}

		} catch (noSuchFile: NotExists) {

			throw NotSupported(noSuchFile)

		}

	}


}
