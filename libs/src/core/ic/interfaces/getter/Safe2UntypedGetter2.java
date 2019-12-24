package ic.interfaces.getter;


import ic.throwables.NotExists;
import kotlin.jvm.functions.Function2;

import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface Safe2UntypedGetter2<Arg1, Arg2, Thrown1 extends Throwable, Thrown2 extends Throwable>
	extends Function2<Arg1, Arg2, Object>
{


	Object getObject(Arg1 arg1, Arg2 arg2) throws Thrown1, Thrown2;


	default <Type> Type get(Arg1 arg1, Arg2 arg2) throws Thrown1, Thrown2 {

		@SuppressWarnings("unchecked")
		final Type value = (Type) getObject(arg1, arg2);

		return value;

	}

	default <Type> Type getOrThrow(Arg1 arg1, Arg2 arg2) throws NotExists, Thrown1, Thrown2 {
		final Type value = get(arg1, arg2);
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default <Type> Type getNotNull(Arg1 arg1, Arg2 arg2) throws Thrown1, Thrown2 {
		final Type value = get(arg1, arg2);
		if (value == null) throw new NotExists.Runtime();
		return value;
	}

	default @Override Object invoke(Arg1 arg1, Arg2 arg2) {
		try {
			return get(arg1, arg2);
		} catch (Throwable thrown) { throwAsUnchecked(thrown); return null; }
	}

}
