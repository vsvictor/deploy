package ic.interfaces.writer;


public interface SafeWriter<Type, Thrown extends Throwable> {

	void write(Type value) throws Thrown;

}