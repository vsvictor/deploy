package ic.id;


import ic.interfaces.getter.Getter1;
import ic.stream.ByteInput;
import ic.throwables.UnableToParse;


public class EasyTypeStringIdGenerator extends CounterIdGenerator<String> {

	@Override protected String generateId(long counter) {
		return Long.toString(counter, 36);
	}

	@Override public Class getClassToDeclare() { return EasyTypeStringIdGenerator.class; }

	public EasyTypeStringIdGenerator(ByteInput input) throws UnableToParse { super(input); }

	public EasyTypeStringIdGenerator(Getter1<String, String> input) throws UnableToParse { super(input); }

	public EasyTypeStringIdGenerator() { super(); }

}