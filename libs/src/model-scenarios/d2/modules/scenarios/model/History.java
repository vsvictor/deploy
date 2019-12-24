package d2.modules.scenarios.model;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.id.Mapper;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.json.JsonSerializable;
import ic.serial.json.JsonSerializer;
import ic.serial.stringmap.StringMapSerializable;
import ic.serial.stringmap.StringMapSerializer;
import ic.storage.BindingStorage;
import ic.storage.CacheStorage;
import ic.storage.Storage;
import ic.struct.list.EditableList;
import ic.struct.list.IndexOutOfBounds;
import ic.struct.list.List;
import ic.struct.map.CountableMap;
import ic.throwables.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import static d2.modules.scenarios.model.Scenario.getScenariosMapper;
import static d2.modules.scenarios.model.Subject.*;
import static ic.date.Millis.*;
import static ic.util.Hex.hexStringToLong;
import static ic.util.Hex.longToFixedSizeHexString;


public class History implements JsonSerializable, StringMapSerializable, Changeable {


	public final BindingStorage storage;

	public final String userId, subjectId, scenarioId;


	public static class Item implements JsonSerializable, StringMapSerializable {

		public final Date date;

		public final @Nullable Integer way;

		public final int score;

		public final String blockId;


		// Serializable implementation:

		@Override public Class getClassToDeclare() { return Item.class; }


		// JsonSerializable implementation:

		@Override public void serialize(JSONObject json) {
			json.putOpt("date", nullableDateToMillis(date));
			json.putOpt("way", way);
			json.put("score", score);
			json.put("blockId", blockId);
		}

		@Necessary public Item(JSONObject json) {
			{ final long dateMillis = json.optLong("date", Long.MIN_VALUE);
				if (dateMillis == Long.MIN_VALUE) this.date = null;
				else this.date = millisToDate(dateMillis);
			}
			{ final int way = json.optInt("way", Integer.MIN_VALUE);
				if (way == Integer.MIN_VALUE) this.way = null;
				else this.way = way;
			}
			this.score 		= json.getInt("score");
			this.blockId 	= json.getString("blockId");
		}


		// StringMapSerializable implementation:

		@Override public void serialize(Setter1<String, String> output) {
			if (date != null) output.set("date", longToFixedSizeHexString(dateToMillis(date)));
			if (way != null) output.set("way", Integer.toString(way));
			output.set("score", Integer.toString(score));
			output.set("blockId", blockId);
		}

		@Necessary public Item(Getter1<String, String> input) {
			{ final String dateString = input.get("date");
				if (dateString == null) this.date = null;
				else 					this.date = millisToDate(hexStringToLong(dateString));
			}
			{ final String wayString = input.get("way");
				if (wayString == null) 	way = null;
				else 					way = Integer.parseInt(wayString);
			}
			this.score 		= Integer.parseInt(input.get("score"));
			this.blockId 	= input.get("blockId");
		}


		public Item(@Nullable Integer way, int score, String blockId) {
			this.date 		= now();
			this.way 		= way;
			this.score 		= score;
			this.blockId 	= blockId;
		}

	}


	private final EditableList<Item> items;

	public List<Item> getItems() {
		return new List.Default<>(items);
	}


	public synchronized Block getLastBlock() throws Empty {
		final Scenario scenario = Scenario.getScenariosMapper(storage, subjectId).getItem(scenarioId);
		try {
			return scenario.getBlockByIdOrThrow(items.getLastOrThrow().blockId);
		} catch (Empty | NotExists empty) {
			final Block block = scenario.getBlocks().getFirst();
			items.add(new Item(-1, 0, block.id));
			changeEvent.run();
			return block;
		}
	}

	public synchronized boolean isFirstTime() {
		final String firstBlockId; {
			final Scenario scenario = getScenariosMapper(storage, subjectId).getItem(scenarioId);
			try {
				firstBlockId = scenario.getBlocks().get(0).id;
			} catch (Empty empty) {
				return true;
			}
		}
		final int itemsCount = items.getCount();
		if (itemsCount <= 0) return true;
		for (int i = itemsCount - 1; i >= 0; i--) { final Item item = items.get(i);
			if (item.blockId.equals(firstBlockId)) return i == 0;
		} throw new InconsistentData("firstItem.blockId: " + items.get(0).blockId + ", firstBlockId: " + firstBlockId);
	}

