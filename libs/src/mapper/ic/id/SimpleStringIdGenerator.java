package ic.id;


import ic.annotations.Necessary;
import ic.interfaces.getter.Getter1;
import ic.stream.ByteInput;
import ic.throwables.UnableToParse;
import org.json.JSONException;
import org.json.JSONObject;

import static ic.util.Hex.longToFixedSizeHexString;


public class SimpleStringIdGenerator extends CounterIdGenerator<String> {

    @Override protected String generateId(long counter) {
		return longToFixedSizeHexString(counter);
	}

	@Override public Class getClassToDeclare() { return SimpleStringIdGenerator.class; }

	@Necessary public SimpleStringIdGenerator(ByteInput input) 					throws UnableToParse 				{ super(input); }
	@Necessary public SimpleStringIdGenerator(JSONObject json) 					throws UnableToParse, JSONException { super(json); 	}
	@Necessary public SimpleStringIdGenerator(Getter1<String, String> input) 	throws UnableToParse 				{ super(input); }

	public SimpleStringIdGenerator() { super(); }

}