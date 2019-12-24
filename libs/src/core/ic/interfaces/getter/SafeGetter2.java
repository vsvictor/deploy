package ic.interfaces.getter;


import kotlin.jvm.functions.Function2;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface SafeGetter2<Value, Arg1, Arg2, Thrown extends Throwable> extends Function2<Arg1, Arg2, Value> {

	Value get(Arg1 arg1, Arg2 arg2) throws Thrown;

	@Override default Value invoke(Arg1 arg1, Arg2 arg2) {
		try {
			return get(arg1, arg2);
		} catch (Throwable thrown) { throwAsUnchecked(thrown); return null; }
	}

}