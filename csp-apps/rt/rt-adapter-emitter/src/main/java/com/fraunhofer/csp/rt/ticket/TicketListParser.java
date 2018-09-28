package com.fraunhofer.csp.rt.ticket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 * Created by Majid Salehi on 4/8/17.
 */
@Provider
@Consumes(MediaType.TEXT_PLAIN)
public class TicketListParser implements MessageBodyReader<List<Ticket>> {
	//private static final Logger LOG = LoggerFactory.getLogger(TicketListParser.class);

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// LOG.debug("isReadable ...");
		// LOG.debug("type: " + genericType);
		// LOG.debug("class: " + type);
		// LOG.debug("media type: " + mediaType);
		return (List.class.equals(type)
				&& Ticket.class.equals(((ParameterizedType) genericType).getActualTypeArguments()[0])
				&& MediaType.TEXT_PLAIN_TYPE.withCharset("utf-8").equals(mediaType));
		// || (Links.class.equals(type) &&
		// MediaType.TEXT_PLAIN_TYPE.withCharset("utf-8").equals(mediaType));
	}

	@Override
	public List<Ticket> readFrom(Class<List<Ticket>> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		final Predicate<String> acceptNonEmptyLines = line -> !line.trim().isEmpty();
		return new BufferedReader(new InputStreamReader(entityStream)).lines().skip(2) // Status and empty line
				.filter(acceptNonEmptyLines).collect(TicketCollector.create());
	}

	private static class TicketCollector {
		private static final Pattern KEY_LINE_PATTERN = Pattern.compile("^(.*?): ?(.*)$");
		private static final Pattern CONTINUING_LINE_PATTERN = Pattern.compile("^\\s+(\\S.*)$");

		private String currentKey;
		private final StringBuilder currentValueBuilder;
		private Ticket currentTicket;

		private final TicketDataMapper ticketDataMapper;
		private final List<Ticket> tickets;

		TicketCollector() {
			this.tickets = new ArrayList<>();
			this.currentKey = "";
			this.currentValueBuilder = new StringBuilder();
			this.currentTicket = new Ticket();
			this.ticketDataMapper = new TicketDataMapper();
		}

		static Collector<String, TicketCollector, List<Ticket>> create() {
			return Collector.of(TicketCollector::new, TicketCollector::accumulate, (acc, id) -> acc,
					TicketCollector::finish);
		}

		void accumulate(String line) {
			if ("--".equals(line)) {
				tickets.add(currentTicket);
				currentTicket = new Ticket();
				return;
			}

			final Matcher continuingLineMatcher = CONTINUING_LINE_PATTERN.matcher(line);
			if (continuingLineMatcher.matches()) {
				currentValueBuilder.append(continuingLineMatcher.group(1));
				updateTicket(currentTicket, currentKey, currentValueBuilder.toString());
				return;
			}

			final Matcher keyLineMatcher = KEY_LINE_PATTERN.matcher(line);
			if (keyLineMatcher.matches()) {
				currentKey = keyLineMatcher.group(1);
				currentValueBuilder.setLength(0);
				currentValueBuilder.append(keyLineMatcher.group(2));

				updateTicket(currentTicket, currentKey, currentValueBuilder.toString());
			}
		}

		private void updateTicket(Ticket ticket, String key, String value) {
			ticketDataMapper.set(ticket, key, value);
		}

		List<Ticket> finish() {
			if (Objects.nonNull(currentTicket.getQueue())) {
				tickets.add(currentTicket);
			}

			return tickets;
		}
	}
}
