package com.flexdms.flexims.accesscontrol.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.Decision;

/**
 * The only use of this class is for JPA Mapping
 * 
 * @author jason
 * 
 */
@Access(AccessType.PROPERTY)
@Embeddable
public class InstanceRolePermission implements PermissionI {

	
	long roleid;
	List<Action> actions;
	Decision decision = Decision.Allow;

	public InstanceRolePermission() {

	}

	public InstanceRolePermission(RolePermission rp) {
		super();
		roleid = rp.roleid;
		actions = rp.actions;
		decision = rp.decision;
	}

	public void fromRolePermission(RolePermission rp) {
		roleid = rp.roleid;
		actions = rp.actions;
		decision = rp.decision;
	}

	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	@Transient
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	@Column(name = "actions")
	private String getActionString() {
		if (this.actions == null || this.actions.isEmpty()) {
			return null;
		}
		return StringUtils.join(actions, ',');
	}

	@SuppressWarnings("unused")
	private void setActionString(String action) {
		if (actions == null) {
			actions = new ArrayList<Action>(INITIAL_PERMISSION_NUM);
		}
		actions.clear();
		if (action == null) {
			return;
		}
		String[] as = action.split(",");
		for (String name : as) {
			actions.add(ACLHelper.getActionByName(name));
		}
	}

	@Enumerated(EnumType.STRING)
	public Decision getDecision() {
		return decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	public RolePermission toRolePermission() {
		RolePermission rp = new RolePermission();
		rp.actions = actions;
		rp.roleid = roleid;
		rp.decision = decision;
		return rp;
	}

}
