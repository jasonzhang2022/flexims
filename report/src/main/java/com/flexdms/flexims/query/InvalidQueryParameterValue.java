package com.flexdms.flexims.query;

import com.flexdms.flexims.util.CodedException;

public class InvalidQueryParameterValue extends CodedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int CODE = 502;

	public InvalidQueryParameterValue(String message) {
		super(message);
	}

	@Override
	public int getAppCode() {
		return CODE;
	}

}
