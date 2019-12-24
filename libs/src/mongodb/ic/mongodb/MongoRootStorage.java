package ic.mongodb;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ic.annotations.DoesNothing;
import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.annotations.ToOverride;
import ic.interfaces.closeable.Closeable;
import ic.network.SocketAddress;
import ic.storage.Storage;
import ic.stream.ByteSequence;
import ic.struct.list.List;
import ic.struct.order.OrderedCountableSet;
import ic.throwables.IOException;
import ic.throwables.InvalidValue;
import ic.throwables.NotExists;
import ic.throwables.NotImplementedYet;
import org.jetbrains.annotations.NotNull;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.NotExists.NOT_EXISTS;


public class MongoRootStorage extends Storage.BaseStorage implements Closeable {

	final @NotNull MongoClient mongoClient;

	@Override public OrderedCountableSet<String> getKeys() {
		return new OrderedCountableSet.Default<>(
			ALPHABETIC_STRING_ORDER,
			mongoClient.listDatabaseNames()
		);
	}

	@Override public Storage getParent() {
		return null;
	}

	@Override public String getName() { return "mongodb:/"; }

	@Override public boolean isFolder(String key) throws NotExists {
		if (getKeys().contains(key)) {
			return true;
		} else {
			throw NOT_EXISTS;
		}
	}

	@Override protected Storage implementGetFolder(String key) {
		return new MongoDatabaseStorage(this, key);
	}

	@Override protected Object implementGetNonFolder(String key, List<Class<?>> additionalArgClasses, List<Object> additionalArgs) throws NotExists {
		throw new InvalidValue.Runtime("MongoRootStorage can contain only folders");
	}

	@Override protected void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
		throw new InvalidValue.Runtime("MongoRootStorage can contain only folders");
	}

	@Override public void removeOrThrow(String key) throws NotExists {
		final MongoDatabase mongoDatabase = mongoClient.getDatabase(key);
		if (mongoDatabase == null) throw NOT_EXISTS;
		mongoDatabase.drop();
	}

	@Override protected Storage implementCreateFolder(String key) {
		return new MongoDatabaseStorage(this, key);
	}

	@Override public void close() {
		mongoClient.close();
	}

	@ToOverride @DoesNothing protected void prepareSystem() {} { prepareSystem(); }


	@Narrowing @Repeat @Override public @NotNull synchronized MongoDatabaseStorage createFolderIfNotExists(String key) throws InvalidValue.Runtime {
		return (MongoDatabaseStorage) super.createFolderIfNotExists(key);
	}

	public ByteSequence getDump() {
		throw new NotImplementedYet();
	}


	public MongoRootStorage(SocketAddress socketAddress) {
		MongoClient mongoClient = null;
		for (int i = 0; i < 4; i++) {
			try {
				mongoClient = MongoClients.create("mongodb://" + socketAddress.host + ":" + socketAddress.port);
				mongoClient.listDatabaseNames();
				break;
			} catch (Throwable throwable) { throwable.printStackTrace(); }
		}
		if (mongoClient == null) throw new IOException.Runtime(
			"Can't connect to MongoDB " + socketAddress.host + ":" + socketAddress.port
		);
		this.mongoClient = mongoClient;
	}

}
