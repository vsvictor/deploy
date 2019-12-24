package ic.interfaces.reader;


public interface SafeReader1<Type, Arg, Thrown extends Throwable> {

	Type read(Arg arg) throws Thrown;

}
