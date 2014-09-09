package com.flexdms.flexims.jaxb.moxy;

import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.flexdms.flexims.jpa.helper.NameValueList;

public class MapAdapter extends XmlAdapter<NameValueList, Map<String, String>> {

	@Override
	public Map<String, String> unmarshal(NameValueList v) throws Exception {
		return v.toMap();
	}

	@Override
	public NameValueList marshal(Map<String, String> v) throws Exception {
		return NameValueList.fromMap(v);
	}

}
