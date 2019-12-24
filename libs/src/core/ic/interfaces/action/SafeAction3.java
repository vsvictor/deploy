package ic.interfaces.action;


public interface SafeAction3<Arg1, Arg2, Arg3, Thrown extends Throwable> {

	void run(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown;

}
