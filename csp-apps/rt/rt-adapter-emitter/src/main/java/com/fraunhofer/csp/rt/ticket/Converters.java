package com.fraunhofer.csp.rt.ticket;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by Majid Salehi on 4/8/17.
 */
final class Converters {

	private Converters() {
	}

	static String stringConverter(String value) {
		return value;
	}

	static TicketStatus ticketStatusConverter(String value) {
		return TicketStatus.getTicketStatusFor(value);
	}

	static LocalDateTime localDateTimeConverter(String value) {
		try {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
			return LocalDateTime.parse(value, formatter);
		} catch (DateTimeParseException e) {
			// includes "Not set"
			return null;
		}
	}

	static Duration durationConverter(String value) {
		final int separatorIndex = value.indexOf(' ');
		if (separatorIndex > 0) {
			return Duration.ofMinutes(Long.parseLong(value.substring(0, separatorIndex)));
		} else {
			return Duration.ofMinutes(Long.parseLong(value));
		}
	}

	static List<String> stringListConverter(String value) {
		final String[] items = value.split(", ?");
		if (items.length == 1 && items[0].isEmpty()) {
			return Collections.emptyList();
		}

		return Arrays.asList(items);
	}
}
