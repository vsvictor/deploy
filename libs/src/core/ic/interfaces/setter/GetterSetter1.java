package ic.interfaces.setter;


import ic.interfaces.getter.Getter1;
import ic.throwables.NotExists;
import kotlin.jvm.functions.Function0;


public interface GetterSetter1<Type, Arg> extends Getter1<Type, Arg>, Setter1<Type, Arg> {


	default Type createIfNull(Arg arg, Function0<Type> getter) {

		synchronized (this) {

			try {
				return getOrThrow(arg);
			} catch (NotExists notExists) {
				final Type value = getter.invoke();
				assert value != null;
				set(arg, value);
				return value;
			}

		}

	}


}
