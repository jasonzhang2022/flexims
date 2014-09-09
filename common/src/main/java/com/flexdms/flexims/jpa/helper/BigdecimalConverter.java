package com.flexdms.flexims.jpa.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

/**
 * Handle Bigdecimal uniformly cross all database platform.
 * 
 * @author jason.zhang
 * 
 */
public class BigdecimalConverter implements Converter {

	public static final int DECIMAL_PRECISION = 5;
	private static final long serialVersionUID = 1L;

	@Override
	public Object convertObjectValueToDataValue(Object objectValue, Session session) {
		if (objectValue == null) {
			return null;
		}
		if (objectValue instanceof String) {
			objectValue = new BigDecimal(objectValue.toString());
		}
		return ((BigDecimal) objectValue).setScale(DECIMAL_PRECISION, RoundingMode.HALF_UP);

	}

	@Override
	public Object convertDataValueToObjectValue(Object dataValue, Session session) {
		return dataValue;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public void initialize(DatabaseMapping mapping, Session session) {

	}

}
