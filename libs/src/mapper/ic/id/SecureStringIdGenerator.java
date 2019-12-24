package ic.id;


import ic.interfaces.getter.Getter1;
import ic.stream.ByteInput;
import ic.throwables.UnableToParse;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import static ic.math.RandomKt.randomLong;
import static ic.util.Hex.*;


public class SecureStringIdGenerator extends CounterIdGenerator<String> {


	@Override protected synchronized String generateId(long counter) {
		return longToFixedSizeHexString(counter) + "." + longToFixedSizeHexString(randomLong());
	}

	@Override public @NotNull Class getClassToDeclare() { return SecureStringIdGenerator.class; }

	public SecureStringIdGenerator(ByteInput input) 				throws UnableToParse { super(input); 	}
	public SecureStringIdGenerator(Getter1<String, String> input) 	throws UnableToParse { super(input); 	}
	public SecureStringIdGenerator(JSONObject json) 				throws UnableToParse { super(json); 	}

	public SecureStringIdGenerator() { super(); }


}
