package ic;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.storage.Directory;
import ic.storage.JsonStorage;
import ic.struct.map.CountableMap;
import ic.struct.map.UntypedCountableMap;
import ic.throwables.*;

import static ic.AuthKt.getAuth;
import static ic.Store.addStore;
import static ic.storage.Directory.WORKDIR;


public @Degenerate class Distributions { @Hide private Distributions() {}


	public static Distribution getLocalDistribution() throws Fatal {
		try {
			return Distribution.loadOrThrow(WORKDIR);
		} catch (NotExists notExists) {
			throw new Fatal(WORKDIR.absolutePath + " get not an InstantCoffee distribution");
		}
	}


	private static String getGlobalDistributionPath() { return "/ic"; }

	public static synchronized Distribution getGlobalDistribution() {

		final String globalDistributionPath = getGlobalDistributionPath();

		try {

			return Distribution.load(new Directory(globalDistributionPath));

		} catch (NotExists notExists) {

			final Directory globalDistributionDirectory = Directory.create(globalDistributionPath);

			final Distribution distribution = new Distribution(globalDistributionDirectory,
				false,
				"stores",
				"packages",
				"src",
				true,
				new CountableMap.Default<>(),
				"bin",
				"tmp",
				"jar",
				"/usr/local/bin",
				true,
				null,
				new UntypedCountableMap.Default<>()
			);

			new JsonStorage(globalDistributionDirectory).set("distribution", distribution);

			getAuth("instant-coffee.store:warm", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
			getAuth("instant-coffee.store:hot", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
			getAuth("instant-coffee.store:cold", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
			getAuth("instant-coffee.store:brewing", "read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));

			try {
				addStore(distribution, "ic-warm", 		"instant-coffee.store:warm" 	);
				addStore(distribution, "ic-hot", 		"instant-coffee.store:hot" 		);
				addStore(distribution, "ic-cold", 		"instant-coffee.store:cold" 	);
				addStore(distribution, "ic-brewing", 	"instant-coffee.store:brewing" 	);
			} catch (AlreadyExists alreadyExists) { throw new InconsistentData(alreadyExists); }

			return distribution;

		}

	}


	public static Distribution createDistribution(

		@NotNull Directory rootDirectory,
		@Nullable Distribution.ProjectType projectType

	) {

		final Distribution distribution; {

			if (projectType == null) {

				distribution = new Distribution(
					rootDirectory,
					true,
					".ic/stores",
					".ic/packages",
					"src",
					true,
					new CountableMap.Default<>(),
					".ic/bin",
					".ic/tmp",
					".ic/jar",
					"launch",
					false,
					null,
					new UntypedCountableMap.Default<>()
				);

			} else

			if (projectType == Distribution.ProjectType.GRADLE) {

				distribution = new Distribution(
					rootDirectory,
					true,
					".ic/stores",
					".ic/packages",
					"src",
					true,
					new CountableMap.Default<>(),
					".ic/bin",
					".ic/tmp",
					".ic/jar",
					"launch",
					false,
					Distribution.ProjectType.GRADLE,
					new UntypedCountableMap.Default<>()
				);

			}

			else throw new NotSupported.Runtime("projectType: " + projectType.name);

		}

		getAuth("instant-coffee.store:warm", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
		getAuth("instant-coffee.store:hot", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
		getAuth("instant-coffee.store:cold", 	"read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));
		getAuth("instant-coffee.store:brewing", "read", (url, existingAuth) -> new Pair<>("public", "ilovejava"));

		try {
			addStore(distribution, "ic-warm", 		"instant-coffee.store:warm" 	);
			addStore(distribution, "ic-hot", 		"instant-coffee.store:hot" 		);
			addStore(distribution, "ic-cold", 		"instant-coffee.store:cold" 	);
			addStore(distribution, "ic-brewing", 	"instant-coffee.store:brewing" 	);
		} catch (AlreadyExists alreadyExists) { throw new InconsistentData(alreadyExists); }

		return distribution;

	}


}
