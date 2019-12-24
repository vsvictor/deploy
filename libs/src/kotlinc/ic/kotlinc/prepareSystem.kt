package ic.kotlinc


import ic.Storages.getCommonPublicDataStorage
import ic.cmd.SystemConsole
import ic.cmd.Sudo


internal fun prepareSystem() {

	getCommonPublicDataStorage().createFolderIfNotExists("kotlinc").createIfNull("kotlin-installed") {
		val systemConsole = Sudo(SystemConsole())
		systemConsole.writeLine("snap install --classic kotlin")
		true
	}

}
