package ic.date;


import ic.annotations.Degenerate;
import ic.annotations.Hide;
import ic.throwables.NotExists;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

import static ic.throwables.NotExists.NOT_EXISTS;
import static java.lang.System.currentTimeMillis;


public @Degenerate class Millis { @Hide private Millis() {};


	public static long dateToMillisOrThrow(Date date) throws NotExists {
		if (date == null) throw NOT_EXISTS;
		return date.getTime();
	}

	public static long dateToMillis(@NotNull Date date) throws NotExists.Runtime {
		try {
			return dateToMillisOrThrow(date);
		} catch (NotExists notExists) { throw new NotExists.Runtime(); }
	}

	public static @Nullable Long nullableDateToMillis(@Nullable Date date) {
		try {
			return dateToMillisOrThrow(date);
		} catch (NotExists notExists) {
			return null;
		}
	}


	public static @NotNull Date millisToDate(long millis) {
		return new Date(millis);
	}

	public static @Nullable Date nullableMillisToDate(@Nullable Long millis) {
		if (millis == null) return null;
		else return millisToDate(millis);
	}


	public static Date now() {
		return new Date(currentTimeMillis());
	}


}
