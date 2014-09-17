package com.flexdms.flexims.config;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class ConfigItem {
	public static final String TYPE_NAME = "FxConfig";
	public static final String PROP_NAME_PROP_NAME = "Name";
	public static final String PROP_NAME_PROP_DESCRIPTION = "Description";
	public static final String PROP_NAME_PROP_VALUE = "ConfigValue";
	public static final String PROP_NAME_PROP_FORCLIENT = "ForClient";
	public static final String PROP_NAME_PROP_FORADMIN = "ForAdmin";

	FleximsDynamicEntityImpl entity;

	public ConfigItem() {

	}

	public ConfigItem(FleximsDynamicEntityImpl entity) {
		super();
		this.entity = entity;
	}

	public FleximsDynamicEntityImpl getEntity() {
		return entity;
	}

	public void setEntity(FleximsDynamicEntityImpl entity) {
		this.entity = entity;
	}

	public String getName() {
		return entity.get(PROP_NAME_PROP_NAME);
	}

	public String getValue() {
		return entity.get(PROP_NAME_PROP_VALUE);
	}

	public boolean isForClient() {
		Boolean boolean1 = entity.get(PROP_NAME_PROP_FORCLIENT);
		if (boolean1 == null) {
			return false;
		}
		return boolean1;
	}

	public boolean isForAdmin() {
		Boolean boolean1 = entity.get(PROP_NAME_PROP_FORADMIN);
		if (boolean1 == null) {
			return false;
		}
		return boolean1;
	}

	public String getValue(String defaultValue) {
		String ret = getValue();
		if (ret == null || ret.trim().length() == 0) {
			return defaultValue;
		} else {
			return ret;
		}
	}

	public int getIntegerValue(int defaultValue) {
		String strv = getValue();
		if (strv == null) {
			return defaultValue;
		} else {
			return Integer.parseInt(strv);
		}
	}

	public boolean getBooleanValue() {
		return getBooleanValue(false);
	}

	public boolean getBooleanValue(boolean defaultValue) {
		String strv = getValue();
		if (strv == null) {
			return defaultValue;
		} else {
			return com.flexdms.flexims.util.Utils.stringToBoolean(strv, false);
		}
	}

}
