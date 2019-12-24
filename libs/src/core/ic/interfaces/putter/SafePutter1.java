package ic.interfaces.putter;


import ic.annotations.DoNotOverride;
import ic.annotations.ToOverride;


public interface SafePutter1<Type, Arg, Thrown extends Throwable> {


	void put(Arg arg, Type value) throws Thrown;


	abstract class BaseSafePutter1<Type, Arg, Thrown extends Throwable>

		implements SafePutter1<Type, Arg, Thrown>

	{

		@ToOverride protected abstract void implementSet(Arg arg, Type value) throws Thrown;

		@DoNotOverride
		@Override public void put(Arg arg, Type value) throws Thrown {
			implementSet(arg, value);
		}

	}


}