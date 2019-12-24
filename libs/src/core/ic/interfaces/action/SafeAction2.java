package ic.interfaces.action;


import ic.annotations.Narrowing;


public interface SafeAction2<Arg1, Arg2, Thrown extends Throwable> extends Safe2Action2<Arg1, Arg2, Thrown, RuntimeException> {


	@Narrowing @Override void run(Arg1 arg1, Arg2 arg2) throws Thrown;


}
