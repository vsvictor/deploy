package ic.date;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.throwables.NotExists;

import java.util.Calendar;
import java.util.Date;

import static ic.throwables.NotExists.NOT_EXISTS;


public class DayIndex {

	public static int dateToDayIndexOrThrow(@NotNull Date date) throws NotExists {
		if (date == null) throw NOT_EXISTS;
		final Calendar calendar = Calendar.getInstance();
		final long time = date.getTime();
		calendar.set(Calendar.YEAR, 1970);
		calendar.set(Calendar.DAY_OF_YEAR, 2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		int dayIndex = 0;
		while (true) {
			if (time < calendar.getTimeInMillis()) return dayIndex;
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			calendar.add(Calendar.DATE, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			dayIndex++;
		}
	}

	public static int dateToDayIndex(@NotNull Date date) {
		try {
			return dateToDayIndexOrThrow(date);
		} catch (NotExists notExists) {
			throw new NotExists.Runtime(notExists);
		}
	}

	public static @Nullable Integer nullableDateToDayIndex(@Nullable Date date) {
		try {
			return dateToDayIndexOrThrow(date);
		} catch (NotExists notExists) {
			return null;
		}
	}

	public static Date dayIndexToDate(int dayIndex) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0L);
		calendar.add(Calendar.DATE, dayIndex);
		return calendar.getTime();
	}

}
