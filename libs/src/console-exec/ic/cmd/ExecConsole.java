package ic.cmd;


import ic.text.*;
import ic.throwables.End;


public abstract class ExecConsole extends Console.BaseConsole {


	private TextBuffer output;

	private CharInput input;


	private String pwd = ".";


	protected abstract Text exec(String command);


	@Override public final synchronized void put(char c) {

		if (output == null) output = new TextBuffer();

		if (c == '\n') {

			try {

				String command = output.toString();

				final String newPwd;
				final String internalCommand; {
					if (command.startsWith("cd ")) {
						final String relativePwd = command.substring("cd ".length());
						if (relativePwd.trim().isEmpty()) 	newPwd = "~"; 			else
						if (relativePwd.startsWith("/")) 	newPwd = relativePwd; 	else
						if (relativePwd.startsWith("~")) 	newPwd = relativePwd;
						else newPwd = pwd + "/" + relativePwd;
						internalCommand = "cd " + newPwd;
					} else {
						newPwd = pwd;
						internalCommand = "cd " + pwd + "; " + command;
					}
				}

				final Text response = exec(internalCommand);

				if (command.startsWith("cd ") && response.isEmpty()) {

					pwd = newPwd;

				}

				input = response.getIterator();

			}  finally {

				output = null;

			}

		} else {

			output.put(c);

		}

	}

	@Override public synchronized char getChar() throws End {
		if (input == null) throw new End();
		return input.getChar();
	}


}
