package ic.interfaces.getter;


import ic.throwables.NotExists;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;
import static ic.throwables.NotExists.NOT_EXISTS;


public interface Safe3Getter<Value, Thrown1 extends Throwable, Thrown2 extends Throwable, Thrown3 extends Throwable>

	extends Function0<Value>

{


	@Nullable Value get() throws Thrown1, Thrown2, Thrown3;

	default @NotNull Value getOrThrow() throws NotExists, Thrown1, Thrown2, Thrown3 {
		final Value value = get();
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default @NotNull Value getNotNull() throws Thrown1, Thrown2, Thrown3 {
		final Value value = get();
		assert value != null;
		return value;
	}

	default @NotNull Value get(@NotNull Function0<? extends Value> defaultValueGetter) throws Thrown1, Thrown2, Thrown3 {
		final Value value = get();
		if (value == null) return defaultValueGetter.invoke();
		else return value;
	}


	@Override default Value invoke () {
		try {
			return get();
		} catch (Throwable thrown) { throwAsUnchecked(thrown); return null; }
	}


}
