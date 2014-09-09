package com.flexdms.flexims.accesscontrol;

/*
 * The result for a requested action
 */
public enum PermissionResult {
	Allow, Deny, Undecided;
	public boolean isDecisive() {
		return !(this == PermissionResult.Undecided);
	}
}
