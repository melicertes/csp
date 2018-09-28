package com.fraunhofer.csp.rt.ticket;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public enum TicketStatus {

	NEW, OPEN, STALLED, RESOLVED, REJECTED, DELETED, UNKNOWN;

	public static TicketStatus getTicketStatusFor(String status) {
		switch (status) {
		case "new":
			return NEW;
		case "open":
			return OPEN;
		case "stalled":
			return STALLED;
		case "resolved":
			return RESOLVED;
		case "rejected":
			return REJECTED;
		case "deleted":
			return DELETED;
		default:
			return UNKNOWN;
		}
	}
}