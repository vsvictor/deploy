package ic.interfaces.setter;


import ic.interfaces.getter.Getter;
import ic.throwables.NotExists;


public interface GetterSetter<Value> extends Getter<Value>, Setter<Value> {


	default Value createIfNull(Getter<Value> getter) {
		try {
			return getOrThrow();
		} catch (NotExists notExists) {
			final Value value = getter.get();
			assert value != null;
			set(value);
			return value;
		}
	}


}
