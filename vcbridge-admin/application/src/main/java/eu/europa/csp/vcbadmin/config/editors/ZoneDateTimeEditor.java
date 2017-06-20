package eu.europa.csp.vcbadmin.config.editors;

import java.beans.PropertyEditorSupport;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.util.StringUtils;

public class ZoneDateTimeEditor extends PropertyEditorSupport {

	private final DateTimeFormatter dateFormat;

	private final boolean allowEmpty;

	private final int exactDateLength;

	public ZoneDateTimeEditor(DateTimeFormatter dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	public ZoneDateTimeEditor(DateTimeFormatter dateFormat, boolean allowEmpty, int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
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
				setValue(ZonedDateTime.parse(text, dateFormat));
			} catch (DateTimeParseException ex) {
				// throw new IllegalArgumentException("Could not parse date: " +
				// ex.getMessage(), ex);
				setValue(null);
			}
		}
	}

	@Override
	public String getAsText() {
		ZonedDateTime value = (ZonedDateTime) getValue();
		return (value != null ? DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm").format(value) : "");
	}

}
