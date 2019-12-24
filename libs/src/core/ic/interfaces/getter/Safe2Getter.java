package ic.interfaces.getter;


import ic.annotations.Narrowing;
import ic.throwables.NotExists;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface Safe2Getter<Value, Thrown1 extends Throwable, Thrown2 extends Throwable> extends Safe3Getter<Value, Thrown1, Thrown2, RuntimeException> {


	@Narrowing @Override @Nullable Value get() throws Thrown1, Thrown2;

	@Narrowing @Override default @NotNull Value getOrThrow() throws NotExists, Thrown1, Thrown2 { return Safe3Getter.super.getOrThrow(); }

	@Narrowing @Override default @NotNull Value getNotNull() throws NotExists.Runtime, Thrown1, Thrown2 { return Safe3Getter.super.getNotNull(); }

	@Narrowing @Override default @NotNull Value get(Function0<? extends Value> defaultValueGetter) throws Thrown1, Thrown2 { return Safe3Getter.super.get(defaultValueGetter); }


}
