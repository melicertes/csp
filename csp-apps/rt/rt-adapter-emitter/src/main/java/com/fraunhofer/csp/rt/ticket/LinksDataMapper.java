package com.fraunhofer.csp.rt.ticket;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class LinksDataMapper {
	private static final Logger LOG = Logger.getLogger(LinksDataMapper.class.getName());
	private static final Map<String, Field> FIELDS_BY_KEY = new HashMap<>();
	private static final Map<String, Field> FIELDS_BY_VARIABLE_KEY = new HashMap<>();

	static {
		final Field[] fields = Links.class.getDeclaredFields();
		for (Field field : fields) {
			final Key key = field.getAnnotation(Key.class);

			if (Objects.nonNull(key)) {
				field.setAccessible(true);

				final String keyValue = key.value();
				if (keyValue.contains("%s")) {
					FIELDS_BY_VARIABLE_KEY.put(keyValue, field);
				} else {
					FIELDS_BY_KEY.put(keyValue, field);
				}
			}
		}
	}

	void set(Links links, String key, String value) {
		final Optional<Field> fieldOpt = findFieldByKey(key);
		if (fieldOpt.isPresent()) {
			setToField(links, fieldOpt.get(), value);
		} else {
			setToVariableField(links, key, value);
		}
	}

	private Optional<Field> findFieldByKey(String key) {
		return Optional.ofNullable(FIELDS_BY_KEY.get(key));
	}

	private void setToField(Links links, Field field, String value) {
		findConverter(field).ifPresent(converter -> setToField(links, field, converter.apply(value)));
	}

	private Optional<Function<String, Object>> findConverter(Field field) {
		Function<String, Object> converter = null;

		final Class<?> targetType = field.getType();
		if (targetType == String.class) {
			converter = Converters::stringConverter;
		} else if (targetType == TicketStatus.class) {
			converter = Converters::ticketStatusConverter;
		} else if (targetType == LocalDateTime.class) {
			converter = Converters::localDateTimeConverter;
		} else if (targetType == Duration.class) {
			converter = Converters::durationConverter;
		} else if (targetType == List.class
				&& String.class.equals(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0])) {
			converter = Converters::stringListConverter;
		}

		return Optional.ofNullable(converter);
	}

	private void setToField(Links links, Field field, Object value) {
		try {
			field.set(links, value);
		} catch (IllegalAccessException e) {
			LOG.log(Level.WARNING, "Could not set value to field for ticket " + links.getId(), e);
		}
	}

	private void setToVariableField(Links links, String key, String value) {
		for (Map.Entry<String, Field> entry : FIELDS_BY_VARIABLE_KEY.entrySet()) {
			final String fieldKey = entry.getKey();

			final String start = fieldKey.substring(0, fieldKey.indexOf("%s"));
			final String end = fieldKey.substring(fieldKey.indexOf("%s") + 2);

			if (key.startsWith(start) && key.endsWith(end)) {
				final String extractedKey = key.substring(start.length(), key.length() - end.length());
				final String convertedValue = Converters.stringConverter(value);

				final Field field = entry.getValue();

				try {
					@SuppressWarnings("unchecked")
					final Map<String, String> map = (Map<String, String>) field.get(links);
					map.put(extractedKey, convertedValue);
				} catch (IllegalAccessException e) {
					LOG.log(Level.WARNING,
							"Could not update value for field '" + fieldKey + "' of ticket " + links.getId(), e);
				}
			}
		}
	}
}
