package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.storage.Directory;
import ic.text.CharInput;
import ic.text.Text;
import ic.throwables.NotExists;


@Degenerate class GetCurrentBranch { @Hide private GetCurrentBranch() {}


	public static String getCurrentBranch(@NotNull Console console) {
		assert console != null;
		console.writeLine("LANG=en_US git status");
		final Text response = console.read();
		final Text firstRow = response.getIterator().readTill('\n');
		final CharInput firstRowIterator = firstRow.getIterator();
		try {
			firstRowIterator.safeReadTill("On branch ");
		} catch (NotExists notExists) { throw new RuntimeException(response.toString()); }
		return firstRowIterator.read().toString();
	}


	public static String getCurrentBranch(@NotNull Directory repositoryDir) {
		assert repositoryDir != null;
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		return getCurrentBranch(systemConsole);
	}


}
