package com.flexdms.flexims.auth;

import java.io.Serializable;

import com.flexdms.flexims.users.FxUser;

public class UserPrincipal implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String email;

	public UserPrincipal(final FxUser user) {
		super();
		email = user.getEmail();
	}

	@Override
	public String toString() {
		return email;
	}

	public FxUser getUser() {
		return FxUser.loadByEmail(com.flexdms.flexims.App.getEM(), email);
	}

}
