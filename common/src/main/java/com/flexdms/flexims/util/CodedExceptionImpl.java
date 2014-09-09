package com.flexdms.flexims.util;

public class CodedExceptionImpl extends CodedException {


	private static final long serialVersionUID = 1L;
	int code;
	@Override
	public int getAppCode() {
		return code;
	}

	public CodedExceptionImpl(int c) {
		super();
		this.code=c;
	}

	public CodedExceptionImpl(int c, String message, Object[] params) {
		super(message, params);
		this.code=c;
	}

	public CodedExceptionImpl(int c, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code=c;
	}

	public CodedExceptionImpl(int c, String message, Throwable cause) {
		super(message, cause);
		this.code=c;
	}

	public CodedExceptionImpl(int c, String message) {
		super(message);
		this.code=c;
	}

	public CodedExceptionImpl(int c, Throwable cause) {
		super(cause);
		this.code=c;
	}

	
}
