package eu.europa.csp.vcbadmin.config.formatters;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.europa.csp.vcbadmin.model.CustomUserDetails;

public class ZoneDateTimeFormatter implements Formatter<ZonedDateTime> {

	private DateTimeFormatter dateTimeFormatter;

	public ZoneDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
		this.dateTimeFormatter = dateTimeFormatter;
	}

	@Override
	public String print(ZonedDateTime object, Locale locale) {
		String tz = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			CustomUserDetails p = (CustomUserDetails) auth.getPrincipal();
			tz = p.getTimezone();
			try {
				return ZonedDateTime.ofInstant(object.toInstant(), ZoneId.of(tz))
						.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
			} catch (Exception e) {
				// ignore
			}
		}
		return object.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
	}

	@Override
	public ZonedDateTime parse(String text, Locale locale) throws ParseException {
		ZonedDateTime result = null;
		result = ZonedDateTime.parse(text, dateTimeFormatter);
		return result;
	}

}
