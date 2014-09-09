package com.flexdms.flexims.query;

import com.flexdms.flexims.util.CodedException;

public class QueryClassException extends CodedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CODE = 503;

	public QueryClassException(String msg) {
		super(msg);
	}

	public QueryClassException(String message, Throwable cause) {
		super(message, cause);
	}

	public QueryClassException(Throwable cause) {
		super(cause);
	}

	@Override
	public int getAppCode() {
		return CODE;
	}

}
