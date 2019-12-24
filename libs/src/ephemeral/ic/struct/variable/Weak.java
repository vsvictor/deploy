package ic.struct.variable;


import java.lang.ref.WeakReference;
import ic.throwables.None;
import org.jetbrains.annotations.NotNull;


public class Weak<Type> extends WeakReference<Type> implements Ephemeral<Type> {

	@Override public Type getValue() throws None {
		return get();
	}

	public Weak(@NotNull Type value) {
		super(value);
	}

}