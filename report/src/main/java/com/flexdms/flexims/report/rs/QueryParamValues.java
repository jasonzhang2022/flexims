package com.flexdms.flexims.report.rs;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * <?xml version="1.0" encoding="UTF-8"?>
 <queryParamValues>
 <paramValues>
 <index>0</index>
 <values>test</values>
 <values>test1</values>
 </paramValues>
 <paramValues>
 <index>1</index>
 </paramValues>
 <paramValues>
 <index>2</index>
 <values>test3</values>
 <values>test4</values>
 </paramValues>
 </queryParamValues>

 {
 "queryParamValues" : {
 "paramValues" : [ {
 "index" : 0,
 "values" : [ "test", "test1" ]
 }, {
 "index" : 1,
 "values" : [ ]
 }, {
 "index" : 2,
 "values" : [ "test3", "test4" ]
 } ]
 }
 }
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class QueryParamValues {

	ArrayList<QueryParamValue> paramValues;

	public ArrayList<QueryParamValue> getParamValues() {
		return paramValues;
	}

	public void setParamValues(ArrayList<QueryParamValue> paramValues) {
		this.paramValues = paramValues;
	}

	public void addParamValues(String... values) {
		if (paramValues == null) {
			paramValues = new ArrayList<>();
		}
		paramValues.add(new QueryParamValue(paramValues.size(), values));
	}
}
