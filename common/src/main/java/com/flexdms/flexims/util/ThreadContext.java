package com.flexdms.flexims.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Assume Request scope is thread safe.
 * 
 * @author jason
 * 
 */
public final class ThreadContext {
	public static final int INITIAL_OBJECT_IN_THREAD = 5;

	private ThreadContext() {
	}

	private static final ThreadLocal<Map<String, Object>> TMAP = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			return new HashMap<String, Object>(INITIAL_OBJECT_IN_THREAD);

		}
	};

	public static Object get(String key) {
		return TMAP.get().get(key);
	}

	public static Object put(String key, Object value) {
		return TMAP.get().put(key, value);
	}

	public static Object remove(Object key) {
		return TMAP.get().remove(key);
	}

}
