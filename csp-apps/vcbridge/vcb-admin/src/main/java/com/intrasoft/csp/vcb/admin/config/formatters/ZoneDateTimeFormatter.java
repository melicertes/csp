package com.intrasoft.csp.vcb.admin.config.formatters;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.intrasoft.csp.vcb.admin.model.CustomUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.Formatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ZoneDateTimeFormatter implements Formatter<ZonedDateTime> {

	private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ISO_ZONED_DATE_TIME;

	@Value(value = "${event.show.timezone.default:Europe/Athens}")
	String tz_default;

//	public ZoneDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
//		this.dateTimeFormatter = dateTimeFormatter;
//	}

	@Override
	public String print(ZonedDateTime object, Locale locale) {
		String tz = null;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated()) {
			CustomUserDetails p = (CustomUserDetails) auth.getPrincipal();
			tz = p.getTimezone();
			try {
				return ZonedDateTime.ofInstant(object.toInstant(), ZoneId.of(tz))
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")/*
																				 * DateTimeFormatter.
																				 * ISO_ZONED_DATE_TIME
																				 */);
			} catch (Exception e) {
				// ignore
			}
		}
		// use Europe/Athens by default
		return ZonedDateTime.ofInstant(object.toInstant(), ZoneId.of(tz_default))
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		// return object.format(DateTimeFormatter.ofPattern("yyyy-MM-dd
		// HH:mm")/*DateTimeFormatter.ISO_ZONED_DATE_TIME*/);
	}

	@Override
	public ZonedDateTime parse(String text, Locale locale) throws ParseException {
		ZonedDateTime result = null;
		result = ZonedDateTime.parse(text, dateTimeFormatter);
		return result;
	}

}
