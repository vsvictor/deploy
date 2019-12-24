package ic.interfaces.setter;


import ic.annotations.*;


public interface Setter1<Type, Arg> extends SafeSetter1<Type, Arg, RuntimeException> {


	@Narrowing @Repeat @Override void set(Arg arg, Type value);


}
