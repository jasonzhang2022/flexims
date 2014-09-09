package com.flexdms.flexims.accesscontrol;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.flexdms.flexims.accesscontrol.action.QueryAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;
import com.flexdms.flexims.query.TypedQuery;
import com.flexdms.flexims.report.rs.ReportActionContext;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.RoleUtils;

@RequestScoped
public class QueryAuthorizer {
	@Inject
	RoleContext roleContext;

	@Inject
	PermissionChecker permissionChecker;

	public void assertQueryExecution(@Observes ReportActionContext actionContext) {
		for (FxRole role : roleContext.getRoles()) {
			if (role.isAdminRole()) {
				return;
			}
			if (role.isIncludedBy(RoleUtils.getAdminRole())) {
				return;
			}
		}

		FleximsDynamicEntityImpl fxReportOrQuery = actionContext.getFxReport();

		Action action = ACLHelper.getActionByName(QueryAction.NAME);
		FleximsDynamicEntityImpl reportEntity = null;
		FleximsDynamicEntityImpl queryEntity = null;
		FleximsDynamicEntityImpl queryPermissionEntity = null;
		if (fxReportOrQuery.getClass().getSimpleName().equals(FxReportWrapper.TYPE_NAME)) {
			reportEntity = fxReportOrQuery;
			queryEntity = reportEntity.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
			if (reportEntity.getId() == 0) {
				queryPermissionEntity = queryEntity;
			} else {
				queryPermissionEntity = reportEntity;
			}
		} else {
			queryEntity = fxReportOrQuery;
			queryPermissionEntity = queryEntity;
		}

		// check query permission defined in query instance.
		for (FxRole role : roleContext.getRoles()) {
			PermissionResult pr = permissionChecker.checkInternalPermission(action, role, queryPermissionEntity.getClass().getSimpleName(),
					queryPermissionEntity, true);
			if (pr == PermissionResult.Allow) {
				return;
			}
			if (pr == PermissionResult.Deny) {
				throw new AuthorizedException(InstanceActionType.Query, queryPermissionEntity);
			}
		}

		// we not reach a decision yet, check the read permission on type
		action = ACLHelper.getActionByName(ReadAction.NAME);
		String targetType = queryEntity.get(TypedQuery.PROP_NAME_TYPE);
		for (FxRole role : roleContext.getRoles()) {
			PermissionResult pr = permissionChecker.checkInternalPermission(action, role, targetType, null, false);
			if (pr == PermissionResult.Allow) {
				return;
			}
			if (pr == PermissionResult.Deny) {
				throw new AuthorizedException(InstanceActionType.Query, queryPermissionEntity);
			}
		}

		// configured
		PermissionResult pr = permissionChecker.getConfigedPermissionResult(action);
		if (pr == PermissionResult.Allow) {
			return;
		}
		if (pr == PermissionResult.Deny) {
			throw new AuthorizedException(InstanceActionType.Query, queryPermissionEntity);
		}
		// default.
		if (!permissionChecker.defaultActionPermission(action)) {
			throw new AuthorizedException(InstanceActionType.Query, queryPermissionEntity);
		}

	}
}
