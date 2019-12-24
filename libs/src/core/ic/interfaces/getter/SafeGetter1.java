package ic.interfaces.getter;


import ic.annotations.Narrowing;
import ic.throwables.None;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import ic.annotations.Repeat;
import ic.throwables.NotExists;


public interface SafeGetter1<Value, Arg, Thrown extends Throwable> extends Safe2Getter1<Value, Arg, Thrown, None> {


	@Narrowing @Override Value get(Arg arg) throws Thrown;

	@Narrowing @Repeat @Override @NotNull default Value getOrThrow(Arg arg) throws NotExists, Thrown 							{ return Safe2Getter1.super.getOrThrow(arg); 				}
	@Narrowing @Repeat @Override @NotNull default Value getNotNull(Arg arg) throws Thrown										{ return Safe2Getter1.super.getNotNull(arg); 				}
	@Narrowing @Repeat @Override @NotNull default Value get(Arg arg, @NotNull Function0<Value> defaultValueGetter) throws Thrown 	{ return Safe2Getter1.super.get(arg, defaultValueGetter); 	}


}
