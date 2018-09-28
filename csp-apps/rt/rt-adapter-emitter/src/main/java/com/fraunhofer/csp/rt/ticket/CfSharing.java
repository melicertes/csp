package com.fraunhofer.csp.rt.ticket;

/**
 * Created by Majid Salehi on 4/8/17.
 */
public enum CfSharing {
	DEFAULT_SHARING("default sharing"), NO_SHARING("no sharing");

	private final String sharing;

	/**
	 * @param sharing
	 */
	private CfSharing(final String sharing) {
		this.sharing = sharing;
	}

	@Override
	public String toString() {
		return sharing;
	}
}
