package ic.struct.variable;


import java.lang.ref.SoftReference;
import ic.throwables.None;
import org.jetbrains.annotations.NotNull;


public class Soft<Type> extends SoftReference<Type> implements Ephemeral<Type> {

	@Override public Type getValue() throws None {
		return get();
	}

	public Soft(@NotNull Type value) {
		super(value);
	}


}