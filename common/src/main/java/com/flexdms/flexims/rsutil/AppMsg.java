package com.flexdms.flexims.rsutil;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Used to report Application specific process result.  The result is an expected
 * answer, not an unexpected exception
 * 
 * @author jason.zhang
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AppMsg  {

	private static final long serialVersionUID = 1L;
	@XmlElement
	long statuscode = 0;
	@XmlElement
	String msg = "OK";

	public long getStatuscode() {
		return statuscode;
	}

	public void setStatuscode(long statuscode) {
		this.statuscode = statuscode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
