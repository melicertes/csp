package eu.europa.csp.vcbadmin.config.editors;

import java.beans.PropertyEditorSupport;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

public class DurationEditor extends PropertyEditorSupport {

	private final DateTimeFormatter dateFormat;

	private final boolean allowEmpty;

	private final int exactDateLength;

	public DurationEditor(DateTimeFormatter dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	public DurationEditor(DateTimeFormatter dateFormat, boolean allowEmpty, int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
	}
	
	public DurationEditor(String dateFormat, boolean allowEmpty) {
		this.dateFormat = DateTimeFormatter.ofPattern(dateFormat);
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		} else if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
			throw new IllegalArgumentException(
					"Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
		} else {
			try {
				System.out.println("EDW1");
				LocalTime time = LocalTime.parse(text, this.dateFormat);
				setValue(Duration.ofHours(time.getHour() >= 12 ? time.getHour() - 12 : time.getHour())
						.plusMinutes(time.getMinute()));
				System.out.println("EDW@ "+time);
			} catch (DateTimeParseException ex) {
				System.out.println("EDW2");

				// throw new IllegalArgumentException("Could not parse date: " +
				// ex.getMessage(), ex);
				setValue(null);
			}
		}
	}

	@Override
	public String getAsText() {
		Duration value = (Duration) getValue();
		return (value != null ? LocalTime
				.of(Long.valueOf(value.toHours()).intValue(), Long.valueOf(value.toMinutes()).intValue() % 60)
				.format(dateFormat) : "");
	}

}
