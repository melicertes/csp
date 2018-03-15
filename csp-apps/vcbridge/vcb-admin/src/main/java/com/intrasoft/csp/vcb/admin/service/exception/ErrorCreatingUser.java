package com.intrasoft.csp.vcb.admin.service.exception;

public class ErrorCreatingUser extends OpenfireException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4425567139054890622L;

	public ErrorCreatingUser(String message) {
		super(message);
	}
	
	public ErrorCreatingUser(String message, Exception e) {
		super(message, e);
	}

}