	public synchronized int getCurrentTimeScore() {
		final String firstBlockId; {
			final Scenario scenario = getScenariosMapper(storage, subjectId).getItem(scenarioId);
			try {
				firstBlockId = scenario.getBlocks().get(0).id;
			} catch (Empty empty) {
				return 0;
			}
		}
		final int itemsCount = items.getCount();
		if (itemsCount == 0) return 0;
		int score = 0;
		for (int i = itemsCount - 1; i >= 0; i--) { final Item item = items.get(i);
			if (item.blockId.equals(firstBlockId)) return score;
			score += item.score;
		} throw new InconsistentData("firstItem.blockId: " + items.get(0).blockId + ", firstBlockId: " + firstBlockId);
	}

	public synchronized boolean contains(String blockId, int way) {
		for (int i = 1; i < items.getCount(); i++) {
			if (items.get(i).way != way) continue;
			if (items.get(i - 1).blockId.equals(blockId)) return true;
		} return false;
	}


	public static abstract class GoCallback {
		protected abstract void onFinish(String userId, String subjectId, String scenarioId, boolean firstTime, int currentTimeScore);
	}
	public synchronized void go(int wayIndex, GoCallback callback) throws Empty {
		final boolean firstTime = isFirstTime();
		final int currentTimeScore = getCurrentTimeScore();
		final Scenario scenario = getScenariosMapper(storage, subjectId).getItem(scenarioId);
		final String firstBlockId = scenario.getBlocks().getFirst().id;
		final Item lastItem; {
			Item value;
			try {
				value = items.getLastOrThrow();
			} catch (Empty empty) {
				final Item newItem = new Item(-1, 0, scenario.getBlocks().getFirst().id);
				items.add(newItem);
				value = newItem;
			}
			lastItem = value;
		}
		try {
			final Block lastItemBlock = scenario.getBlockByIdOrThrow(lastItem.blockId);
			final Block.Way wayObject = lastItemBlock.ways.safeGet(wayIndex);
			final Item newItem = new Item(wayIndex, wayObject.score, wayObject.blockId);
			items.add(newItem);
			if (wayObject.blockId.equals(firstBlockId)) {
				callback.onFinish(userId, subjectId, scenarioId, firstTime, currentTimeScore);
			}
		} catch (NotExists notFound) {
			final Item newItem = new Item(-1, 0, scenario.getBlocks().getFirst().id);
			items.add(newItem);
		} catch (IndexOutOfBounds indexOutOfBounds) {
			final Item newItem = new Item(-1, 0, scenario.getBlocks().getFirst().id);
			items.add(newItem);
			callback.onFinish(userId, subjectId, scenarioId, firstTime, currentTimeScore);
		}
		changeEvent.run();
	}


	// Serializable implementation:

