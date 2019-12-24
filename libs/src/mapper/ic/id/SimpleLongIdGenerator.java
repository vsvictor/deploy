package ic.id;


import ic.interfaces.getter.Getter1;
import ic.stream.ByteInput;
import ic.throwables.UnableToParse;


public class SimpleLongIdGenerator extends CounterIdGenerator<Long> {

    @Override protected Long generateId(long counter) {
		return counter;
	}

	@Override public Class getClassToDeclare() { return SimpleLongIdGenerator.class; }

	public SimpleLongIdGenerator(ByteInput input) throws UnableToParse { super(input); }

	public SimpleLongIdGenerator(Getter1<String, String> input) throws UnableToParse { super(input); }

	public SimpleLongIdGenerator() { super(); }

}