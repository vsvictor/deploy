package ic.cmd;


import ic.stream.ByteInput;
import ic.text.Charset;
import ic.text.JoinText;
import ic.text.Text;
import ic.throwables.*;
import ic.throwables.IOException;

import java.io.*;
import java.util.NoSuchElementException;


public class SystemConsole extends ExecConsole {


	private final Runtime runtime = Runtime.getRuntime();


	@Override protected Text exec(String command) {

		try {

			final Process process = runtime.exec(new String[] {
				"/bin/bash",
				"-c",
				command
			});

			//ProcessBuilder builder = new ProcessBuilder();
			//Process process = builder.command("/bin/bash","-c",command).inheritIO().start();

			final ByteInput outputStream 	= new ByteInput.FromInputStream(process.getInputStream());
			final ByteInput errorStream 	= new ByteInput.FromInputStream(process.getErrorStream());

			final Text response = new JoinText(
				Charset.DEFAULT_UNIX.bytesToText(outputStream.read()),
				Charset.DEFAULT_UNIX.bytesToText(errorStream.read())
			);

			try {
				process.waitFor();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}


			outputStream.close();
			errorStream.close();

			return response;

		} catch (java.io.IOException e) { throw new IOException.Runtime(e); }

	}


	@Override public void close() { throw new NotSupported.Runtime("Can't close system console"); }


}
