package com.flexdms.flexims.accesscontrol;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.RoleUtils;
import com.flexdms.flexims.util.SessionCtx;

@RequestScoped
public class RoleContext {
	public static final String SESSION_USER_KEY = "fxuser";

	@Inject
	SessionCtx sessionCtx;

	/**
	 * Ordered role context. last role should be checked first by permission
	 * check
	 */
	private LinkedList<FxRole> roles = new LinkedList<FxRole>();

	@PostConstruct
	public void setLoginedUser() {
		FxUser user = (FxUser) sessionCtx.getAttr(SESSION_USER_KEY);
		if (user != null) {
			roles.add(user);
		} else {
			roles.add(RoleUtils.getAnonymousRole());
		}
	}

	public LinkedList<FxRole> getRoles() {
		return roles;
	}

	public void setRoles(LinkedList<FxRole> roles) {
		this.roles = roles;
	}

	public boolean hasRole(FxRole role) {
		for (FxRole r : roles) {
			if (r.getId() == role.getId()) {
				return true;
			}
		}
		return false;
	}

	public boolean include(FxRole role) {
		for (FxRole r : roles) {
			if (role.isIncludedBy(r)) {
				return true;
			}
		}
		return false;
	}
}
