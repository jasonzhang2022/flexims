package com.flexdms.flexims.users;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class FxUserSettings {

	public static final String TYPE_NAME = "FxUserSettings";
	public static final String PROP_NAME_PROP_EMAIL = "Email";
	public static final String PROP_NAME_PROP_PASSWORD = "Password";
	public static final String PROP_NAME_PROP_USER = "FxUser";
	FleximsDynamicEntityImpl entityImpl;

	public FxUserSettings() {
		super();
	}

	public FxUserSettings(FleximsDynamicEntityImpl entityImpl) {
		this.entityImpl = entityImpl;
	}

	public FleximsDynamicEntityImpl getEntityImpl() {
		return entityImpl;
	}

	public void setEntityImpl(FleximsDynamicEntityImpl entityImpl) {
		this.entityImpl = entityImpl;
	}

	public String getEmail() {
		return entityImpl.get(PROP_NAME_PROP_EMAIL);
	}

	public String getPassword() {
		return entityImpl.get(PROP_NAME_PROP_PASSWORD);
	}

	public FxUserSettings setEmail(String value) {
		entityImpl.set(PROP_NAME_PROP_EMAIL, value);
		return this;
	}

	public FxUserSettings setPassword(String value) {
		entityImpl.set(PROP_NAME_PROP_PASSWORD, value);
		return this;
	}

}
