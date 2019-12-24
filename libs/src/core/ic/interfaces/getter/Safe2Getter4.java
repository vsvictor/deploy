package ic.interfaces.getter;


import kotlin.jvm.functions.Function4;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface Safe2Getter4<
	Value, 
	Arg1, Arg2, Arg3, Arg4,
	Throwable1 extends Throwable,
	Throwable2 extends Throwable
>
	extends Function4<Arg1, Arg2, Arg3, Arg4, Value>
{

	Value get(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4) throws Throwable1, Throwable2;

	default @Override Value invoke(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4) {
		try {
			return get(arg1, arg2, arg3, arg4);
		} catch (Throwable throwable) {
			throwAsUnchecked(throwable); return null;
		}
	}

}