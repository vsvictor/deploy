package ic;


import ic.annotations.Necessary;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Ordered;
import ic.apps.ic.UiCallback;
import ic.storage.Directory;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.serial.stream.StreamSerializable;
import ic.struct.list.ConvertList;
import ic.struct.list.List;
import ic.throwables.*;

import static ic.AuthKt.getAuth;
import static ic.Remove.remove;
import static ic.serial.stream.ParseKt.parseFromStream;
import static ic.serial.stream.SerializeKt.serializeToStream;
import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.url.Url.separateAuth;


public class Store implements StreamSerializable {


	public final @NotNull String name;


	private @NotNull String url;

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }


	// StreamSerializable implementation:

	@Override public @NotNull Class<? extends StreamSerializable> getClassToDeclare() { return Store.class; }

	@Override public void serialize(ByteOutput output) {
		serializeToStream(output, url);
	}

	@Necessary public Store(ByteInput input, String name) throws UnableToParse {
		this.name 	= name;
		this.url 	= parseFromStream(input);
	}


	// Constructor:

	public Store(@NotNull String name, @NotNull String url) {
		this.name 		= name;
		this.url 		= url;
	}


	// Static:

	public static @Ordered("ALPHABETIC") List<Store> getStores(@NotNull final Distribution distribution) {
		final Directory storesDirectory = distribution.getStoresDirectory();
		return new ConvertList<>(
			storesDirectory.getKeys(),
			storeName -> storesDirectory.getNotNull(
				storeName,
				new List.Default<>(String.class),
				new List.Default<>(storeName)
			)
		);
	}

	public static Store byName(@NotNull final Distribution distribution, @NotNull final String storeName) throws NotExists {
		return distribution.getStoresDirectory().getOrThrow(
			storeName,
			new List.Default<>(String.class),
			new List.Default<>(storeName)
		);
	}

	public static String getStoreNameOrThrow(@NotNull final Distribution distribution, @NotNull final String packageName) throws NotExists {
		final PackageInstance packageInstance = distribution.getPackagesDirectory().getOrThrow(packageName);
		return packageInstance.getStoreName();
	}

	public static String getStoreName(@NotNull final Distribution distribution, @NotNull final String packageName) throws NotExists.Runtime {
		try {
			return getStoreNameOrThrow(distribution, packageName);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	public static Store load(@NotNull final Distribution distribution, @NotNull final String packageName) throws NotExists {
		try {
			return byName(distribution, getStoreNameOrThrow(distribution, packageName));
		} catch (NotExists notExists) { throw new InconsistentData(notExists); }
	}

	public static void addStore(
		@NotNull final Distribution distribution,
		@NotNull final String storeName,
		@NotNull String url
	) throws AlreadyExists {

		if (distribution.getStoreNames().contains(storeName)) throw ALREADY_EXISTS;

		distribution.getStoresDirectory().set(storeName, new Store(
			storeName,
			url
		));

	}

	public static void addStore(
		@NotNull final Distribution distribution,
		@NotNull final String storeName,
		@NotNull String rawUrl,
		@NotNull UiCallback uiCallback
	) throws AlreadyExists {

		if (distribution.getStoreNames().contains(storeName)) throw ALREADY_EXISTS;

		addStore(distribution, storeName, separateAuth(rawUrl).getFirst());

		getAuth(rawUrl, "read", uiCallback);

	}

	public static void save(@NotNull final Distribution distribution, @NotNull Store store) {
		distribution.getStoresDirectory().set(store.name, store);
	}

	public static void removeStore(

		@NotNull final Distribution distribution,

		@NotNull final String storeName

	) throws NotExists, Fatal {

		if (!distribution.getStoresDirectory().contains(storeName)) throw NOT_EXISTS;

		distribution.getPackagesDirectory().getKeys().forEach(packageName -> {
			final String packageStoreName; try {
				packageStoreName = getStoreNameOrThrow(distribution, packageName);
			} catch (NotExists notExists) { throw new InconsistentData(notExists); }
			if (packageStoreName.equals(storeName)) {
				try {
					remove(
						distribution,
						packageName
					);
				} catch (NotExists notExists) {}
			}
		});

		distribution.getStoresDirectory().remove(storeName);

	}


}