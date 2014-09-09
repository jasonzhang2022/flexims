package com.flexdms.flexims.config;

import java.util.HashMap;
import java.util.Map;

import com.flexdms.flexims.App;
import com.flexdms.flexims.AppCache;

/**
 * Global Cache for Configuration TODO: use a cache instead of hash map to
 * accommodate cluster environment.
 * 
 * @author jason.zhang
 * 
 */
public final class Configs {

	public static final String CACHE_KEY = "FxConfigs";

	private Configs() {
	}

	public static final int CONFIG_ENTRIES_NUM = 100;

	@SuppressWarnings("unchecked")
	public static Map<String, ConfigItem> getItems() {
		AppCache appCache = App.getInstance(AppCache.class);
		return (Map<String, ConfigItem>) appCache.get(CACHE_KEY);

	}

	public static ConfigItem getConfigItem(String name) {
		Map<String, ConfigItem> items = getItems();
		if (items == null) {
			return null;
		}
		return items.get(name);
	}

	public static String getConfig(String name) {
		return getConfig(name, null);
	}

	public static String getConfig(String name, String defaultValue) {
		ConfigItem ci = getConfigItem(name);
		if (ci != null) {
			return ci.getValue(defaultValue);
		} else {
			return defaultValue;
		}
	}

	public static boolean getConfigAsBoolean(String name, boolean defaultValue) {
		ConfigItem ci = getConfigItem(name);
		if (ci != null) {
			return ci.getBooleanValue(defaultValue);
		} else {
			return defaultValue;
		}
	}

}
