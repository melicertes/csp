package eu.europa.csp.vcbadmin.config;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public final class ZonedDateTimeConverter implements Converter<String, ZonedDateTime> {

	private final DateTimeFormatter formatter;

	public ZonedDateTimeConverter(String timeFormat) {
		this.formatter = DateTimeFormatter.ofPattern(timeFormat);
	}
	public ZonedDateTimeConverter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public ZonedDateTime convert(String source) {
		if (source == null || source.isEmpty()) {
			return null;
		}
		
		return ZonedDateTime.parse(source, formatter);
	}
}