package ic.struct.map;


import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.interfaces.getter.Getter1;


public interface Map<Value, Key> extends Getter1<Value, Key> {


	@Narrowing @Repeat @Override Value get(Key key);


	abstract class BaseMap<Value, Key> implements Map<Value, Key> {

	}


}
