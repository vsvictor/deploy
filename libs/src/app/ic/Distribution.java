package ic;


import ic.annotations.Necessary;
import ic.struct.list.List;
import ic.struct.map.EditableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.annotations.Overload;
import ic.interfaces.condition.Condition1;
import ic.serial.json.JsonSerializable;
import ic.serial.json.JsonSerializer;
import ic.storage.Directory;
import ic.storage.JsonStorage;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.map.CountableMap;
import ic.struct.map.UntypedCountableMap;
import ic.struct.map.UntypedCountableMap.BaseUntypedCountableMap;
import ic.struct.set.CountableSet;
import ic.struct.set.SubtractCountableSet;
import ic.throwables.NotExists;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.throwables.Skip.SKIP;


public class Distribution implements JsonSerializable {


	private static final CountableSet<String> KEYS = new CountableSet.Default<>(
		"portable",
		"storesPath",
		"packagesPath",
		"srcPath",
		"srcGroupByStore",
		"customSrcPaths",
		"binPath",
		"tmpPath",
		"jarPath",
		"launchPath",
		"buildOnUpdate",
		"projectType"
	);


	final Directory rootDirectory;


	final boolean portable;


	private final String storesPath;

	Directory getStoresDirectory() {
		return Directory.createIfNotExists(rootDirectory, storesPath);
	}

	CountableSet<String> getStoreNames() { return getStoresDirectory().getKeys(); }


	private final String packagesPath;

	Directory getPackagesDirectory() {
		return Directory.createIfNotExists(rootDirectory, packagesPath);
	}

	public CountableSet<String> getPackageNames() {
		return getPackagesDirectory().getKeys();
	}


	private final String srcPath;

	private final boolean srcGroupByStore;

	private final CountableMap<String, String> customSrcPaths;

	String getPackageSrcPath(@NotNull String storeName, @NotNull final String packageName) {
		gettingCustomDirectory: {
			final String customPath; try {
				customPath = customSrcPaths.getOrThrow(packageName);
			} catch (NotExists notExists) { break gettingCustomDirectory; }
			return customPath;
		}
		final String srcPath; {
			if (srcGroupByStore) {
				srcPath = Distribution.this.srcPath + "/" + storeName;
			} else {
				srcPath = Distribution.this.srcPath;
			}
		}
		return srcPath + "/" + packageName;
	}

	Directory getPackageSrcDirectoryOrThrow(@NotNull final String packageName) throws NotExists {
		return new ConvertCollection<>(
			getStoreNames(),
			storeName -> {
				try {
					return Directory.getExistingOrThrow(rootDirectory, getPackageSrcPath(storeName, packageName));
				} catch (NotExists notExists) { throw SKIP; }
			}
		).findOrThrow(new Condition1.True<>());
	}

