package com.flexdms.flexims.accesscontrol;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.users.FxRole;

/**
 * An object observer can observe to provide customized action
 * 
 * @author jason
 * 
 */
public class ActionContext {
	Action action;
	FxRole role;
	String type;
	FleximsDynamicEntityImpl instance;
	boolean instanceOnly;
	PermissionResult permissionResult = PermissionResult.Undecided;

	public boolean isInstanceOnly() {
		return instanceOnly;
	}

	public void setInstanceOnly(boolean instanceOnly) {
		this.instanceOnly = instanceOnly;
	}

	public PermissionResult getPermissionResult() {
		return permissionResult;
	}

	public void setPermissionResult(PermissionResult permissionResult) {
		this.permissionResult = permissionResult;
	}

	public Action getAction() {
		return action;
	}

	public FxRole getRole() {
		return role;
	}

	public void setRole(FxRole role) {
		this.role = role;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public FleximsDynamicEntityImpl getInstance() {
		return instance;
	}

	public void setInstance(FleximsDynamicEntityImpl instance) {
		this.instance = instance;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public ActionContext(Action action, FxRole role, String type, FleximsDynamicEntityImpl instance, boolean instanceOnly) {
		super();
		this.action = action;
		this.role = role;
		this.type = type;
		this.instance = instance;
		this.instanceOnly = instanceOnly;
	}

}
