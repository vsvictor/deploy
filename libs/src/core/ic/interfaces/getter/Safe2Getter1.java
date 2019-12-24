package ic.interfaces.getter;


import ic.annotations.Narrowing;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.annotations.Repeat;
import ic.throwables.NotExists;


public interface Safe2Getter1<Value, Arg, Thrown1 extends Throwable, Thrown2 extends Throwable> extends Safe3Getter1<Value, Arg, Thrown1, Thrown2, RuntimeException> {


	@Narrowing @Override @Nullable Value get(Arg arg) throws Thrown1, Thrown2;

	@Narrowing @Repeat @Override @NotNull default Value getOrThrow(Arg arg) throws NotExists, Thrown1, Thrown2 						{ return Safe3Getter1.super.getOrThrow(arg); 				}
	@Narrowing @Repeat @Override @NotNull default Value getNotNull(Arg arg) throws NotExists.Runtime, Thrown1, Thrown2					{ return Safe3Getter1.super.getNotNull(arg); 				}
	@Narrowing @Repeat @Override @NotNull default Value get(Arg arg, @NotNull Function0<Value> defaultValueGetter) throws Thrown1, Thrown2 { return Safe3Getter1.super.get(arg, defaultValueGetter); 	}


}
