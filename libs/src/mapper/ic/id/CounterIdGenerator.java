package ic.id;


import ic.annotations.Necessary;
import ic.event.Event;
import ic.interfaces.changeable.Changeable;
import ic.interfaces.getter.Getter1;
import ic.interfaces.setter.Setter1;
import ic.serial.json.JsonSerializable;
import ic.stream.ByteInput;
import ic.stream.ByteOutput;
import ic.serial.stream.StreamSerializer;
import ic.serial.stringmap.StringMapSerializable;
import ic.throwables.UnableToParse;
import ic.serial.stream.StreamSerializable;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.util.Hex.safeHexStringToLong;
import static ic.util.Hex.longToFixedSizeHexString;


public abstract class CounterIdGenerator<Id> extends IdGenerator<Id> implements StreamSerializable, StringMapSerializable, JsonSerializable, Changeable {


	private volatile long nextCounter;

	protected abstract Id generateId(long counter);

	@Override public synchronized final Id get() {
		final long counter = nextCounter++;
		changeEvent.run();
		return generateId(counter);
	}


	// Changeable implementation:

	private final Event.Trigger changeEvent = new Event.Trigger();

	@Override public Event getChangeEvent() { return changeEvent; }


	// StreamSerializable implementation:

	@Override public void serialize(ByteOutput output) {
		StreamSerializer.write(output, nextCounter);
	}

	@Necessary public CounterIdGenerator(ByteInput input) throws UnableToParse {
		{ // Backward compatibility:
			Object nextCounter = StreamSerializer.read(input);
			if (nextCounter instanceof Long) 	this.nextCounter = (long) nextCounter; 			else
			if (nextCounter instanceof Integer) this.nextCounter = (long) (int) nextCounter;
			else assert false;
		}
	}


	// JsonSerializable implementation:

	@Override public void serialize(JSONObject json) {
		json.put("nextCounter", nextCounter);
	}

	@Necessary public CounterIdGenerator(JSONObject json) throws JSONException, UnableToParse {
		this.nextCounter = json.getLong("nextCounter");
	}


	// StringMapSerializable implementation:

	@Override public void serialize(Setter1<String, String> output) {
		output.set("nextCounter", longToFixedSizeHexString(nextCounter));
	}

	@Necessary public CounterIdGenerator(Getter1<String, String> input) throws UnableToParse {
		this.nextCounter = safeHexStringToLong(input.get("nextCounter"));
	}


	// Constructors:

	public CounterIdGenerator() {
		nextCounter = 1;
	}


}
