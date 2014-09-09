package com.flexdms.flexims.query;

import com.flexdms.flexims.util.CodedException;

public class InvalidQueryParameter extends CodedException {

	public static final int CODE = 501;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidQueryParameter(String message) {
		super(message);
	}

	@Override
	public int getAppCode() {
		return CODE;
	}

	public InvalidQueryParameter(String message, Object[] params) {
		super(message, params);
		// TODO Auto-generated constructor stub
	}

}
