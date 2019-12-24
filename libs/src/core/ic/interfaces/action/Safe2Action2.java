package ic.interfaces.action;


import kotlin.Unit;
import kotlin.jvm.functions.Function2;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface Safe2Action2<Arg1, Arg2, Thrown1 extends Throwable, Thrown2 extends Throwable>

	extends Function2<Arg1, Arg2, Unit>

{

	void run(Arg1 arg1, Arg2 arg2) throws Thrown1, Thrown2;

	default @Override Unit invoke(Arg1 arg1, Arg2 arg2) {
		try {
			run(arg1, arg2);
		} catch (Throwable throwable) { throwAsUnchecked(throwable); return null; }
		return Unit.INSTANCE;
	}

}
