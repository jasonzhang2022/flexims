package com.flexdms.flexims.util;

public class ValueFormatException extends CodedException {
	private static final long serialVersionUID = 1L;
	public static final int CODE = 1;

	public ValueFormatException(String message) {
		super(message);
	}

	@Override
	public int getAppCode() {
		return CODE;
	}

	public ValueFormatException(String message, Object[] params) {
		super(message, params);
	}

}
