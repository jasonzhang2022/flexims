package com.flexdms.flexims.util;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.flexdms.flexims.jpa.PrimitiveType;

public final class ValueUtils {
	private ValueUtils() {
	}
	/**
	 * 
	 * @param pt
	 *            type
	 * @param value
	 *            an single object value
	 * @return
	 */
	public static String singleValueToString(PrimitiveType pt, Object value) {
		if (value == null) {
			return null;
		}
		if (pt == null) {
			return value.toString();
		}
		switch (pt) {
		case BOOLEAN:
			return value.toString();
		case DATE:
			return Utils.dateToString((Date) value);
		case TIME:
			return Utils.dateToString((Date) value);
		case TIMESTAMP:
			return Utils.dateToString((Date) value);
		default:
			return value.toString();
		}

	}

	public static Object toObjectValue(String value, PrimitiveType pt, boolean multiple) {
		if (value == null) {
			return null;
		}
		if (multiple) {
			List<Object> values = new LinkedList<Object>();
			String[] vs = value.split(",");
			for (String v : vs) {

				values.add(toSingleObjectValue(v, pt));

			}
			return values;
		} else {

			return toSingleObjectValue(value, pt);

		}
	}


	public static Object toSingleObjectValue(String value, PrimitiveType pt) {
		if (value == null) {
			return null;
		}
		if (pt == null) {
			try {
				return Long.valueOf(value);
			} catch (NumberFormatException e) {
				// throw new
				// ValueFormatException("A instance id (number) is expected. but get {0}",
				// new Object[]{value.toString()});
				throw new ValueFormatException("A instance id (number) is expected. but get {0}", new Object[] { value.toString() });
			}

		}
		switch (pt) {
		case BOOLEAN:
			try {
				return Utils.stringToBoolean(value, true);
			} catch (Exception e) {
				// throw new
				// ValueFormatException("A True or false string is expected. but get {0}",
				// new Object[]{value.toString()});
				throw new ValueFormatException("A True or false string is expected. but get {0}", new Object[] { value.toString() });
			}
		case FLOAT:
			try {
				Float d = Utils.stringToFloat(value);
				return d;
			} catch (NumberFormatException e) {
				// throw new
				// ValueFormatException("A fractional number is expected. but get {0}",
				// new Object[]{value.toString()});
				throw new ValueFormatException("A fractional number is expected. but get {0}", new Object[] { value.toString() });
			}
		case DOUBLE:
			try {
				Double d = Utils.stringToDouble(value);
				return d;
			} catch (NumberFormatException e) {
				// throw new
				// ValueFormatException("A fractional number is expected. but get {0}",
				// new Object[]{value.toString()});
				throw new ValueFormatException("A fractional number is expected. but get {0}", new Object[] { value.toString() });
			}
		case INTEGER:
			try {
				Integer l = Utils.stringToInteger(value);
				return l;
			} catch (NumberFormatException e) {
				// throw new
				// ValueFormatException("A number is expected. but get {0}", new
				// Object[]{value.toString()});
				throw new ValueFormatException("A number is expected. but get {0}", new Object[] { value.toString() });
			}
		case LONG:
			try {
				Long l = Utils.stringToLong(value);
				return l;
			} catch (NumberFormatException e) {
				// throw new
				// ValueFormatException("A number is expected. but get {0}", new
				// Object[]{value.toString()});
				throw new ValueFormatException("A number is expected. but get {0}", new Object[] { value.toString() });
			}
		case DATE:
			try {
				Date retDate = ValueUtils.calculateRelativeDate(value);
				if (retDate == null) {
					retDate = Utils.stringToDate(value);
				}
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(retDate);

				// zero out unneeded field
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				return new Date(calendar.getTimeInMillis());

			} catch (Exception e) {
				// throw new
				// ValueFormatException("Date format is not correct. Correct format should be  like 2001-02-15");
				throw new ValueFormatException("Date format is not correct. Correct format should be  like 2001/02/15");
			}
		case TIME:
			try {
				Date retDate = Utils.stringToTime(value);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(retDate);

				// zero out unneeded field
				calendar.set(Calendar.YEAR, 1970);
				calendar.set(Calendar.MONTH, 0);
				calendar.set(Calendar.DATE, 1);
				calendar.set(Calendar.MILLISECOND, 0);
				return new Date(calendar.getTimeInMillis());
			} catch (Exception e) {
				// throw new
				// ValueFormatException("Time is not correct. Correct format should be  like 15:30:23");
				throw new ValueFormatException("Time is not correct. Correct format should be  like 15:30:23");
			}
		case TIMESTAMP:
			try {
				Date retDate = ValueUtils.calculateRelativeDate(value);
				if (retDate != null) {
					return retDate;
				}
				return Utils.stringToDateTime(value);
			} catch (Exception e) {
				// throw new
				// ValueFormatException("Date Time format is not correct. Correct format should be  like 2001-02-15 15:30:23");
				throw new ValueFormatException("Date Time format is not correct. Correct format should be  like 2001/02/15 15:30:23");
			}
		default:
			return value;
		}
	}

