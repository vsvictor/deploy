package ic.interfaces.putter;


import ic.annotations.Repeat;


public interface Putter1<Type, Arg> extends SafePutter1<Type, Arg, RuntimeException> {


	@Repeat @Override void put(Arg arg, Type value);


	abstract class BasePutter1<Type, Arg>

		extends BaseSafePutter1<Type, Arg, RuntimeException>

		implements Putter1<Type, Arg>

	{

		@Repeat @Override public void put(Arg arg, Type value) { super.put(arg, value); }

	}


}