	@Override public Class getClassToDeclare() { return History.class; }


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) throws JSONException {
		json.put("items", JsonSerializer.serialize(items, true));
	}

	@SuppressWarnings("unchecked")
	@Necessary public History(JSONObject json) throws UnableToParse {
		this.storage = null;
		this.userId = null;
		this.subjectId = null;
		this.scenarioId = null;
		{ Object items = JsonSerializer.parse(json.get("items"));
			if (items instanceof EditableList) {
				this.items = (EditableList<Item>) items;
			} else
			if (items instanceof CountableMap) {
				this.items = new EditableList.Default<>();
				int count = ((CountableMap) items).getCount();
				for (int i = 0; i < count; i++) {
					this.items.add((Item) ((CountableMap) items).get(i));
				}
			}
			else throw new WrongType.Runtime(items);
		}

	}

	@Necessary public History(JSONObject json, BindingStorage storage, String userId, String subjectId, String scenarioId) throws UnableToParse {
		this.storage = storage;
		this.userId = userId;
		this.subjectId = subjectId;
		this.scenarioId = scenarioId;
		this.items = JsonSerializer.parse(json.get("items"));
	}


	// StringMapSerializable implementation:

	@Override public void serialize(Setter1<String, String> output) {
		StringMapSerializer.write(output, "items", items);
	}

	@Necessary public History(Getter1<String, String> input, BindingStorage storage, String userId, String subjectId, String scenarioId) throws UnableToParse {
		this.storage = storage;
		this.userId = userId;
		this.subjectId = subjectId;
		this.scenarioId = scenarioId;
		this.items = StringMapSerializer.read(input, "items");
	}

	@Necessary public History(Getter1<String, String> input) throws UnableToParse {
		this.storage = null;
		this.userId = null;
		this.subjectId = null;
		this.scenarioId = null;
		this.items = StringMapSerializer.read(input, "items");
	}


	// Changeable implementation:

	private final Event.Trigger changeEvent = new Event.Trigger(); @Override public Event getChangeEvent() { return changeEvent; }


	// Constructor:

	private History(@NotNull BindingStorage storage, @NotNull String userId, @NotNull String subjectId, @NotNull String scenarioId) {
		this.storage = storage;
		this.userId = userId;
		this.subjectId = subjectId;
		this.scenarioId = scenarioId;
		this.items = new EditableList.Default<>();
	}


	// Static:

	private static CacheStorage getHistoriesStorage(@NotNull CacheStorage storage) {
		return storage.createFolderIfNotExists("histories");
	}

	public static synchronized History getHistory(
		@NotNull BindingStorage storage,
		@NotNull final String userId,
		@NotNull final String subjectId,
		@NotNull final String scenarioId
	) {

		final Storage historiesStorage = storage.createFolderIfNotExists("histories");

		{
			final int version = storage.get("version", 0);
			if (version == 0) {
				historiesStorage.getKeys().forEach(historyUserId -> {
					System.out.println("SCENARIOS HISTORY MIGRATE USER " + historyUserId);
					final Storage historiesBySubjectIdAndScenarioId; try {
						historiesBySubjectIdAndScenarioId = historiesStorage.getNotNull(historyUserId);
					} catch (ClassCastException e) { return; }
					historiesBySubjectIdAndScenarioId.getKeys().forEach(historySubjectId -> {
						System.out.println("SCENARIOS HISTORY MIGRATE SUBJECT " + historyUserId + "/" + historySubjectId);
						final Storage historiesByScenarioId; try {
							historiesByScenarioId = historiesBySubjectIdAndScenarioId.getNotNull(historySubjectId);
						} catch (ClassCastException e) { return; }
						historiesByScenarioId.getKeys().forEach(historyScenarioId -> {
							System.out.println("SCENARIOS HISTORY MIGRATE SCENARIO " + historyUserId + "/" + historySubjectId + "/" + historyScenarioId);
							historiesStorage.set(
								historyUserId + "/" + historySubjectId + "/" + historyScenarioId,
								historiesByScenarioId.getNotNull(historyScenarioId)
							);
							try { Thread.sleep(256); } catch (InterruptedException e) {}
						});
					});
					historiesStorage.remove(historyUserId);
				});
				storage.set("version", 1);
			}
		}

		final String historyFileName = userId + "/" + subjectId + "/" + scenarioId;

		if (historiesStorage.contains(historyFileName)) {

			return historiesStorage.getNotNull(
				historyFileName,
				new List.Default<>( BindingStorage.class, String.class, String.class, String.class ),
				new List.Default<>( storage, userId, subjectId, scenarioId )
			);

		} else {

			final History history = new History(storage, userId, subjectId, scenarioId);
			historiesStorage.set(historyFileName, history);
			return history;

		}

	}

	public static boolean isScenarioPassed(
		@NotNull final BindingStorage storage,
		@NotNull final String userId,
		@NotNull final String subjectId,
		@NotNull final String scenarioId
	) {
		return !getHistory(storage, userId, subjectId, scenarioId).isFirstTime();
	}

	public static boolean isSubjectPassed(
		@NotNull final BindingStorage storage,
		@NotNull final String userId,
		@NotNull final Subject subject
	) {
		final String subjectId = getSubjectsMapper(storage).getId(subject);
		for (String scenarioId : getScenariosMapper(storage, subjectId).getIds()) {
			if (!isScenarioPassed(storage, userId, subjectId, scenarioId)) return false;
		} return true;
	}

	public static int getSubjectsPassedCount(
		@NotNull final BindingStorage storage,
		@NotNull final String userId
	) {
		int subjectsPassed = 0;
		for (Subject subject : getSubjectsMapper(storage).getItems()) {
			if (isSubjectPassed(storage, userId, subject)) subjectsPassed++;
		}
		return subjectsPassed;
	}

	public static int getScenariosPassedCount(
		@NotNull final BindingStorage storage,
		@NotNull final String userId
	) {
		int scenariosPassed = 0;
		final Mapper<Subject, String> subjectsMapper = getSubjectsMapper(storage);
		for (String subjectId : subjectsMapper.getIds()) {
			for (String scenarioId : getScenariosMapper(storage, subjectId).getIds()) {
				if (isScenarioPassed(storage, userId, subjectId, scenarioId)) scenariosPassed++;
			}
		}
		return scenariosPassed;
	}

	/*public static Collection<History> getHistoriesByScenario(@NotNull final BindingStorage storage, @NotNull final String subjectId, @NotNull final String scenarioId) {
		final Storage historiesStorage = getHistoriesStorage(storage);
		return new List.FromIterable<>(
			new ConvertCollection<>(
				historiesStorage.getKeys(),
				userId -> getHistory(storage, userId, subjectId, scenarioId)
			)
		);
	}*/


}
