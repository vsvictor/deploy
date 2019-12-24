package ic.mongodb


import ic.cmd.SystemConsole
import ic.storage.Directory
import ic.stream.ByteSequence

import ic.Storages.getCommonPublicDataStorage
import ic.network.SocketAddress
import ic.text.SplitText


@JvmField val localMongoDbStorage = object : MongoRootStorage(SocketAddress("localhost", 27017)) {

	@Synchronized
	override fun getDump() : ByteSequence {
		val tmpDir = Directory.getExisting("/tmp")
		tmpDir.removeIfExists("mongodb-dump")
		SystemConsole().writeLine("mongodump --archive > /tmp/mongodb-dump")
		val dump = tmpDir.read("mongodb-dump")
		tmpDir.remove("mongodb-dump")
		return dump
	}

	override fun prepareSystem() {
		/*
		val dataStorage = getCommonPublicDataStorage().createFolderIfNotExists("mongodb-local")
		if (dataStorage.get("mongodb-installed") { false }) return
		val systemConsole = SystemConsole()
		systemConsole.writeLine("sudo apt install -y mongodb > /dev/null")
		var error = false
		for (row in SplitText(systemConsole.read(), '\n')) {
			if (row.isEmpty) continue
			if (row.startsWith("WARNING")) {
				System.out.println("apt: $row")
				continue
			}
			else {
				System.out.println("apt: $row")
				error = true
			}
		}
		if (!error) {
			dataStorage.set("mongodb-installed", true)
		}

		 */
	}

}
