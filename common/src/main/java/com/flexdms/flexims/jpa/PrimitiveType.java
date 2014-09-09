package com.flexdms.flexims.jpa;

public enum PrimitiveType {

	SHORTSTRING, STRING, LONGSTRING, INTEGER, LONG, FLOAT, DOUBLE, BIGDECIMAL, BYTEARRAY, BOOLEAN, OBJECT, DATE, TIMESTAMP, TIME, EMAIL, URL, DIRECTORY, FILE;

	public boolean isStringType() {
		return this == STRING || this == SHORTSTRING || this == LONGSTRING;
	}

	public boolean isStringTypeLoose() {
		if (isStringType()) {
			return true;
		}

		if (this == EMAIL || this == URL || this == DIRECTORY || this == FILE) {
			return true;
		}
		return false;
	}

	public boolean isNumeric() {
		if (this == INTEGER || this == LONG || this == FLOAT || this == DOUBLE || this == BIGDECIMAL) {
			return true;
		}
		return false;
	}

	public boolean isDateTime() {
		return this == DATE || this == TIME || this == TIMESTAMP;
	}

	public static final int TYPE_INDEX_EMBEDDED = -1;
	public static final int TYPE_INDEX_RELATION = -2;
	public static final int TYPE_INDEX_STRING = 0;
	public static final int TYPE_INDEX_MIDIUM_STRING = 1;
	public static final int TYPE_INDEX_LONG_STRING = 2;
	public static final int TYPE_INDEX_INT = 3;
	public static final int TYPE_INDEX_LONG = 4;
	public static final int TYPE_INDEX_FLOAT = 5;
	public static final int TYPE_INDEX_DOUBLE = 6;
	public static final int TYPE_INDEX_BIGDECIMAL = 7;
	public static final int TYPE_INDEX_BYTEARRAY = 8;
	public static final int TYPE_INDEX_BOOLEAN = 9;
	public static final int TYPE_INDEX_CUSTOME_OBJECT = 10;
	public static final int TYPE_INDEX_DATE = 11;
	public static final int TYPE_INDEX_TIMESTAMP = 12;
	public static final int TYPE_INDEX_TIME = 13;
	public static final int TYPE_INDEX_EMAIL = 14;
	public static final int TYPE_INDEX_URL = 15;
	public static final int TYPE_INDEX_DIRECTORY = 16;
	public static final int TYPE_INDEX_FILE = 17;

}
