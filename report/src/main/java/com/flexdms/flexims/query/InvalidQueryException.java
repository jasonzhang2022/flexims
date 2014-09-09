package com.flexdms.flexims.query;

import com.flexdms.flexims.util.CodedException;

public class InvalidQueryException extends CodedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CODE = 500;

	public InvalidQueryException(String msg) {
		super(msg);
	}

	public InvalidQueryException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidQueryException(Throwable cause) {
		super(cause);
	}

	@Override
	public int getAppCode() {
		return CODE;
	}

}
