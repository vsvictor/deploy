package ic.interfaces.putter;


public interface SafePutter<Type, Thrown extends Throwable> {

	void put(Type value) throws Thrown;

}