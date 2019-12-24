package ic.interfaces.getter;


import ic.annotations.Narrowing;
import ic.throwables.NotExists;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface SafeGetter<Value, Thrown extends Throwable> extends Safe2Getter<Value, Thrown, RuntimeException> {


	@Narrowing @Nullable Value get() throws Thrown;

	@Narrowing @Override default @NotNull Value getOrThrow() throws NotExists, Thrown { return Safe2Getter.super.getOrThrow(); }
	@Narrowing @Override default @NotNull Value getNotNull() throws NotExists.Runtime, Thrown { return Safe2Getter.super.getNotNull(); }
	@Narrowing @Override default @NotNull Value get(Function0<? extends Value> defaultValueGetter) throws Thrown { return Safe2Getter.super.get(defaultValueGetter); }


}
