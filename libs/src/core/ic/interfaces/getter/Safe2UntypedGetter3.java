package ic.interfaces.getter;


import ic.throwables.NotExists;

import static ic.throwables.NotExists.NOT_EXISTS;


public interface Safe2UntypedGetter3<Arg1, Arg2, Arg3, Thrown1 extends Throwable, Thrown2 extends Throwable> {


	Object getObject(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown1, Thrown2;


	default <Type> Type get(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown1, Thrown2 {

		@SuppressWarnings("unchecked")
		final Type value = (Type) getObject(arg1, arg2, arg3);

		return value;

	}

	default <Type> Type getOrThrow(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws NotExists, Thrown1, Thrown2 {
		final Type value = get(arg1, arg2, arg3);
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default <Type> Type getNotNull(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown1, Thrown2 {
		final Type value = get(arg1, arg2, arg3);
		if (value == null) throw new NotExists.Runtime();
		return value;
	}


}
