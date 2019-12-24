package ic.interfaces.setter;



public interface SafeSetter1<Type, Arg, Thrown extends Throwable> {


	void set(Arg arg, Type value) throws Thrown;


}