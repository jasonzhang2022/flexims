package com.flexdms.flexims.accesscontrol.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.users.FxRole;
/**
 * Whether a Role has permission on something(TYPE and INSTANCE)
 * @author jason.zhang
 *
 */
@Access(AccessType.PROPERTY)
@DiscriminatorValue(value = "R")
@Entity
public class RolePermission extends Permission {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long roleid;

	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
	}

	@Override
	public boolean isApplyForUser(FxRole role, FleximsDynamicEntityImpl i) {
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl permissionRole = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, FxRole.TYPE_NAME), roleid);
		if (permissionRole == null) {
			// role is deleted
			return false;
		}
		return role.isIncludedBy(new FxRole(permissionRole));
	}

}
