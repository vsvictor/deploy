package ic.interfaces.action;


import kotlin.Unit;
import kotlin.jvm.functions.Function0;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;

/**
 * Does something and eventually throws {@link Thrown1} or {@link Thrown2} in the process.
 */
public interface Safe2Action<Thrown1 extends Throwable, Thrown2 extends Throwable> extends Function0<Unit> {

	void run() throws Thrown1, Thrown2;

	default @Override Unit invoke() {
		try {
			run();
		} catch (Throwable throwable) { throwAsUnchecked(throwable); }
		return Unit.INSTANCE;
	}

}
