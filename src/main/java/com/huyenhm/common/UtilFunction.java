package com.huyenhm.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import com.huyenhm.exception.InvalidInputException;
import com.huyenhm.exception.RequiredException;

public class UtilFunction {

	public static boolean isNullOrEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static Object validateInput(Object value, String key, boolean requiresFormat, String regex) {
		if (value == null || value.toString().isEmpty()) {
			throw new RequiredException(key + "is required");
		}

		String stringValue = value.toString();

		if (requiresFormat && regex != null && !Pattern.matches(regex, stringValue)) {
			throw new InvalidInputException(key + " has an invalid format.");
		}

		return value;
	}

	public static LocalDate getDate(String dateStr) {
		return getTimestamp(dateStr).toLocalDate();
	}

	public static LocalTime getTime(String dateStr) {
		return getTimestamp(dateStr).toLocalTime();
	}

	private static LocalDateTime getTimestamp(String dateStr) {
		if (dateStr == null || "0".equals(dateStr)) {
			return LocalDateTime.now();
		}

		try {
			LocalDateTime localDateTime = null;
			if (hasTimezone(dateStr)) {
				ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateStr);
				localDateTime = zonedDateTime.toLocalDateTime();
			} else {
				DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
				localDateTime = LocalDateTime.parse(dateStr, formatter);
			}

			return localDateTime;
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("Invalid date format: " + e.getMessage());
		}
	}

	private static boolean hasTimezone(String dateTime) {
		String timezonePattern = "([+-]\\d{2}:?\\d{2}|Z|GMT|UTC|[A-Za-z]+)";
		return Pattern.compile(timezonePattern).matcher(dateTime).find();
	}
}
