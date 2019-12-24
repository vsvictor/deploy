package ic.interfaces.reader;


import ic.annotations.Narrowing;


public interface Reader1<Type, Arg> extends SafeReader1<Type, Arg, RuntimeException> {

	@Narrowing @Override Type read(Arg arg);

}
