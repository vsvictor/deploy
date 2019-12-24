package d2.modules.events.model;


import ic.annotations.Necessary;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;


public class StatisticsEvent extends Event {


	public final String type;

	public final String content;


	@Override public void toJson(JSONObject eventJson) {
		eventJson.put("type", type);
		eventJson.put("content", content);
	}


	// Serializable implementation:

	@Override public Class getClassToDeclare() { return StatisticsEvent.class; }


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) {
		super.serialize(json);
		json.put("type", type);
		json.put("content", content);
	}

	@Necessary public StatisticsEvent(JSONObject json) {
		super(json);
		this.type = json.getString("type");
		this.content = json.optString("content", "");
	}


	// StringMapSerializable implementation:

	@Override public void serialize(Setter1<String, String> output) {
		super.serialize(output);
		output.set("type", type);
		output.set("content", content);
	}

	@Necessary public StatisticsEvent(Getter1<String, String> input) {
		super(input);
		this.type = input.getNotNull("type");
		this.content = input.getNotNull("content");
	}


	public StatisticsEvent(@NotNull String userId, @NotNull String type, @NotNull String content) {
		super(userId);
		this.type = type;
		this.content = content;
	}

	@Deprecated public StatisticsEvent(@NotNull String type, @NotNull String content) {
		super();
		this.type = type;
		this.content = content;
	}


}
