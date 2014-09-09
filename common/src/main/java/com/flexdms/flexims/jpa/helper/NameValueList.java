package com.flexdms.flexims.jpa.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Generic class to represent a map or list.
 * 
 * @author jason.zhang
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NameValueList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElement
	ArrayList<NameValuePair> entry;

	public ArrayList<NameValuePair> getEntry() {
		return entry;
	}

	public void setEntry(ArrayList<NameValuePair> entry) {
		this.entry = entry;
	}

	public NameValueList(ArrayList<NameValuePair> entry) {
		super();
		this.entry = entry;
	}

	public NameValueList() {

	}

	public Map<String, String> toMap() {
		Map<String, String> map = new TreeMap<>();
		for (NameValuePair nv : entry) {
			map.put(nv.getName(), nv.getValue());
		}
		return map;
	}

	public static NameValueList fromMap(Map<String, String> map) {
		NameValueList pairs = new NameValueList();
		pairs.entry = new ArrayList<>(map.size());
		for (Map.Entry<String, String> entry : map.entrySet()) {
			pairs.entry.add(new NameValuePair(entry.getKey(), entry.getValue()));
		}
		return pairs;
	}

	public List<String> toList() {
		ArrayList<String> list = new ArrayList<>();
		for (int i = 0; i < entry.size(); i++) {
			list.add(null);
		}

		for (NameValuePair nv : entry) {
			int index = Integer.parseInt(nv.getName());
			list.set(index, nv.getValue());

		}
		return list;
	}

	public static NameValueList fromList(List<String> list) {
		NameValueList pairs = new NameValueList();
		pairs.entry = new ArrayList<>(list.size());
		int count = 0;
		for (String value : list) {
			pairs.entry.add(new NameValuePair(String.valueOf(count), value));
			count++;
		}
		return pairs;
	}

	public NameValueList addPair(String name, String value) {
		if (entry == null) {
			entry = new ArrayList<>();
		}
		entry.add(new NameValuePair(name, value));
		return this;
	}
}
