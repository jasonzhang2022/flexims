package com.flexdms.flexims.report.rs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class QueryParamValue {

	@XmlElement
	int index;

	@XmlElement
	List<String> values;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public List<String> getValues() {
		return values;
	}

	public String getValue() {
		if (values == null || values.isEmpty()) {
			return null;
		}
		return values.get(0);
	}

	public void setValue(String v) {
		if (values == null) {
			values = new ArrayList<>(1);
		}
		if (v != null) {
			values.add(v);
		}
	}

	public String[] getValuesAsArray() {
		if (values == null) {
			return new String[0];
		}
		return values.toArray(new String[0]);
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public void setValues(String... values) {
		this.values = Arrays.asList(values);
	}

	public QueryParamValue(int index, List<String> values) {
		super();
		this.index = index;
		this.values = values;
	}

	public QueryParamValue(List<String> values) {
		super();
		this.values = values;
	}

	public QueryParamValue() {
	}

	public QueryParamValue(int size, String... values) {
		super();
		this.index = size;
		this.values = Arrays.asList(values);
	}

}
