package ic.cmd.ssh;


import com.jcraft.jsch.*;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import ic.cmd.ExecConsole;
import ic.stream.ByteInput;
import ic.text.*;
import ic.throwables.IOException;

import java.util.Properties;


public class SshSession extends ExecConsole {


	private final Session session;


	@Override protected Text exec(String command) {

		//System.out.println("SSH_COMMAND " + command); // TODO LOG

		try {

			final ChannelExec channel = (ChannelExec) session.openChannel("exec"); try {

				channel.setCommand(command);

				channel.connect();
				final ByteInput inputStream = new ByteInput.FromInputStream(channel.getInputStream());
				final ByteInput errorStream = new ByteInput.FromInputStream(channel.getErrStream());

				final String inputString = new JoinText(
					Charset.UTF_8.bytesToText(inputStream.read()),
					Charset.UTF_8.bytesToText(errorStream.read())
				).getStringValue().replace("bash: line 0: ", "");

				inputStream.close();
				errorStream.close();

				//System.out.println("SSH_RESPONSE:\n" + inputString); // TODO LOG

				return new Text.FromString(inputString);

			} catch (java.io.IOException e) { throw new IOException.Runtime(e);
			} finally {
				channel.disconnect();
			}

		} catch (JSchException e) { throw new IOException.Runtime(e); }

	}


	@Override public synchronized void close() {
		session.disconnect();
	}


	public SshSession(@NotNull String host, @NotNull Pair<String, String> auth) {

		try {

			final JSch jsch = new JSch();

			session = jsch.getSession(auth.getFirst(), host);

			session.setPassword(auth.getSecond());

			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect();

		} catch (JSchException e) { throw new IOException.Runtime(e); }


	}


}
