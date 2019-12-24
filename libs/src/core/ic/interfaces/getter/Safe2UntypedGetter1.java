package ic.interfaces.getter;


import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import ic.throwables.NotExists;

import static ic.throwables.NotExists.NOT_EXISTS;


public interface Safe2UntypedGetter1<Arg, Thrown1 extends Throwable, Thrown2 extends Throwable> {


	Object getObject(Arg arg) throws Thrown1, Thrown2;


	default <Type> Type get(Arg arg) throws Thrown1, Thrown2 {

		@SuppressWarnings("unchecked")
		final Type value = (Type) getObject(arg);

		return value;

	}

	default <Type> Type getNotNull(Arg arg) throws Thrown1, Thrown2 {
		final Type value = get(arg);
		if (value == null) throw new NotExists.Runtime();
		return value;
	}

	default <Type> Type getOrThrow(Arg arg) throws NotExists, Thrown1, Thrown2 {
		final Type value = get(arg);
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default <Type> Type get(Arg arg, @NotNull Function0<Type> defaultValueGetter) throws Thrown1, Thrown2 {
		final Type value = get(arg);
		if (value == null) return defaultValueGetter.invoke();
		return value;
	}

	default <Type> Type get(Arg arg, Type defaultValue) throws Thrown1, Thrown2 {
		final Type value = get(arg);
		if (value == null) return defaultValue;
		return value;
	}


}
