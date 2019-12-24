package ic.cmd;


import ic.text.Text;
import ic.throwables.End;
import ic.throwables.NotSupported;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;


public class UserConsole extends Console.BaseConsole {


	// Output:

	@Override public void put(char c) {
		/*if (c == '\t') {
			System.out.print("    ");
			return;
		}*/
		System.out.print(c);
	}

	@Override public void put(Character character) {
		put((char) character);
	}

	@Override public void writeLine(Text text) {
		super.writeLine(text);
	}


	// Input:

	private final InputStreamReader inputStreamReader = new InputStreamReader(System.in);


	@Override public char getChar() throws End {
		try {
			return (char) inputStreamReader.read();
		} catch (IOException e) { throw new Error(e); }
	}

	@Override public Character get() throws End {
		return getChar();
	}

	@Override public Reader toReader() {
		throw new NotSupported.Runtime();
	}

	@Override public void close() {
		throw new NotSupported.Runtime("Can't close user console");
	}


	public static final UserConsole USER_CONSOLE = new UserConsole();


}
