package com.flexdms.flexims.accesscontrol;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import com.flexdms.flexims.accesscontrol.action.AllAction;
import com.flexdms.flexims.accesscontrol.action.GrantAction;
import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.InstanceACL;
import com.flexdms.flexims.accesscontrol.model.InstanceRolePermission;
import com.flexdms.flexims.accesscontrol.model.PropertyPermission;
import com.flexdms.flexims.accesscontrol.model.RolePermission;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.config.ConfigItem;
import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.RoleUtils;

/**
 * Enforce the Permission to access instance
 * 
 * @author jason.zhang
 * 
 *         <h2>Rule Definitions</h2>
 *         <p>
 *         <b>Container Rule</b>: An instance follows the permission defined in
 *         its containers. Its
 * 
 *         containers are one or more related entities. E.g. OrderItem follows
 *         the Permisison in Order it is associated with.
 *         </p>
 * 
 *         <p>
 *         <b>Static Rule</b>: Static rule defined in type or instance
 *         </p>
 * 
 *         <p>
 *         <b>Property Rule</b>: fetch the roles from instance's property, and
 *         judge the permission defined for this property.
 * 
 *         The property has to be FxRole type.
 * 
 *         E.g: a project has a Project Manager. We could define permission for
 *         project->project Manager.
 *         </p>
 * 
 *         <div> <h2>
 *         Access Control Flow</h2>
 *
 *         <p>
 *         Step is performed one by one until permission is granted or denied
 *         explicitly.
 *         </p>
 *
 *         <ol>
 *         <li>Rule 1:Admin shortcut. If current logined user is in <i>admin</i>
 *         role, grant permission</li>
 *
 *         <li>Rule 2:Instance static rule. Fetch Instance-specifc access
 *         control rule, If there is any, go through the rule to see whether
 *         current logined user is granted or denied permission or not</li>
 *
 *         <li>Rule 3: Property Rule. Check <strong>Property Rule</strong>
 *         defined in current type. If there is any, fetch property's value from
 *         the instance, and check the rule.</li>
 *
 *         <li>Rule 4: Inherited Property Rule. Check <strong>Property
 *         Rule</strong> defined in parent type if there is any. If there is any
 *         such rule, fetch property's value from the instance and check the
 *         rule</li>
 *
 *         <li>Rule 5: Container Rule. Check <strong>Container Rule</strong>
 *         defined in current type</li>
 *
 *         <li>Rule 6: Inherited Container Rule. Check <strong>Container
 *         Rule</strong> defined in parent type</li>
 *
 *         <li>Rule 7: Type Static Rule. Check <strong>Static Rule</strong>
 *         defined in current type</li>
 *
 *         <li>Rule 8: Inherited Type Static Rule. Check <strong>Static Rule
 *         </strong> define in parent type</li>
 *
 *         <li>Rule 9: If permission can not be decided when it comes to this
 *         step, check configuration entries in
 *         <i>fx.acl.defaultpermission.ACTION</i> domain. If there is no such
 *         configuration entry, use some reasonable default.</li>
 *
 *         </ol>
 *         </div>
 */

