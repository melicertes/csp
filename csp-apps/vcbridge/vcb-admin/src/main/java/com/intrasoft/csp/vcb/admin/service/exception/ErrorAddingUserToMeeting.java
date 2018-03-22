package com.intrasoft.csp.vcb.admin.service.exception;

public class ErrorAddingUserToMeeting extends OpenfireException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4717948649701517570L;

	public ErrorAddingUserToMeeting(String message) {
		super(message);
	}

	public ErrorAddingUserToMeeting(String message, Exception e) {
		super(message, e);
	}
}