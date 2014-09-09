package com.flexdms.flexims.util;

public abstract class CodedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public abstract int getAppCode();

	public CodedException() {
		super();
	}

	public CodedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CodedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CodedException(String message) {
		super(message);
	}

	public CodedException(Throwable cause) {
		super(cause);
	}

	public CodedException(String message, Object[] params) {
		super(message);
	}

}
