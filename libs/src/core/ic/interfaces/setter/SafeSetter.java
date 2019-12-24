package ic.interfaces.setter;


public interface SafeSetter<Type, Thrown extends Throwable> {

	void set(Type value) throws Thrown;

}