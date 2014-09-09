package com.flexdms.flexims.accesscontrol;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.flexdms.flexims.accesscontrol.action.CreateAction;
import com.flexdms.flexims.accesscontrol.action.DeleteAction;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.EntityContext;
import com.flexdms.flexims.jpa.event.InstanceAction;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;

@RequestScoped
public class InstanceAuthorizer {

	@Inject
	RoleContext roleContext;
	@Inject
	PermissionChecker permissionChecker;
	
	
	public void checkPermission(FleximsDynamicEntityImpl entity, Action action) {
		PermissionResult pr = permissionChecker.checkPermission(action, roleContext.getRoles(), entity.getClass().getSimpleName(), entity, false);
		if (pr == PermissionResult.Allow) {
			return;
		}
		if (pr == PermissionResult.Deny) {
			throw new AuthorizedException(InstanceActionType.PostLoad, entity);
		}
		if (!permissionChecker.defaultActionPermission(action)) {
			throw new AuthorizedException(InstanceActionType.Query, entity);
		}
	}
	

	public void checkPostLoad(@Observes @InstanceAction(actionType = InstanceActionType.PostLoad) EntityContext ctx) {
		Action action = ACLHelper.getActionByName(ReadAction.NAME);
		checkPermission((FleximsDynamicEntityImpl) ctx.getEntity(), action);
	}

	public void checkPreUpdate(@Observes @InstanceAction(actionType = InstanceActionType.PreUpdate) EntityContext ctx) {
		Action action = ACLHelper.getActionByName(EditAction.NAME);
		checkPermission((FleximsDynamicEntityImpl) ctx.getEntity(), action);
	}

	public void checkPreRemove(@Observes @InstanceAction(actionType = InstanceActionType.PreRemove) EntityContext ctx) {
		Action action = ACLHelper.getActionByName(DeleteAction.NAME);
		checkPermission((FleximsDynamicEntityImpl) ctx.getEntity(), action);
	}

	public void checkPrePersist(@Observes @InstanceAction(actionType = InstanceActionType.PrePersist) EntityContext ctx) {
		Action action = ACLHelper.getActionByName(CreateAction.NAME);
		checkPermission((FleximsDynamicEntityImpl) ctx.getEntity(), action);
	}

}
