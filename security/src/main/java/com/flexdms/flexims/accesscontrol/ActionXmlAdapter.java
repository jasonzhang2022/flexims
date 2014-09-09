package com.flexdms.flexims.accesscontrol;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ActionXmlAdapter extends XmlAdapter<String, Action> {

	@Override
	public Action unmarshal(String v) throws Exception {
		return ACLHelper.getActionByName(v);
	}

	@Override
	public String marshal(Action v) throws Exception {
		return v.getName();
	}

}
