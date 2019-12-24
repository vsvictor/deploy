package ic.interfaces.condition;


import kotlin.jvm.functions.Function1;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface SafeCondition1<Arg,  Thrown extends Throwable> extends Function1<Arg, Boolean> {

	boolean is(Arg arg) throws Thrown;

	default @Override Boolean invoke(Arg arg) {
		try {
			return is(arg);
		} catch (Throwable throwable) {
			throwAsUnchecked(throwable); return false;
		}
	}

}
