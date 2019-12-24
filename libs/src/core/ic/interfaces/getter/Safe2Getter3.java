package ic.interfaces.getter;

public interface Safe2Getter3<
	Value, 
	Arg1, Arg2, Arg3, 
	Thrown1 extends Throwable, 
	Thrown2 extends Throwable
> {

	Value get(Arg1 arg1, Arg2 arg2, Arg3 arg3) throws Thrown1, Thrown2;
	
}