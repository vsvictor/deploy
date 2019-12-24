package ic.test;


import ic.Package;
import ic.interfaces.action.Action1;
import ic.text.CharInput;
import ic.text.CharOutput;
import ic.text.JoinStrings;
import ic.throwables.NotExists;

import static ic.cmd.UserConsole.USER_CONSOLE;
import static ic.util.reflect.Classes.getClassByNameOrThrow;
import static ic.util.reflect.Classes.instantiate;


public abstract class Test implements Action1<CharOutput> {


	public static void main(String[] argsSplit) {

		final CharInput argsIterator = new JoinStrings(argsSplit, ' ').getIterator();

		final String packageName = argsIterator.readTill(' ').toString();

		final Package packageObject = Package.getNotNull(packageName);

		if (packageObject.testClassName != null) {

			final Class<? extends Test> testClass; try {
				testClass = getClassByNameOrThrow(packageObject.testClassName);
				assert Test.class.isAssignableFrom(testClass);
			} catch (NotExists noSuchClass) { throw new NotExists.Error(noSuchClass); }

			final Test test = instantiate(testClass, new Class[] {}, new Object[] {});

			test.run(USER_CONSOLE);

		}

	}


}