	Directory getPackageSrcDirectory(@NotNull final String packageName) {
		try {
			return getPackageSrcDirectoryOrThrow(packageName);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}


	private final String binPath;

	Directory getBinDirectory() {
		return Directory.createIfNotExists(rootDirectory, binPath);
	}


	private final String tmpPath;

	Directory getTmpDirectory() {
		return Directory.createIfNotExists(rootDirectory, tmpPath);
	}


	private final String jarPath;

	Directory getJarDirectory() {
		return Directory.createIfNotExists(rootDirectory, jarPath);
	}


	private final String launchPath;

	public Directory getLaunchDirectory() {
		return Directory.createIfNotExists(rootDirectory, launchPath);
	}


	public final boolean buildOnUpdate;


	public static class ProjectType {

		public final String name;

		private ProjectType(@NotNull String name) {
			this.name = name;
		}

		public static final ProjectType GRADLE 	= new ProjectType("GRADLE");
		public static final ProjectType ANDROID = new ProjectType("ANDROID");

		public static final Collection<ProjectType> PROJECT_TYPES = new Collection.Default<>(
			GRADLE,
			ANDROID
		);

		public static @Nullable ProjectType byNullableNameOrThrow(@Nullable final String name) throws NotExists {
			if (name == null) return null;
			return PROJECT_TYPES.findOrThrow(projectType -> projectType.name.equals(name));
		}

		public static @Nullable ProjectType byNullableName(@Nullable final String name) {
			try {
				return byNullableNameOrThrow(name);
			} catch (NotExists notExists) { throw new NotExists.Runtime(); }
		}

	}

	public final @Nullable ProjectType projectType;


	public final UntypedCountableMap<String> customData;


	// JsonSerializable implementation:

	@Override public Class getClassToDeclare() { return Distribution.class; }

	@Override public void serialize(final JSONObject json) throws JSONException {

		json.put("portable", portable);

		json.put("storesPath", 		storesPath		);
		json.put("packagesPath", 	packagesPath	);
		json.put("srcPath", 		srcPath			);
		json.put("srcGroupByStore", srcGroupByStore	);
		json.put("binPath", 		binPath			);
		json.put("tmpPath", 		tmpPath			);
		json.put("jarPath", 		jarPath			);
		json.put("launchPath",		launchPath		);
		json.put("buildOnUpdate",	buildOnUpdate	);

		{ final JSONObject customSrcPathsJson = new JSONObject();
			customSrcPaths.getKeys().forEach(packageName -> customSrcPathsJson.put(packageName, customSrcPaths.getNotNull(packageName)));
			json.put("customSrcPaths", customSrcPathsJson);
		}

		if (projectType != null) json.put("projectType", projectType.name);

		customData.getKeys().forEach(key -> json.put(key, JsonSerializer.serialize(customData.get(key), true)));

	}

	@Necessary public Distribution(final JSONObject json, @NotNull Directory rootDirectory) throws JSONException {

		this.rootDirectory = rootDirectory;

		this.portable = json.optBoolean("portable", true);

		this.storesPath 	= json.getString("storesPath");
		this.packagesPath 	= json.getString("packagesPath");
		this.srcPath 		= json.getString("srcPath");

		this.srcGroupByStore = json.optBoolean("srcGroupByStore", false);

		{ final JSONObject customSrcPathsJson = json.optJSONObject("customSrcPaths");
			if (customSrcPathsJson == null) {
				customSrcPaths = new CountableMap.Default<>();
			} else {
				final EditableMap<String, String> customSrcPaths = new EditableMap.Default<>();
				for (String packageName : customSrcPathsJson.keySet()) {
					customSrcPaths.set(packageName, customSrcPathsJson.getString(packageName));
				}
				this.customSrcPaths = customSrcPaths;
			}
		}

		this.binPath 	= json.getString("binPath");
		this.tmpPath 	= json.getString("tmpPath");
		this.jarPath 	= json.getString("jarPath");
		this.launchPath	= json.getString("launchPath");

		this.buildOnUpdate = json.optBoolean("buildOnUpdate", false);

		this.projectType = ProjectType.byNullableName(json.optString("projectType", null));

		this.customData = new BaseUntypedCountableMap<>() {
			@Override public Object getObject(String key) {
				if (KEYS.contains(key)) return null;
				return JsonSerializer.parse(json.opt(key));
			}
			@Override public CountableSet<String> getKeys() {
				return new SubtractCountableSet<>(
					new CountableSet.FromJavaSet<>(json.keySet()),
					KEYS
				);
			}
		};

	}


	// Constructors:

	Distribution(

		@NotNull Directory rootDirectory,
		boolean portable,
		@NotNull String storesPath,
		@NotNull String packagesPath,
		@NotNull String srcPath,
		boolean srcGroupByStore,
		@NotNull CountableMap<String, String> customSrcPaths,
		@NotNull String binPath,
		@NotNull String tmpPath,
		@NotNull String jarPath,
		@NotNull String launchPath,
		boolean buildOnUpdate,
		@Nullable ProjectType projectType,
		@NotNull UntypedCountableMap<String> customData

	) {

		this.rootDirectory 		= rootDirectory;
		this.portable			= portable;
		this.storesPath 		= storesPath;
		this.packagesPath 		= packagesPath;
		this.srcPath 			= srcPath;
		this.srcGroupByStore	= srcGroupByStore;
		this.customSrcPaths		= customSrcPaths;
		this.binPath 			= binPath;
		this.tmpPath			= tmpPath;
		this.jarPath 			= jarPath;
		this.launchPath 		= launchPath;
		this.buildOnUpdate		= buildOnUpdate;
		this.projectType		= projectType;

		this.customData = customData;

		new JsonStorage(rootDirectory).set("distribution", this);

	}


	// Static:

	static @NotNull Distribution loadOrThrow(@NotNull Directory rootDirectory) throws NotExists {

		return new JsonStorage(rootDirectory).getOrThrow(
			"distribution",
			new List.Default<>(Directory.class),
			new List.Default<>(rootDirectory)
		);

	}

	@Overload static @NotNull Distribution load(@NotNull Directory rootDirectory) throws NotExists.Runtime {
		try {
			return loadOrThrow(rootDirectory);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}


	private static Distribution distribution;

	public @NotNull static synchronized Distribution get() {
		if (distribution == null) {
			final Directory rootDirectory; {
				final String env = System.getenv("IC_DISTRIBUTION_PATH");
				if (env == null) {
					rootDirectory = Directory.WORKDIR;
				} else {
					try {
						rootDirectory = new Directory(env);
					} catch (NotExists notExists) { throw new NotExists.Error(notExists); }
				}
			}
			try {
				distribution = loadOrThrow(rootDirectory);
			} catch (NotExists notExists) { throw new NotExists.Error(notExists); }
		}
		return distribution;
	}


}
