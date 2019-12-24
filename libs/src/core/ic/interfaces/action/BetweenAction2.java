package ic.interfaces.action;


import ic.annotations.Narrowing;
import ic.annotations.Repeat;
import ic.throwables.None;


public abstract class BetweenAction2<Arg1, Arg2> extends SafeBetweenAction2<Arg1, Arg2, None> implements Action2<Arg1, Arg2> {

	@Narrowing @Repeat @Override public synchronized void run(Arg1 arg1, Arg2 arg2) { super.run(arg1, arg2); }

}
