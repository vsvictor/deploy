package ic.throwables;


@SuppressWarnings("serial")


public class NoNecessaryConstructor extends RuntimeException {

	private static String generateMessage(Class<?> classObject, Class<?>[] constructorArguments) {

		String message = classObject.getName() + " doesn't contain necessary constructor with arguments (";

		for (int i = 0; i < constructorArguments.length; i++) {

			if (i > 0) message += ", ";

			message += constructorArguments[i].getName();

			if (i < i - 1) message += ", ";

		}

		message += ")";

		return message;

	}

	public NoNecessaryConstructor(Class<?> classObject, Class<?>[] constructorArguments) {

		super(generateMessage(classObject, constructorArguments));

	}

}
