package com.intrasoft.csp.vcb.admin.config.formatters;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class DurationFormatter implements Formatter<Duration> {

	private DateTimeFormatter dateTimeFormatter;

	public DurationFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	public DurationFormatter(String dateFormat) {
		this.dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
	}

	@Override
	public String print(Duration object, Locale locale) {
//		return LocalTime.of(Long.valueOf(object.toHours()).intValue(), Long.valueOf(object.toMinutes()).intValue() % 60)
//				.format(dateTimeFormatter);
		return Long.valueOf(object.toHours()).intValue() +"h "+ Long.valueOf(object.toMinutes()).intValue() % 60 + "m";
	}

	@Override
	public Duration parse(String text, Locale locale) throws ParseException {
		LocalTime time = LocalTime.parse(text, dateTimeFormatter);
		return Duration.ofHours(time.getHour() >= 12 ? time.getHour() - 12 : time.getHour())
				.plusMinutes(time.getMinute());
	}

}
