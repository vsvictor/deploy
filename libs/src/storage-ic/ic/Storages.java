package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.storage.Directory;
import ic.throwables.NotExists;

import static ic.AppKt.getApp;
import static ic.storage.Directory.getPublicIcPath;


public @Degenerate class Storages { @Hide private Storages() {}


	// Public:

	private static volatile Directory publicIcDir;

	private static synchronized Directory getPublicIcDir() {
		if (publicIcDir == null) {
			try {
				publicIcDir = Directory.getExistingOrThrow(getPublicIcPath());
			} catch (NotExists notExists) {
				if (System.getProperty("user.name").equals("root")) {
					publicIcDir = Directory.create(getPublicIcPath());
				} else {
					publicIcDir = Directory.create("~/.ic/public");
				}
			}
		}
		return publicIcDir;
	}


	private static Directory publicDataStorage;

	public static synchronized Directory getPublicDataStorage() {
		if (publicDataStorage == null) {
			publicDataStorage = getPublicIcDir().createFolderIfNotExists("data").createFolderIfNotExists(getApp().getPackageName());
		}
		return publicDataStorage;
	}


	private static Directory commonPublicDataStorage;

	public static synchronized Directory getCommonPublicDataStorage() {
		if (commonPublicDataStorage == null) {
			commonPublicDataStorage = getPublicIcDir().createFolderIfNotExists("data").createFolderIfNotExists("common");
		}
		return commonPublicDataStorage;
	}


	// User:

	private static volatile Directory userIcDir;

	public static synchronized Directory getUserIcDir() {
		if (userIcDir == null) {
			userIcDir = Directory.createIfNotExists(System.getProperty("user.home") + "/.ic");
		}
		return userIcDir;
	}


	private static Directory commonDataStorage;

	public static synchronized Directory getCommonDataStorage() {
		if (commonDataStorage == null) {
			commonDataStorage = getUserIcDir().createFolderIfNotExists("data").createFolderIfNotExists("common");
		}
		return commonDataStorage;
	}


	private static Directory configStorage;

	public static synchronized Directory getConfigStorage() {
		if (configStorage == null) {
			configStorage = getUserIcDir().createFolderIfNotExists("config").createFolderIfNotExists(getApp().getPackageName());
		}
		return configStorage;
	}


	private static volatile Directory logsStorage;

	public static synchronized Directory getLogsStorage() {
		if (logsStorage == null) {
			logsStorage = getUserIcDir().createFolderIfNotExists("logs").createFolderIfNotExists(getApp().getPackageName());
		}
		return logsStorage;
	}


}
