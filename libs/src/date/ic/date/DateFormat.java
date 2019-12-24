package ic.date;


import ic.annotations.NotEmpty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ic.throwables.NotExists;
import ic.throwables.UnableToParse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ic.throwables.NotExists.NOT_EXISTS;


public class DateFormat {


	public static @NotNull String formatDateOrThrow(Date date, @NotNull String dateFormat) throws NotExists {
		if (date == null) throw NOT_EXISTS;
		return new SimpleDateFormat(dateFormat).format(date);
	}

	public static @NotNull String formatDate(@NotNull Date date, @NotNull String dateFormat) throws NotExists.Runtime {
		try {
			return formatDateOrThrow(date, dateFormat);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	public static String formatNullableDate(@Nullable Date date, @NotNull String dateFormat) throws NotExists.Runtime {
		if (date == null) return null;
		return formatDate(date, dateFormat);
	}


	public static @NotNull Date safeParseDateOrThrow(@NotEmpty String dateString, @NotNull String... dateFormats) throws NotExists, UnableToParse {
		if (dateString == null) 			throw NOT_EXISTS;
		if (dateString.trim().isEmpty()) 	throw NOT_EXISTS;
		for (String dateFormat : dateFormats) {
			assert dateFormat != null;
			try {
				return new SimpleDateFormat(dateFormat).parse(dateString);
			} catch (ParseException parseException) {}
		} throw new UnableToParse("Can't parse date \"" + dateString + "\"");
	}

	public static @NotNull Date safeParseDate(@NotNull @NotEmpty String dateString, @NotNull String... dateFormats) throws UnableToParse {
		try {
			return safeParseDateOrThrow(dateString, dateFormats);
		} catch (NotExists notExists) { throw new NotExists.Runtime(notExists); }
	}

	public static @Nullable Date safeParseNullableDate(@Nullable String dateString, @NotNull String... dateFormats) throws UnableToParse {
		try {
			return safeParseDateOrThrow(dateString, dateFormats);
		} catch (NotExists notExists) {
			return null;
		}
	}

	public static @NotNull Date parseDateOrThrow(@NotNull @NotEmpty String dateString, @NotNull String... dateFormats) throws NotExists {
		try {
			return safeParseDateOrThrow(dateString, dateFormats);
		} catch (UnableToParse unableToParse) {
			throw new UnableToParse.Runtime(unableToParse);
		}
	}

	public static @NotNull Date parseDate(@NotNull @NotEmpty String dateString, @NotNull String... dateFormats) {
		try {
			return safeParseDateOrThrow(dateString, dateFormats);
		} catch (NotExists notExists) {
			throw new NotExists.Runtime(notExists);
		} catch (UnableToParse unableToParse) {
			throw new UnableToParse.Runtime(unableToParse);
		}
	}

	public static @Nullable Date parseNullableDate(@Nullable String dateString, @NotNull String... dateFormats) {
		try {
			return safeParseDateOrThrow(dateString, dateFormats);
		} catch (NotExists notExists) {
			return null;
		} catch (UnableToParse unableToParse) {
			throw new UnableToParse.Runtime(unableToParse);
		}
	}


}
