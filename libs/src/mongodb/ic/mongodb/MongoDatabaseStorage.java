package ic.mongodb;


import com.mongodb.client.MongoDatabase;
import ic.annotations.Narrowing;
import ic.struct.collection.Collection;
import ic.struct.collection.ConvertCollection;
import ic.struct.list.List;
import ic.text.Charset;
import org.jetbrains.annotations.NotNull;
import ic.storage.Storage;
import ic.struct.order.OrderedCountableSet;
import ic.throwables.InvalidValue;
import ic.throwables.NotExists;

import java.util.function.Consumer;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.Skip.SKIP;


public class MongoDatabaseStorage extends Storage.BaseStorage {


	private final MongoRootStorage parent;

	private final String databaseName;

	private final MongoDatabase database;


	@Override public synchronized OrderedCountableSet<String> getKeys() {
		return new OrderedCountableSet.Default<>(
			ALPHABETIC_STRING_ORDER,
			new ConvertCollection<String, String>(
				new Collection.Default<String>(database.listCollectionNames()),
				collectionName -> {
					if (collectionName.contains("/")) throw SKIP;
					return Charset.UTF_8.decodeUrl(collectionName);
				}
			)
		);
	}

	@Override public Storage getParent() {
		return parent;
	}

	@Override public synchronized boolean isFolder(String key) throws NotExists {
		if (getKeys().contains(key)) {
			return true;
		} else {
			throw NOT_EXISTS;
		}
	}



	@Override public String getName() { return databaseName; }

	@Override protected synchronized Storage implementGetFolder(@NotNull String key) {
		return new MongoCollectionStorage(this, database, Charset.UTF_8.encodeUrl(key));
	}

	@Override protected synchronized Object implementGetNonFolder(
		@NotNull String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) { throw new InvalidValue.Runtime("MongoDatabaseStorage can contain only folders"); }

	@Override protected synchronized void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
		throw new InvalidValue.Runtime("MongoDatabaseStorage can contain only folders");
	}

	@Override public synchronized void removeOrThrow(String key) {
		final String encodedKey = Charset.UTF_8.encodeUrl(key);
		database.listCollectionNames().forEach((Consumer<String>) collectionName -> {
			if (collectionName.equals(encodedKey) || collectionName.startsWith(encodedKey + "/")) {
				database.getCollection(collectionName).drop();
			}
		});
	}

	@Override protected synchronized Storage implementCreateFolder(@NotNull String key) {
		return new MongoCollectionStorage(this, database, Charset.UTF_8.encodeUrl(key));
	}


	// Narrowing methods:

	@NotNull @Narrowing @Override public MongoCollectionStorage createFolderIfNotExists(@NotNull String key) { return (MongoCollectionStorage) super.createFolderIfNotExists(key); }


	MongoDatabaseStorage(@NotNull MongoRootStorage parent, @NotNull String databaseName) {
		this.parent 		= parent;
		this.databaseName 	= databaseName;
		this.database 		= parent.mongoClient.getDatabase(databaseName);
	}


}
