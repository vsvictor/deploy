package ic.throwables;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.annotations.Repeat;


@SuppressWarnings("serial")


public class WrongType extends Exception {


	public final Class<?> type;
	public final Object object;


	public WrongType(@NotNull Class<?> type, @NotNull Object object) {
		this.type = type;
		this.object = object;
	}

	public WrongType(@NotNull Object object) {
		super(object.toString());
		this.type = object.getClass();
		this.object = object;
	}

	@Repeat public WrongType() { 
		super(); 
		this.type = null;
		this.object = null;
	}

	@Repeat public WrongType(String message) { 
		super(message); 
		this.type = null;
		this.object = null;
	}


	public static final WrongType WRONG_TYPE = new WrongType() { @Override public synchronized Throwable fillInStackTrace() { return null; } };


	public static void checkType(@NotNull Class<?> type, @NotNull Object object) throws WrongType {

		if (!type.isInstance(object)) throw new WrongType(type, object);

	}


	public static class Runtime extends java.lang.RuntimeException {

		public final Class<?> type;
		public final Object object;

		public Runtime(@NotNull Class<?> type, @NotNull Object object) {

			super("type: " + type.getName() + ", object: " + object.toString());

			assert !type.isInstance(object);

			this.type = type;
			this.object = object;

		}

		public Runtime(@NotNull Object object) {
			super("type: " + object.getClass().getName() + ", object: " + object.toString());
			this.type = null;
			this.object = object;
		}

		public Runtime(WrongType wrongType) {
			super(wrongType);
			this.type = wrongType.type;
			this.object = wrongType.object;
		}

	}


	public static class Error extends java.lang.Error {

		public @Nullable final Class<?> type;
		public @Nullable final Object object;

		public Error(@NotNull Class<?> type, @NotNull Object object) {

			super("type: " + type.getName() + ", object: " + object.toString());

			assert !type.isInstance(object);

			this.type = type;
			this.object = object;

		}

		public Error(Class<?> type) {
			super("type: " + type.getName());
			this.type = type;
			this.object = null;
		}

		public Error(Object object) {
			super("type: " + object.getClass().getName() + ", object: " + object.toString());
			this.object = object;
			this.type = object.getClass();
		}

		public Error(Throwable cause) {
			super(cause);
			this.type = null;
			this.object = null;
		}

		public static void checkType(@NotNull Class<?> type, @NotNull Object object) throws Error {

			{ // Checking arguments:
				assert type != null;
				assert object != null;
			}

			if (!type.isInstance(object)) throw new Error(type, object);

		}

	}


}