package ic.interfaces.getter;


import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.throwables.NotExists;

import static ic.throwables.NotExists.NOT_EXISTS;
import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface Safe3Getter1<Value, Arg, Thrown1 extends Throwable, Thrown2 extends Throwable, Thrown3 extends Throwable>

	extends Function1<Arg, Value>

{


	@Nullable Value get(Arg arg) throws Thrown1, Thrown2, Thrown3;

	default Value getOrThrow(Arg arg) throws NotExists, Thrown1, Thrown2, Thrown3 {
		final Value value = get(arg);
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default Value getNotNull(Arg arg) throws Thrown1, Thrown2, Thrown3 {
		final Value value = get(arg);
		assert value != null;
		return value;
	}

	default Value get(Arg arg, @NotNull Function0<Value> defaultValueGetter) throws Thrown1, Thrown2, Thrown3 {
		final Value value = get(arg);
		if (value == null) return defaultValueGetter.invoke();
		else return value;
	}

	default Value get(Arg arg, Value defaultValue) throws Thrown1, Thrown2, Thrown3 {
		final Value value = get(arg);
		if (value == null) return defaultValue;
		else return value;
	}

	default @Nullable Value getNullable(@Nullable Arg arg) throws Thrown1, Thrown2, Thrown3 {
		if (arg == null) return null;
		return get(arg);
	}


	@Override default Value invoke (Arg arg) {
		try {
			return get(arg);
		} catch (Throwable thrown) { throwAsUnchecked(thrown); return null; }
	}


}
