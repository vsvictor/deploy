package ic.interfaces.putter;

import ic.annotations.Repeat;


public interface Putter<Type> extends SafePutter<Type, RuntimeException> {

	@Repeat @Override void put(Type value);

}
