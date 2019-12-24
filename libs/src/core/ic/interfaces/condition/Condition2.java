package ic.interfaces.condition;


// util imports:

import ic.annotations.Narrowing;
import ic.annotations.Repeat;


public interface Condition2<Arg1, Arg2>  extends SafeCondition2<Arg1, Arg2, RuntimeException> {

	@Narrowing @Repeat boolean is(Arg1 arg1, Arg2 arg2);

}
