package com.flexdms.flexims.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class SessionCtx implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Map<String, Object> attrs = new HashMap<String, Object>();

	public Object getAttr(String name) {
		return attrs.get(name);
	}

	public void putAttr(String name, Object value) {
		attrs.put(name, value);
	}

	public Map<String, Object> getAttrs() {
		return attrs;
	}

}
