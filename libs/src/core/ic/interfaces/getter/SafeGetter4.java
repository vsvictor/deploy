package ic.interfaces.getter;


import ic.annotations.Narrowing;
import ic.throwables.None;


public interface SafeGetter4<
	Value,
	Arg1, Arg2, Arg3, Arg4,
	ThrowableType extends Throwable
> extends Safe2Getter4 <Value, Arg1, Arg2, Arg3, Arg4, ThrowableType, None> {

	@Narrowing @Override Value get(Arg1 arg1, Arg2 arg2, Arg3 arg3, Arg4 arg4) throws ThrowableType;

}