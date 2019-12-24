package ic.log;


import org.jetbrains.annotations.NotNull;


public abstract class Logger {

	public abstract void writeLine(@NotNull String string);

	public abstract void writeStackTrace(@NotNull Throwable throwable);

}
