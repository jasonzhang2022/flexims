package com.flexdms.flexims.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public final class Utils {

	private Utils() {
	}
	// use ISO-8601 format. The format used by javascript.
	public static String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
	public static DateTimeFormatter iso8601Formatter = ISODateTimeFormat.dateTime();

	public static String truncateString(String str, int length) {
		if (str == null) {
			return null;
		}
		if (str.length() < length) {
			return str;
		}
		return str.substring(0, length);

	}

	public static boolean stringToBoolean(String v, boolean strict) {
		if (v == null) {
			return false;
		}
		v = v.toUpperCase();
		if (v.equals("YES") || v.equals("Y") || v.equals("1") || v.equals("TRUE") || v.equals("T") || v.equals("ON") || v.equals("CHECKED")
				|| v.equals("CHECK") || v.equals("SELECTED") || v.equals("SELECT")) {
			return true;
		}

		if (v.equals("NO") || v.equals("N") || v.equals("0") || v.equals("FALSE") || v.equals("F") || v.equals("OFF") || v.equals("UNCHECKED")
				|| v.equals("UNCHECK") || v.equals("UNSELECTED") || v.equals("UNSELECT")) {
			return false;
		}
		if (!strict) {
			return false;
		}
		throw new ValueFormatException("String is not a boolean value");
	}

	public static Float stringToFloat(String v) {
		if (v == null) {
			return null;
		}
		try {
			return Float.valueOf(v);
		} catch (NumberFormatException e) {
			throw new ValueFormatException("String is not a number");
		}
	}

	public static Double stringToDouble(String v) {
		if (v == null) {
			return null;
		}
		try {
			return Double.valueOf(v);
		} catch (NumberFormatException e) {
			throw new ValueFormatException("String is not a number");
		}

	}

	public static Integer stringToInteger(String v) {
		if (v == null) {
			return null;
		}

		try {
			return Integer.valueOf(v);
		} catch (NumberFormatException e) {
			throw new ValueFormatException("String is not a number");
		}
	}

	public static Long stringToLong(String v) {
		if (v == null) {
			return null;
		}

		try {
			return Long.valueOf(v);
		} catch (NumberFormatException e) {
			throw new ValueFormatException("String is not a number");
		}
	}

	public static Date stringToDate(String v) {
		return stringToDate(v, true);
	}

	public static String dateToString(Date date) {
		return iso8601Formatter.print(new DateTime(date, DateTimeZone.UTC));
	}
	public static String dateToString(Calendar date) {
		return iso8601Formatter.print(new DateTime(date.getTimeInMillis(), DateTimeZone.forTimeZone(date.getTimeZone())));
	}

	public static Date stringToDate(String v, boolean handleDateTime) {
		if (v == null) {
			return null;
		}
		try {
			return iso8601Formatter.parseDateTime(v).toDate();
		} catch (IllegalArgumentException e) {
			throw new ValueFormatException("String is not a date " + e.getMessage());
		}

	}

	public static Date stringToTime(String v) {
		if (v == null) {
			return null;
		}
		Date d;
		try {
			d = iso8601Formatter.parseDateTime(v).toDate();
			// use sql type for easy persistence
			d = new java.sql.Time(d.getTime());
		} catch (IllegalArgumentException e) {
			throw new ValueFormatException("String is not a date " + e.getMessage());
		}
		return d;

	}

	public static Date stringToDateTime(String v) {
		return stringToDateTime(v, true);
	}

	public static Date stringToDateTime(String v, boolean handleDate) {
		if (v == null) {
			return null;
		}
		Date d;
		try {
			d = iso8601Formatter.parseDateTime(v).toDate();
			// use sql type for easy persistence
			d = new java.sql.Timestamp(d.getTime());
		} catch (IllegalArgumentException e) {
			throw new ValueFormatException("String is not a date " + e.getMessage());
		}
		return d;
	}

	public static Throwable getRootCause(Throwable e) {
		if (e.getCause() == null) {
			return e;
		} else {
			return getRootCause(e.getCause());
		}
	}

	// deep clone object
	public static Object cloneObject(Object td) {
		Object obj = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(td);
			out.flush();
			out.close();

			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			obj = in.readObject();
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ValueFormatException("Can not clone object");
		}
	}

	/*
	 * We supply a class to it is avaialble in the context.
	 */
	public static Object toObject(byte[] data) throws ClassNotFoundException {

		ByteArrayInputStream bins = new ByteArrayInputStream(data);
		try {
			ObjectInputStream in = new ObjectInputStream(bins);
			return in.readObject();
		} catch (IOException e) {
			// ignore
			return null;
		}
	}

	public static byte[] fromObject(Object obj) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			oout.writeObject(obj);
			oout.close();
			return baos.toByteArray();

		} catch (IOException e) {
			// ignore.
			return new byte[0];
		}
	}

	public static String trimString(String value) {

		if (value != null) {
			value = value.trim();
			if (value.length() == 0) {
				value = null;
			}
		}
		return value;
	}

	public static String getMimeType(String fileName) {
		try {
			return Files.probeContentType(Paths.get(fileName));
		} catch (IOException e) {
			return "application/octet-stream";
		}
		/*
		 * Collection types = MimeUtil.getMimeTypes(fileName); return
		 * MimeUtil2.getMostSpecificMimeType(types).toString();
		 */
	}

	public static void zipFolder(File dirFile, OutputStream out) throws IOException {
		ZipOutputStream zipout = new ZipOutputStream(out);
		String baseName = dirFile.getParentFile().getAbsolutePath() + "/";
		addFolderToZip(dirFile, zipout, baseName);
		zipout.close();
	}

	public static void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {

		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				addFolderToZip(file, zip, baseName);
			} else {
				addFileToZip(file, zip, baseName);
			}
		}

	}

	public static void addFileToZip(File file, ZipOutputStream zip, String baseName) throws IOException {
		String name = file.getAbsolutePath().substring(baseName.length());
		ZipEntry zipEntry = new ZipEntry(name);
		zip.putNextEntry(zipEntry);
		IOUtils.copy(new FileInputStream(file), zip);
		zip.closeEntry();
	}

	static Pattern pattern = Pattern.compile("(?<!\\\\),");

	public static String[] splitString(String value) {
		if (value == null) {
			return new String[0];
		}
		String[] vsString = pattern.split(value);
		String[] ret = new String[vsString.length];
		for (int i = 0; i < vsString.length; i++) {
			ret[i] = vsString[i].replaceAll("\\\\,", ",");
		}
		return ret;
	}

	public static String mergeString(String... strings) {
		String[] ret = new String[strings.length];
		for (int i = 0; i < strings.length; i++) {
			ret[i] = strings[i].replace(",", "\\,");
		}
		return StringUtils.join(ret, ",");
	}

	public static List<URL> sortUrlsByPath(List<URL> urls) {
		Collections.sort(urls, new Comparator<URL>() {

			@Override
			public int compare(URL o1, URL o2) {
				return o1.getPath().compareTo(o2.getPath());
			}

		});
		return urls;
	}

	public static String upperFirstLetter(String attributeName) {
		char[] string = attributeName.toCharArray();
		string[0] = Character.toUpperCase(string[0]);
		return new String(string);
	}

}
