package ic.interfaces.action;


import ic.annotations.Narrowing;
import ic.throwables.None;


/**
 * Takes {@link Arg} for an argument, does something and eventually throws {@link Thrown1} or {@link Thrown1} in the process.
 */
public interface Safe2Action1<Arg, Thrown1 extends Throwable, Thrown2 extends Throwable> extends Safe3Action1<Arg, Thrown1, Thrown2, None> {

	@Narrowing @Override void run(Arg arg) throws Thrown1, Thrown2;

}
