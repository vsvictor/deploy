package ic.interfaces.action;


import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.throwables.Break;
import ic.throwables.None;


public abstract class BetweenAction1<Arg> extends SafeBetweenAction1<Arg, None> implements SafeAction1<Arg, Break> {

	@Narrowing @Repeat @Override public synchronized void run(Arg arg) { super.run(arg); }

}
