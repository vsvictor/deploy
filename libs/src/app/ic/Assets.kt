package ic


import ic.storage.MergeStreamStorage
import ic.storage.StreamStorage
import ic.struct.list.ReverseList
import ic.struct.sequence.ConvertSequence
import ic.throwables.NotExists

import ic.throwables.Skip.SKIP


class Assets (folderName : String) : MergeStreamStorage (

	ConvertSequence<StreamStorage, String>(
		ReverseList(
			app.includedPackageNames
		)
	) { packageName ->
		try {
			Distribution.get().getPackageSrcDirectory(packageName).getFolderOrThrow(folderName)
		} catch (notExists: NotExists) {
			throw SKIP
		}
	}

) {

	companion object {

		@JvmField val resources = Assets("res")

	}

}