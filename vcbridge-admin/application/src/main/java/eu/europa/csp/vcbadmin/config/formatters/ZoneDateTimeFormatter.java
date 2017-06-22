package eu.europa.csp.vcbadmin.config.formatters;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

public class ZoneDateTimeFormatter implements Formatter<ZonedDateTime> {

	private DateTimeFormatter dateTimeFormatter;

	public ZoneDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public String print(ZonedDateTime object, Locale locale) {
		return object.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	@Override
	public ZonedDateTime parse(String text, Locale locale) throws ParseException {
		ZonedDateTime result = null;
		result = ZonedDateTime.parse(text, dateTimeFormatter);
		return result;
	}

}
