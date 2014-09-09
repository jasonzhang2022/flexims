package com.flexdms.flexims.jpa.helper;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

public class ByteArrayConveter implements Converter {

	private static final long serialVersionUID = 1L;

	@Override
	public Object convertObjectValueToDataValue(Object objectValue, Session session) {

		if (objectValue == null) {
			return null;
		}
		return ((ByteArray) objectValue).value;
	}

	@Override
	public Object convertDataValueToObjectValue(Object dataValue, Session session) {
		if (dataValue == null) {
			return null;
		}
		return new ByteArray((byte[]) dataValue);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public void initialize(DatabaseMapping mapping, Session session) {

	}

}
