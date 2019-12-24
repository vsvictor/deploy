package ic.interfaces.action;


import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import java.util.function.Consumer;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;

/**
 * Takes {@link Arg} for an argument, does something and eventually throws {@link Thrown1}, {@link Thrown2} or {@link Thrown3} in the process.
 */
public interface Safe3Action1<Arg, Thrown1 extends Throwable, Thrown2 extends Throwable, Thrown3 extends Throwable>

	extends Consumer<Arg>, Function1<Arg, Unit>

{

	void run(Arg arg) throws Thrown1, Thrown2, Thrown3;

	default @Override void accept(Arg arg) {
		try {
			run(arg);
		} catch (Throwable throwable) {
			throwAsUnchecked(throwable);
		}
	}

	default @Override Unit invoke(Arg arg) {
		try {
			run(arg);
		} catch (Throwable throwable) {
			throwAsUnchecked(throwable);
		}
		return Unit.INSTANCE;
	}

}
