package ic.interfaces.action;


import ic.annotations.Narrowing;
import ic.throwables.None;

/**
 * Takes {@link Arg} for an argument, does something and eventually throws {@link Thrown} in the process.
 */
public interface SafeAction1<Arg, Thrown extends Throwable> extends Safe2Action1<Arg, Thrown, None> {

	@Narrowing @Override void run(Arg arg) throws Thrown;

}
