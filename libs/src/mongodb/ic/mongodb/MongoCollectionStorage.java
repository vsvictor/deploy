package ic.mongodb;


import com.mongodb.BasicDBObject;
import com.mongodb.MongoWriteException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.DeleteOneModel;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.WriteModel;
import ic.struct.list.List;
import ic.text.Charset;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.serial.json.JsonSerializer;
import ic.storage.DatabaseStorage.BaseDatabaseStorage;
import ic.storage.Storage;
import ic.struct.collection.Collection;
import ic.struct.list.EditableList;
import ic.struct.map.UntypedCountableMap;
import ic.struct.order.OrderedCountableSet;
import ic.throwables.*;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.function.Consumer;

import static ic.struct.order.Order.ALPHABETIC_STRING_ORDER;
import static ic.throwables.AlreadyExists.ALREADY_EXISTS;
import static ic.throwables.NotExists.NOT_EXISTS;


public class MongoCollectionStorage extends BaseDatabaseStorage {


	private static final JsonWriterSettings JSON_WRITER_SETTINGS = (
		JsonWriterSettings.builder()
		.int64Converter((value, writer) -> writer.writeNumber(value.toString()))
		.build()
	);


	private final Storage parent;

	private final MongoDatabase database;

	private final String collectionName;

	private final MongoCollection<Document> collection;


	@Override public String getName() { return collectionName; }


	private String getChildCollectionName(@NotNull String key) {
		return collectionName + "/" + Charset.UTF_8.encodeUrl(key);
	}


	@Override public synchronized OrderedCountableSet<String> getKeys() {
		final MongoIterable<Document> results = collection.find().projection(Projections.include("_id"));
		return new OrderedCountableSet.Default<>(
			ALPHABETIC_STRING_ORDER,
			action -> {
				for (Document document : results) {
					action.run(document.getString("_id"));
				}
			}
		);
	}

	@Override public Storage getParent() {
		return parent;
	}

	@Override public synchronized boolean isFolder(String key) throws NotExists {
		final MongoIterable<Document> results = collection.find(new BasicDBObject("_id", key)).projection(Projections.include("_class"));
		final @Nullable Document result = results.first();
		if (result == null) throw NOT_EXISTS;
		final String className = result.getString("_class");
		if (className == null) return false;
		return className.equals(MongoCollectionStorage.class.getName());
	}

	@Override protected synchronized Storage implementGetFolder(@NotNull String key) {
		return new MongoCollectionStorage(this, database, getChildCollectionName(key));
	}

	@Override protected synchronized Object implementGetNonFolder(
		@NotNull String key, @NotNull List<Class<?>> additionalArgClasses, @NotNull List<Object> additionalArgs
	) throws NotExists {
		final Document document = collection.find(new BasicDBObject("_id", key)).first();
		if (document == null) throw NOT_EXISTS;
		final JSONObject jsonObject = new JSONObject(document.toJson(JSON_WRITER_SETTINGS));
		jsonObject.remove("_id");
		try {
			return JsonSerializer.safeParse(jsonObject, additionalArgClasses.toArray(Class.class), additionalArgs.toArray());
		} catch (UnableToParse unableToParse) { throw new UnableToParse.Runtime(unableToParse); }
	}

	@Override protected synchronized void implementSetNonFolder(@NotNull String key, @NotNull Object value) {
		database.getCollection(getChildCollectionName(key)).drop();
		final Document document = Document.parse(
			JsonSerializer.serialize(value, false).toString()
		); {
			document.put("_id", key);
		}
		final java.util.List<WriteModel<Document>> operations = new ArrayList<>(); {
			operations.add(new DeleteOneModel<>(new BasicDBObject("_id", key)));
			operations.add(new InsertOneModel<>(document));
		}
		collection.bulkWrite(operations);
	}

	@Override public synchronized void removeOrThrow(String key) throws NotExists {
		if (isFolder(key)) {
			final String childCollectionName = getChildCollectionName(key);
			database.listCollectionNames().forEach((Consumer<String>) collectionName -> {
				if (collectionName.equals(childCollectionName) || collectionName.startsWith(childCollectionName + "/")) {
					database.getCollection(collectionName).drop();
				}
			});
		}
		collection.deleteOne(new BasicDBObject("_id", key));
	}

	@Override protected synchronized Storage implementCreateFolder(@NotNull String key) throws AlreadyExists {
		final Document document = new Document();
		document.put("_id", key);
		document.put("_class", MongoCollectionStorage.class.getName());
		try {
			collection.insertOne(document);
		} catch (MongoWriteException mongoWriteException) {
			if (mongoWriteException.getMessage().contains("duplicate key error collection")) throw ALREADY_EXISTS;
		}
		return new MongoCollectionStorage(this, database, getChildCollectionName(key));
	}


	@Override public <Type> Collection<Pair<String, Type>> select(UntypedCountableMap<String> fields) {
		final FindIterable<Document> documents = collection.find(new BasicDBObject(fields.toJavaMap()));
		final EditableList<Pair<String, Object>> editableList = new EditableList.Default<>();
		documents.forEach((Consumer<? super Document>) document -> {
			final Object value; {
				final JSONObject jsonObject = new JSONObject(document.toJson(JSON_WRITER_SETTINGS));
				jsonObject.remove("_id");
				value = JsonSerializer.parse(jsonObject);
			}
			editableList.add(new Pair<>(
				document.getString("_id"),
				value
			));
		});
		@SuppressWarnings("unchecked") final Collection<Pair<String, Type>> result = (EditableList<Pair<String, Type>>) (EditableList) editableList;
		return result;
	}


	MongoCollectionStorage(@NotNull Storage parent, @NotNull MongoDatabase database, @NotNull String collectionName) {

		this.parent			= parent;
		this.database 		= database;
		this.collectionName = collectionName;

		this.collection = database.getCollection(collectionName);

	}


}