	public static Date calculateRelativeDate(String str) {

		String[] segStrings = str.split(":");
		if (segStrings.length != 3) {
			return null;
		}
		if (!segStrings[2].equalsIgnoreCase("true") && !segStrings[2].equalsIgnoreCase("false")) {
			return null;
		}
		boolean wholetime = Boolean.parseBoolean(segStrings[2]);
		TimeUnit tu = TimeUnit.valueOf(segStrings[1]);
		if (tu == null) {
			return null;
		}
		long number = 0;
		try {
			number = Long.parseLong(segStrings[0]);
		} catch (NumberFormatException e) {
			return null;
		}

		long t = ValueUtils.getValue(tu, number, wholetime);
		return new Date(t);
	}

	public static long getValue(TimeUnit tu, long numl, boolean adjustToBegin) {
		try {
			int num = new Long(numl).intValue();
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);

			if (adjustToBegin) {
				switch (tu) {
				case DAY:
					cal.set(Calendar.HOUR_OF_DAY, 0);
					break;
				case HOUR:
					cal.set(Calendar.MINUTE, 0);
					break;
				case MINUTE:
					cal.set(Calendar.SECOND, 0);
					break;
				case MONTH:
					cal.set(Calendar.DAY_OF_MONTH, 0);
					break;
				case WEEK:
					cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
					cal.add(Calendar.WEEK_OF_YEAR, -1);
					break;
				case YEAR:
					cal.set(Calendar.DAY_OF_YEAR, 0);
					break;
				default:
					break;

				}

			}

			// set hour, minute, second to 00:00:00
			switch (tu) {
			case DAY:

				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				break;
			case HOUR:
				cal.set(Calendar.SECOND, 0);
				break;
			case MINUTE:
				break;
			case MONTH:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				break;
			case WEEK:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				break;
			case YEAR:
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				break;
			default:
				break;

			}

			if (num != 0) {
				switch (tu) {
				case DAY:
					cal.add(Calendar.DAY_OF_MONTH, num);
					break;
				case HOUR:
					cal.add(Calendar.HOUR_OF_DAY, num);
					break;
				case MINUTE:
					cal.add(Calendar.MINUTE, num);
					break;
				case MONTH:
					cal.add(Calendar.MONTH, num);
					break;
				case WEEK:
					cal.add(Calendar.WEEK_OF_YEAR, num);
					break;
				case YEAR:
					cal.add(Calendar.YEAR, num);
					break;
				default:
					break;

				}
			}

			return cal.getTimeInMillis();

		} catch (NumberFormatException e) {
			throw new ValueFormatException("A number is expected for relative start or end Date/Time");
		}
	}

}
