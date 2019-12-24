package ic.interfaces.getter;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.throwables.NotExists;

import static ic.throwables.NotExists.NOT_EXISTS;


public interface Getter3<Type, Arg1, Arg2, Arg3> {


	@Nullable Type get(Arg1 arg1, Arg2 arg2, Arg3 arg3);

	default @NotNull Type getOrThrow(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws NotExists {
		final Type value = get(arg1, arg2, arg3);
		if (value == null) throw NOT_EXISTS;
		return value;
	}

	default @NotNull Type getNotNull(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws NotExists.Runtime {
		try {
			return getOrThrow(arg1, arg2, arg3);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}


}
