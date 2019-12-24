package ic.interfaces.getter;


import ic.annotations.Narrowing;


public interface SafeGetter3<
	Value, 
	Arg1, Arg2, Arg3, 
	Thrown extends Throwable
> extends Safe2Getter3 <Value, Arg1, Arg2, Arg3, Thrown, RuntimeException> {

	@Narrowing @Override Value get(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown;
	
}