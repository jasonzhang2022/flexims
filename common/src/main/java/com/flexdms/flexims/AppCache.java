package com.flexdms.flexims;


/**
 * Cache application-scoped data traditionally stored in static field.
 * @author jason.zhang
 *
 */
public interface AppCache {

	public Object get(String key);
	public void put(String key, Object obj);
}
