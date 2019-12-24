package ic.interfaces.setter;


import ic.interfaces.getter.UntypedGetter1;
import ic.throwables.NotExists;
import kotlin.jvm.functions.Function0;


public interface UntypedGetterSetter1<Arg> extends UntypedGetter1<Arg>, Setter1<Object, Arg> {


	default <Type> Type createIfNull(Arg arg, Function0<Type> getter) {
		try {
			return this.getOrThrow(arg);
		} catch (NotExists notExists) {
			final Type value = getter.invoke();
			assert value != null;
			set(arg, value);
			return value;
		}
	}


}
