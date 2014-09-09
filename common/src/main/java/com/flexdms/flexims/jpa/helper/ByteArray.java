package com.flexdms.flexims.jpa.helper;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlValue;

import org.apache.commons.io.IOUtils;

public class ByteArray implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlValue
	public byte[] value;

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public void setValue(InputStream value) {
		try {
			this.value = IOUtils.toByteArray(value);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ByteArray(InputStream value) {
		super();
		setValue(value);
	}

	public ByteArray(byte[] value) {
		super();
		this.value = value;
	}

	public ByteArray() {

	}
}
