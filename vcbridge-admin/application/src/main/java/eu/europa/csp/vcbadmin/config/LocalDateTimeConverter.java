package eu.europa.csp.vcbadmin.config;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public final class LocalDateTimeConverter implements Converter<String, LocalTime> {

	private final DateTimeFormatter formatter;

	public LocalDateTimeConverter(String timeFormat) {
		this.formatter = DateTimeFormatter.ofPattern(timeFormat);
	}

	@Override
	public LocalTime convert(String source) {
		if (source == null || source.isEmpty()) {
			return null;
		}

		return LocalTime.parse(source, formatter);
	}
}