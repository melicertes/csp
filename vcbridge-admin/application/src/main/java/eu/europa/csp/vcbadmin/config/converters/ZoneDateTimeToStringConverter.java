package eu.europa.csp.vcbadmin.config.converters;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

public final class ZoneDateTimeToStringConverter implements Converter<ZonedDateTime, String> {


	@Override
	public String convert(ZonedDateTime source) {
		return source.format(DateTimeFormatter.RFC_1123_DATE_TIME);
	}
}