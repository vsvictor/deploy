package ic.interfaces.closeable;


import ic.annotations.Narrowing;


public interface Closeable extends SafeCloseable<RuntimeException> {

	@Narrowing @Override void close();

}
