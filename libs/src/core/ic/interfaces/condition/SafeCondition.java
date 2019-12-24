package ic.interfaces.condition;


import ic.interfaces.getter.SafeGetter;
import org.jetbrains.annotations.NotNull;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


public interface SafeCondition<Thrown extends Throwable> extends SafeGetter<Boolean, Thrown> {

	boolean getBoolean() throws Thrown;

	default @Override @NotNull Boolean get() throws Thrown {
		return getBoolean();
	}

	default @Override Boolean invoke() {
		try {
			return getBoolean();
		} catch (Throwable thrown) { throwAsUnchecked(thrown); return null; }
	}

	class Constant<Thrown extends Throwable> implements SafeCondition<Thrown> {
		public final boolean value;
		@Override public final boolean getBoolean() throws Thrown {
			return value;
		}
		public Constant(boolean value) {
			this.value = value;
		}
	}

	class Else<Thrown extends Throwable> extends Constant<Thrown> {
		public Else() {
			super(true);
		}
	}

}
