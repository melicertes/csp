package eu.europa.csp.vcbadmin.config.converters;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public final class DurationConverter implements Converter<String, Duration> {

	private final DateTimeFormatter formatter;

	public DurationConverter(String dateFormat) {
		this.formatter = DateTimeFormatter.ofPattern(dateFormat);
	}

	public DurationConverter(DateTimeFormatter formatter) {
		this.formatter = formatter;
	}

	@Override
	public Duration convert(String source) {
		if (source == null || source.isEmpty()) {
			return null;
		}

		LocalTime time = LocalTime.parse(source, formatter);
		return Duration.ofHours(time.getHour()>=12?time.getHour()-12:time.getHour()).plusMinutes(time.getMinute());
	}
}