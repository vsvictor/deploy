package ic.util.reflect;


import ic.annotations.Degenerate;
import ic.struct.list.List;
import org.jetbrains.annotations.NotNull;
import ic.throwables.*;

import java.lang.reflect.InvocationTargetException;

import static ic.throwables.ThrowAsUncheckedKt.throwAsUnchecked;


@Deprecated
public @Degenerate class Classes {


	// Class:

	public static <Type> Class<Type> getClassByNameOrThrow(@NotNull String name) throws NotExists {

		try {

			@SuppressWarnings("unchecked")
			final Class<Type> classInstance = (Class<Type>) Class.forName(name);

			return classInstance;

		} catch (ClassNotFoundException e) {
			throw new NotExists("Class '" + name + "' not exists");
		}

	}

	public static <Type> Class<Type> getClassByName(@NotNull String name) {
		try {
			return getClassByNameOrThrow(name);
		} catch (NotExists notExists) { throwAsUnchecked(notExists); return null; }
	}


	// Instantiation:

	@Deprecated
	public static <Type> Type instantiate(
		@NotNull Class<? extends Type> classInstance,
		@NotNull Class[] constructorArgClasses, @NotNull Object[] constructorArgs
	) {
		try {
			return classInstance.getConstructor(constructorArgClasses).newInstance(constructorArgs);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
			throwAsUnchecked(e); return null;
		} catch (InvocationTargetException e) {
			throwAsUnchecked(e.getTargetException()); return null;
		}
	}

	@Deprecated
	public static <Type> Type instantiate(
		@NotNull Class<? extends Type> classInstance,
		@NotNull List<Class<?>> constructorArgClasses, @NotNull List<?> constructorArgs
	) {
		try {
			return classInstance.getConstructor(constructorArgClasses.toArray(Class.class)).newInstance((Object[]) constructorArgs.toArray());
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException e) {
			throwAsUnchecked(e); return null;
		} catch (InvocationTargetException e) {
			throwAsUnchecked(e.getTargetException()); return null;
		}
	}

}
