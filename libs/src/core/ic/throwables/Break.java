package ic.throwables;


import ic.annotations.DoesNothing;


public class Break extends RuntimeException {

	public static final Break BREAK = new Break() { @DoesNothing @Override public Throwable fillInStackTrace() { return null; } };

}
