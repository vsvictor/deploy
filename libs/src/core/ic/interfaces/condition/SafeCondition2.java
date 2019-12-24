package ic.interfaces.condition;


public interface SafeCondition2<Arg1, Arg2, Thrown extends Throwable> {

	boolean is(Arg1 arg1, Arg2 arg2) throws Thrown;

}
