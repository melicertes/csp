
package com.fraunhofer.csp.rt.ticket;

import java.util.Objects;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public class TicketPrinter {

	public void print(Ticket ticket) {
		System.out.println("-[Ticket Start]-");
		print("id", ticket.getId());
		print("Queue", ticket.getQueue());
		print("Owner", ticket.getOwner());
		print("Creator", ticket.getCreator());
		print("Subject", ticket.getSubject());
		print("Status", ticket.getStatus());
		print("Requestors", ticket.getRequestors());
		print("CC", ticket.getCc());
		print("AdminCC", ticket.getAdminCc());
		print("Created", ticket.getCreated());
		print("Starts", ticket.getStarts());
		print("Started", ticket.getStarted());
		print("Due", ticket.getDue());
		print("Resolved", ticket.getResolved());
		print("Last contact", ticket.getLastContact());
		print("Last updated", ticket.getLastUpdated());
		print("Time estimated", ticket.getTimeEstimated());
		print("Time left", ticket.getTimeLeft());
		print("Time worked", ticket.getTimeWorked());
		ticket.getCustomFields().entrySet().forEach(e -> print(e.getKey() + " (custom field)", e.getValue()));

		System.out.println("-[Ticket End]-");
	}

	private void print(String key, Object value) {
		System.out.println(key + ": " + Objects.toString(value, "<null>"));
	}
}
