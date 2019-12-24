package ic.interfaces.writer;


public interface SafeWriter1<Type, Arg, Thrown extends Throwable> {


	void write(Arg arg, Type value) throws Thrown;


}