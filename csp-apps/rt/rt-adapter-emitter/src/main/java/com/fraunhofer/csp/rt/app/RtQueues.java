package com.fraunhofer.csp.rt.app;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public enum RtQueues {
	INCIDENT_QUEUE("Incidents"), INCIDENT_REPORTS_QUEUE("Incident Reports");

	private final String queue;

	/**
	 * @param queue
	 */
	private RtQueues(final String queue) {
		this.queue = queue;
	}

	@Override
	public String toString() {
		return queue;
	}
}