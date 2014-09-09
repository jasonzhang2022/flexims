package com.flexdms.flexims.rsutil;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Object used for Error Report Only. The response represents an unexpected exception
 * 
 * @author jason.zhang
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RSMsg implements Serializable {

	/**
	 * 
	 */
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
