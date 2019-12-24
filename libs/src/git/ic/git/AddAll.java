package ic.git;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.text.SplitText;
import org.jetbrains.annotations.NotNull;
import ic.cmd.Console;
import ic.cmd.SystemConsole;
import ic.storage.Directory;
import ic.text.Text;


@Degenerate class AddAll { @Hide private AddAll() {}

/*
	static void addAll(@NotNull Console console) {
		assert console != null;
		console.writeLine("LANG=en_US git add --all");
		final Text response = console.read();
		if (!response.isEmpty()) throw new RuntimeException(response.toString());
	}
	*/
	static void addAll(@NotNull Console console) {
		assert console != null;
		console.writeLine("LANG=en_US git add --all");
		final Text response = console.read();
		for (Text responseRow : new SplitText(response, '\n')) {
			if (responseRow.isEmpty()) continue;
			if (responseRow.startsWith("warning: CRLF will be replaced by LF in ")) continue;
			if (responseRow.equals("The file will have its original line endings in your working directory")) continue;
			else throw new RuntimeException(response.toString() + "\nROW " + responseRow);
		}
	}
	static void addAll(@NotNull Directory repositoryDir) {
		assert repositoryDir != null;
		final SystemConsole systemConsole = new SystemConsole();
		systemConsole.writeLine("cd " + repositoryDir.absolutePath);
		addAll(systemConsole);
	}


}
