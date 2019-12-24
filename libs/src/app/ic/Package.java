package ic;


import ic.annotations.Necessary;
import ic.interfaces.getter.Safe3Getter1;
import ic.interfaces.getter.SafeGetter1;
import ic.serial.json.JsonSerializable;
import ic.storage.Directory;
import ic.storage.JsonStorage;
import ic.storage.Storage;
import ic.struct.list.Array;
import ic.struct.collection.Collection;
import ic.struct.list.List;
import ic.struct.set.CountableSet;
import ic.throwables.NotExists;
import ic.throwables.Skip;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.json.JsonArrays.*;


public class Package implements JsonSerializable {


	public final @NotNull String name;


	public static class Store {
		public final @NotNull String name;
		public final @NotNull String url;
		private Store(String name, String url) {
			this.name = name;
			this.url = url;
		}
	}

	public final Collection<Store> stores;

	public final CountableSet<String> dependencies;
	public final CountableSet<String> jarDependencies;
	public final @Nullable String main;
	public final @Nullable String testClassName;
	public final @Nullable String version;


	// JsonSerializable implementation:

	@Override public Class<?> getClassToDeclare() { return Package.class; }

	@Override public void serialize(JSONObject json) throws JSONException {
		json.put("stores", toJsonArray(stores, store -> {
			final JSONObject storeJson = new JSONObject();
			storeJson.put("name", store.name);
			storeJson.put("url", store.url);
			return storeJson;
		}));
		json.put(
			"dependencies",
			toJsonArray(
				dependencies,
				dependency -> dependency
			)
		);
		json.put(
			"jarDependencies",
			toJsonArray(
				jarDependencies,
				dependency -> dependency
			)
		);
		json.putOpt("main", main);
		json.putOpt("testClass", testClassName);
		json.putOpt("version", version);
	}

	@Necessary public Package(JSONObject json, String name) throws UnableToParse {
		this.name = name;
		{ final JSONArray storesJson = json.optJSONArray("stores");
			if (storesJson == null) {
				this.stores = new Array.Default<>();
			} else {
				this.stores = jsonArrayToList(storesJson, (Safe3Getter1<Store, JSONObject, JSONException, UnableToParse, Skip>) storeJson -> new Store(
					storeJson.getString("name"),
					storeJson.getString("url")
				));
			}
		}
		{ final JSONArray dependenciesJson = json.optJSONArray("dependencies");
			if (dependenciesJson == null) {
				this.dependencies = new CountableSet.Default<>();
			} else {
				this.dependencies = new CountableSet.Default<>(
					jsonArrayToList(
						dependenciesJson,
						(SafeGetter1<String, Object, UnableToParse>) dependencyJson -> {
							if (dependencyJson instanceof String) { final String dependency = (String) dependencyJson;
								return dependency;
							}
							else throw new UnableToParse("dependencyJson: " + dependencyJson);
						}
					)
				);
			}
		}
		{ final JSONArray jarDependenciesJson = json.optJSONArray("jarDependencies");
			if (jarDependenciesJson == null) {
				this.jarDependencies = new CountableSet.Default<>();
			} else {
				this.jarDependencies = new CountableSet.Default<>(
					jsonArrayToList(
						jarDependenciesJson,
						(SafeGetter1<String, Object, UnableToParse>) jarDependencyJson -> {
							if (jarDependencyJson instanceof String) { final String jarDependency = (String) jarDependencyJson;
								return jarDependency;
							}
							else throw new UnableToParse("jarDependencyJson: " + jarDependencyJson);
						}
					)
				);
			}
		}
		{ String main = json.optString("main", null);
			if (main == null) main = json.optString("appClass", null);
			this.main = main;
		}
		this.testClassName = json.optString("testClass", null);
		this.version = json.optString("version", null);
	}


	public Package(

		@NotNull String name,
		@NotNull Collection<Store> stores,
		@NotNull CountableSet<String> dependencies,
		@NotNull CountableSet<String> jarDependencies,
		@Nullable String main,
		@Nullable String testClassName,
		@Nullable String version

	) {

		this.name 				= name;
		this.stores 			= stores;
		this.dependencies 		= dependencies;
		this.jarDependencies 	= jarDependencies;
		this.main 				= main;
		this.testClassName		= testClassName;
		this.version 			= version;

	}


	static Package loadOrThrow(@NotNull final Distribution distribution, @NotNull final String packageName) throws NotExists {

		final Directory packageSrcDir = distribution.getPackageSrcDirectoryOrThrow(packageName);

		final Storage jsonPackageSrcDir = new JsonStorage(packageSrcDir);

		try {
			return jsonPackageSrcDir.getOrThrow("package", new List.Default<>(String.class), new List.Default<>(packageName));
		} catch (NotExists notExists) {
			return new Package(
				packageName,
				new Collection.Default<>(),
				new CountableSet.Default<>(),
				new CountableSet.Default<>(),
				null,
				null,
				null
			);
		} catch (UnableToParse.Runtime unableToParse) {
			throw new UnableToParse.Runtime(
				"packageName: " + packageName,
				unableToParse
			);
		}

	}

	static Package load(@NotNull final Distribution distribution, @NotNull final String packageName) throws NotExists.Runtime {
		try {
			return loadOrThrow(distribution, packageName);
		} catch (NotExists notExists) { throw new NotExists.Runtime(
			"distribution: " + distribution.rootDirectory.absolutePath + ", packageName: " + packageName,
			notExists
		); }
	}


	public static Package getOrThrow(@NotNull final String packageName) throws NotExists {
		return Package.loadOrThrow(Distribution.get(), packageName);
	}

	public static Package getNotNull(@NotNull final String packageName) throws NotExists.Runtime {
		try {
			return getOrThrow(packageName);
		} catch (NotExists notExists) { throw new NotExists.Runtime(
			"workdir: " + System.getProperty("user.dir") + ", distribution: " + Distribution.get().rootDirectory.absolutePath + ", packageName: " + packageName,
			notExists
		); }
	}


}