@ApplicationScoped
public class PermissionChecker implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Logger LOGGER = Logger.getLogger(PermissionChecker.class.getName());
	public static final String ACL_CONFIG_DOMAIN = "fx.acl.defaultpermission";

	@Inject
	@Any
	Event<ActionContext> actionEvent;
	@Inject
	InstanceACLCache aclsCache;

	public PermissionChecker() {
		// logger.info("acl is constructed");
	}

	public boolean defaultActionPermission(Action action) {
		if (action.getName().equals(AllAction.NAME)) {
			return false;
		}
		if (action.getName().equals(GrantAction.NAME)) {
			return false;
		}
		return true;
	}

	public boolean hasPermission(Action action,  List<? extends FxRole> roles, String type, FleximsDynamicEntityImpl i) {
		PermissionResult pr = checkPermission(action, roles, type, i, false);
		if (pr == PermissionResult.Allow) {
			return true;
		} else if (pr == PermissionResult.Deny) {
			return false;
		}
		return defaultActionPermission(action);
	}

	public PermissionResult getConfigedPermissionResult(Action action) {
		ConfigItem configItem = Configs.getConfigItem(ACL_CONFIG_DOMAIN + "." + action.getName());
		if (configItem == null) {
			return PermissionResult.Undecided;
		}
		if (configItem.getBooleanValue()) {
			return PermissionResult.Allow;
		}
		return PermissionResult.Deny;
	}

	public PermissionResult checkPermission(Action action, List<? extends FxRole> roles, String type, FleximsDynamicEntityImpl i, boolean instanceOnly) {
		for (FxRole role : roles) {
			// rule 1: admin shortchut.
			if (role.isAdminRole()) {
				return PermissionResult.Allow;
			}
			if (role.isIncludedBy(RoleUtils.getAdminRole())) {
				return PermissionResult.Allow;
			}
		}
		// switch to Admin user when perform checking so all ACL-related facts
		// can be loaded.
		PermissionResult pr = checkInternalPermission(action, roles, type, i, instanceOnly);

		// rule 9: check configurattion.
		if (!pr.isDecisive()) {
			pr = getConfigedPermissionResult(action);
		}

		return pr;
	}

	public PermissionResult checkInternalPermission(Action action,  List<? extends FxRole> roles, String type, FleximsDynamicEntityImpl i, boolean instanceOnly) {
		for (FxRole role : roles) {
			PermissionResult pr = checkInternalPermission(action, role, type, i, instanceOnly);
			if (pr.isDecisive()) {
				return pr;
			}
		}
		return PermissionResult.Undecided;
	}

	/**
	 * Issue ActionEvent first, then invoke internal logic if no decision is
	 * made.
	 * 
	 * @param action
	 * @param realUser
	 * @param type
	 * @param i
	 * @param instanceOnly
	 * @return
	 */
	public PermissionResult checkInternalPermission(Action action, FxRole role, String type, FleximsDynamicEntityImpl i, boolean instanceOnly) {
		PermissionResult pr;

		// hook to override the whole authorization flow.
		ActionContext actx = new ActionContext(action, role, type, i, instanceOnly);
		actionEvent.fire(actx);
		pr = actx.getPermissionResult();
		if (pr.isDecisive()) {
			return pr;
		}
		if (i == null && !instanceOnly) {
			// Rule 7 and 8
			pr = defaultCheckTypePermissionLogic(type, action, role);
		} else {
			// Rule 2 to 8.
			pr = defaultCheckInstancePermissionLogic(i, action, role, instanceOnly);
		}
		return pr;
	}

	/**
	 * Internal Logic to check Instance permission. Itself does not issue
	 * ActionContext event. However, this function could invoke inherited rule
	 * or static rule in type. which could issue ActionContext event.
	 * 
	 * @param i
	 * @param action
	 * @param user
	 * @param instanceOnly
	 * @return
	 */
	public PermissionResult defaultCheckInstancePermissionLogic(FleximsDynamicEntityImpl i, Action action, FxRole role, boolean instanceOnly) {
		InstanceACL iacl = aclsCache.get(i);

		// rule 2: instance-static role
		if (iacl != null) {
			for (InstanceACE ace : iacl.getAces()) {
				InstanceRolePermission rp = ace.getInstanceRolePermission();
				PermissionResult pr5 = rp.toRolePermission().checkPermission(action, role, null);
				if (pr5.isDecisive()) {
					return pr5;
				}
			}
		}

		String selfType = i.getClass().getSimpleName();
		// rule 3 and 4: property rule.
		PermissionResult pr2 = checkPropertyPermission(i, action, role, selfType);
		if (pr2.isDecisive()) {
			return pr2;
		}

		// Rule 5 and 6: Container Rule.
		PermissionResult pr1 = checkContainerPermission(i, action, role, selfType, instanceOnly);
		if (pr1.isDecisive()) {
			return pr1;
		}

		if (!instanceOnly) {
			// rule 3: check static permission in type.
			PermissionResult pr3 = checkInternalPermission(action, role, selfType, null, false);
			if (pr3.isDecisive()) {
				return pr3;
			}
		}

		return PermissionResult.Undecided;
	}

	/**
	 * Internal logic to check type permission. Itself does not issue
	 * ActionContext event.
	 * 
	 * @param type
	 * @param action
	 * @param user
	 * @return
	 */
	public PermissionResult defaultCheckTypePermissionLogic(String type, Action action, FxRole role) {

		// rule 7: static type rule/
		TypeACL typeACL = ACLHelper.loadTypeACL(type);
		// rule 3: check static Role-based permission
		List<RolePermission> rperms = typeACL.getRolePermissions();
		for (RolePermission rp : rperms) {
			PermissionResult pr = rp.checkPermission(action, role, null);
			if (pr.isDecisive()) {
				return pr;
			}
		}

		// Rule 8: inherited static type rule.
		Class<?> sClass = JpaMetamodelHelper.getSuperType(type);
		if (sClass != null) {
			PermissionResult pr = checkInternalPermission(action, role, sClass.getSimpleName(), null, false);
			if (pr.isDecisive()) {
				return pr;
			}
		}

		return PermissionResult.Undecided;
	}

	// rule 5 and 6.
	private PermissionResult checkContainerPermission(FleximsDynamicEntityImpl i, Action action, FxRole role, String type, boolean instanceOnly) {

		// rule 5: container Rule.
		TypeACL typeACL = ACLHelper.loadTypeACL(type);
		if (typeACL != null) {
			// Rule 1: look into relation.
			for (String propName : typeACL.getAclParentTypes()) {
				Object prop = i.get(propName);
				if (prop == null) {
					// data integrity issue
					continue;
				}
				if (prop instanceof Collection<?>) {
					for (Object o : (Collection<?>) prop) {
						PermissionResult pr1 = checkInternalPermission(action, role, o.getClass().getSimpleName(), (FleximsDynamicEntityImpl) o,
								instanceOnly);
						if (pr1.isDecisive()) {
							return pr1;
						}
					}

				} else {

					PermissionResult pr1 = checkInternalPermission(action, role, prop.getClass().getSimpleName(), (FleximsDynamicEntityImpl) prop,
							instanceOnly);
					if (pr1.isDecisive()) {
						return pr1;
					}
				}
			}
		}

		// rule 6: inherited container rule.
		Class<?> sClass = JpaMetamodelHelper.getSuperType(type);
		if (sClass != null) {
			return checkContainerPermission(i, action, role, sClass.getSimpleName(), instanceOnly);
		}
		return PermissionResult.Undecided;
	}

	// Rule 3 and 4: check property rule.
	private PermissionResult checkPropertyPermission(FleximsDynamicEntityImpl i, Action action, FxRole role, String type) {
		TypeACL typeACL = ACLHelper.loadTypeACL(type);
		// Rule 3: property Rule
		for (PropertyPermission pp : typeACL.getPropPermissions()) {
			PermissionResult pr = pp.checkPermission(action, role, i);
			if (pr.isDecisive()) {
				return pr;
			}
		}
		// Rule 4: inherited property rule.
		Class<?> sClass = JpaMetamodelHelper.getSuperType(type);
		if (sClass != null) {
			return checkPropertyPermission(i, action, role, sClass.getSimpleName());
		}
		return PermissionResult.Undecided;
	}
}
