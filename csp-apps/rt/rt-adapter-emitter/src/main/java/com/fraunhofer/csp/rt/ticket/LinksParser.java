package com.fraunhofer.csp.rt.ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Provider
@Consumes(MediaType.TEXT_PLAIN)
public class LinksParser implements MessageBodyReader<Links> {
	private static final Logger LOG = LoggerFactory.getLogger(LinksParser.class);

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		LOG.debug("isReadable ...");
		LOG.debug("type: " + genericType);
		LOG.debug("class: " + type);
		LOG.debug("media type: " + mediaType);
		return (Links.class.equals(type) && MediaType.TEXT_PLAIN_TYPE.withCharset("utf-8").equals(mediaType));
	}

	@Override
	public Links readFrom(Class<Links> clazz, Type type, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		LOG.debug("readFrom");
		final Predicate<String> acceptNonEmptyLines = line -> !line.trim().isEmpty();
		return new BufferedReader(new InputStreamReader(entityStream)).lines().skip(2) // Status and empty line
				.filter(acceptNonEmptyLines).collect(LinksCollector.create());
	}

	private static class LinksCollector {
		private static final Pattern KEY_LINE_PATTERN = Pattern.compile("^(.*?): ?(.*)$");
		private static final Pattern CONTINUING_LINE_PATTERN = Pattern.compile("^\\s+(\\S.*)$");

		private String currentKey;
		private final StringBuilder currentValueBuilder;
		private Links links;

		private final LinksDataMapper dataMapper;

		LinksCollector() {
			LOG.debug("LinksCollector -> ctor");
			this.currentKey = "";
			this.currentValueBuilder = new StringBuilder();
			this.dataMapper = new LinksDataMapper();
			this.links = new Links();
		}

		static Collector<String, LinksCollector, Links> create() {
			LOG.debug("static initialiser: LinksCollector");
			return Collector.of(LinksCollector::new, LinksCollector::accumulate, (acc, id) -> acc,
					LinksCollector::finish);
		}

		void accumulate(String line) {

			final Matcher continuingLineMatcher = CONTINUING_LINE_PATTERN.matcher(line);
			if (continuingLineMatcher.matches()) {
				currentValueBuilder.append(continuingLineMatcher.group(1));
				updateLinks(this.links, currentKey, currentValueBuilder.toString());
				return;
			}

			final Matcher keyLineMatcher = KEY_LINE_PATTERN.matcher(line);
			if (keyLineMatcher.matches()) {
				currentKey = keyLineMatcher.group(1);
				currentValueBuilder.setLength(0);
				currentValueBuilder.append(keyLineMatcher.group(2));

				updateLinks(this.links, currentKey, currentValueBuilder.toString());
			}
		}

		private void updateLinks(Links links, String key, String value) {
			dataMapper.set(links, key, value);
		}

		Links finish() {
			return this.links;
		}
	}

}
