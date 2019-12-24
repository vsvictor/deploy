package ic.interfaces.writer;


import ic.annotations.Repeat;


public interface Writer1<Type, Arg> extends SafeWriter1<Type, Arg, RuntimeException> {


	@Repeat @Override void write(Arg arg, Type value);


}
