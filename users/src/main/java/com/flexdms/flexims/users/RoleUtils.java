package com.flexdms.flexims.users;

import javax.ws.rs.GET;

/**
 * Roles Utils
 * 
 * @author jason.zhang
 *
 *         TODO cache the role instead of using static field.
 */
public final class RoleUtils {
	private RoleUtils() {
		
	}
	public static final long ADMIN_ROLE_ID = 999L;
	public static final long ALL_ROLE_ID = 998L;
	public static final long DEVELOPER_ROLE_ID = 997L;
	public static final long ANONYMOUS_ID = 996L;

	static FxRole adminRole;
	static FxRole developerRole;
	static FxRole allRole;
	static FxRole anonymousRole;

	public static FxRole getAdminRole() {
		return adminRole;
	}

	public static FxRole getAllRole() {
		return allRole;
	}

	public static FxRole getAnonymousRole() {
		return anonymousRole;
	}

	public static FxRole getDeveloperRole() {
		return developerRole;
	}

}
