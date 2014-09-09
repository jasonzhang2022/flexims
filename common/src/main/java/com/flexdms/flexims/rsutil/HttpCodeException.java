package com.flexdms.flexims.rsutil;

public class HttpCodeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int code;
	int appCode;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HttpCodeException(int code, String msg) {
		super(msg);
		this.code = code;
		this.appCode = code;
	}

	public int getAppCode() {
		return appCode;
	}

	public void setAppCode(int appCode) {
		this.appCode = appCode;
	}

}
