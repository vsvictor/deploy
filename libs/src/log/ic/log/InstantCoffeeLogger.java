package ic.log;


import ic.Distribution;
import org.jetbrains.annotations.NotNull;
import ic.stream.ByteOutput;
import ic.text.Charset;

import java.io.PrintStream;
import java.util.Date;

import static ic.Storages.getLogsStorage;
import static ic.date.DateFormat.formatDate;
import static ic.date.Millis.now;


public class InstantCoffeeLogger extends Logger {


	@Override public synchronized void writeLine(@NotNull String string) {

		final Date now = now();

		final ByteOutput output = getLogsStorage().getAppendOutput(
			"logger-" + formatDate(now, "yyyy-MM-dd")
		);

		try {

			output.write(
				Charset.DEFAULT_UNIX.stringToByteArray(
					formatDate(now, "HH:mm:ss") + " " + string + "\n"
				)
			);

		} finally {

			output.close();

		}

		if (Distribution.get().customData.get("tier") == null) {
			System.out.println(string);
		}

	}


	@Override public synchronized void writeStackTrace(@NotNull Throwable throwable) {

		final Date now = now();

		final ByteOutput output = getLogsStorage().getAppendOutput(
			"logger-" + formatDate(now, "yyyy-MM-dd")
		);

		try {

			output.write(
				Charset.DEFAULT_UNIX.stringToByteArray(
					formatDate(now, "HH:mm:ss") + " "
				)
			);

			throwable.printStackTrace(new PrintStream(output.getOutputStream()));

		} finally {

			output.close();

		}

		if (Distribution.get().customData.get("tier") == null) {
			throwable.printStackTrace();
		}

	}


	public static final InstantCoffeeLogger LOGGER = new InstantCoffeeLogger();


}
