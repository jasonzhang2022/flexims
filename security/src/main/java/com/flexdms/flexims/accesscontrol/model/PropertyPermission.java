package com.flexdms.flexims.accesscontrol.model;

import java.util.Collection;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;

/**
 * Perperty Permission: see {@link com.flexdms.flexims.accesscontrol.PermissionChecker} for definition
 * @author jason.zhang
 *
 */
@Access(AccessType.PROPERTY)
@DiscriminatorValue(value = "P")
@Entity
public class PropertyPermission extends Permission {
	private static final long serialVersionUID = 1L;
	String propName;

	public String getPropName() {
		return propName;
	}

	public void setPropName(String propName) {
		this.propName = propName;
	}

	@Override
	public boolean isApplyForUser(FxRole role, FleximsDynamicEntityImpl i) {

		// this is an instance-specific permission
		if (i == null) {
			return false;
		}

		Object prop = i.get(propName);
		if (prop == null) {
			// data integrirty issue
			return false;
		}

		Class<?> proptype = JpaMetamodelHelper.getAttributeType(JpaMetamodelHelper.getAttribute(this.typeACL.getTypeid(), propName));
		if (!proptype.getSimpleName().equals(FxUser.TYPE_NAME) && !proptype.getSimpleName().equals(FxRole.TYPE_NAME)) {
			return false;
		}
		if (prop instanceof Collection<?>) {
			for (Object o : (Collection<?>) prop) {
				FxRole role2 = new FxRole((FleximsDynamicEntityImpl) o);
				if (role.isIncludedBy(role2)) {
					return true;
				}
			}
		} else {
			FxRole role2 = new FxRole((FleximsDynamicEntityImpl) prop);
			if (role.isIncludedBy(role2)) {
				return true;
			}

		}

		return false;
	}

}
