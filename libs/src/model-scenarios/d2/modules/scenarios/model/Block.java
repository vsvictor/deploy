package d2.modules.scenarios.model;


import ic.annotations.Necessary;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.json.JsonSerializable;
import ic.serial.json.JsonSerializer;
import ic.serial.stringmap.StringMapSerializable;
import ic.serial.stringmap.StringMapSerializer;
import ic.struct.list.List;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;


public class Block implements JsonSerializable, StringMapSerializable {


	public final String id;


	public final String type;

	public final String content;

	public final String keyboardType;


	public static class Way implements JsonSerializable, StringMapSerializable {


		public final String blockId;

		public final int score;


		// Serializable implementation:

		@Override public Class getClassToDeclare() { return Way.class; }


		// JsonSerializable implementation:

		@Override public void serialize(JSONObject json) throws JSONException {
			json.put("blockId", blockId);
			json.put("score", score);
		}

		@Necessary public Way(JSONObject json) {
			this.blockId 	= json.getString("blockId");
			this.score		= json.getInt("score");
		}


		// StringMapSerializable implementation:

		@Override public void serialize(Setter1<String, String> output) {
			output.set("blockId", blockId);
			output.set("score", 	Integer.toString(score));
		}

		@Necessary public Way(Getter1<String, String> input) {
			this.blockId 	= input.get("blockId");
			this.score		= Integer.parseInt(input.getNotNull("score"));
		}


		public Way(String blockId, int score) {
			this.blockId = blockId;
			this.score = score;
		}


	}

	public final List<Way> ways;


	// Serializable implementation:

	@Override public Class getClassToDeclare() { return Block.class; }


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) throws JSONException {
		json.put("id", id == null ? "null" : id);
		json.put("type", type);
		json.put("content", content);
		json.put("keyboardType", keyboardType);
		json.put("ways", JsonSerializer.serialize(ways, true));
	}

	@Necessary public Block(JSONObject json) throws UnableToParse {
		this.id = json.getString("id");
		this.type = json.getString("type");
		this.content = json.getString("content");
		this.keyboardType = json.getString("keyboardType");
		this.ways = JsonSerializer.parse(json.get("ways"));
	}


	// StringMapSerializable implementation:

	@Override public void serialize(Setter1<String, String> output) {
		output.set("id", id);
		output.set("type", type);
		output.set("content", content);
		output.set("keyboardType", keyboardType);
		StringMapSerializer.write(output, "ways", ways);
	}

	@Necessary public Block(Getter1<String, String> input) throws UnableToParse {
		this.id = input.get("id");
		this.type = input.get("type");
		this.content = input.get("content");
		this.keyboardType = input.get("keyboardType");
		this.ways = StringMapSerializer.read(input, "ways");
	}


	// Constructor:

	public Block(@NotNull String id, @NotNull String type, @NotNull String content, @NotNull String keyboardType, @NotNull List<Way> ways) {
		this.id = id;
		this.type = type;
		this.content = content;
		this.keyboardType = keyboardType;
		this.ways = ways;
	}


}
