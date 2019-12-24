package ic.interfaces.closeable;


public interface SafeCloseable<Thrown extends Throwable> {

	void close() throws Thrown;

}
