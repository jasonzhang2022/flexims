package com.flexdms.flexims.accesscontrol;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.flexdms.flexims.RunAsAdmin;
import com.flexdms.flexims.users.RoleUtils;

@RunAsAdmin
@Interceptor
public class RunAsAdminInterceptor implements Serializable {

	private static final long serialVersionUID = 1L;
	static final Logger LOGGER = Logger.getLogger(RunAsAdminInterceptor.class.getName());

	@Inject
	RoleContext roleContext;

	@AroundInvoke
	public Object check(InvocationContext ctx) throws Exception {
		if (roleContext.hasRole(RoleUtils.getAdminRole())) {
			return ctx.proceed();
		}
		roleContext.getRoles().addFirst(RoleUtils.getAdminRole());
		try {
			return ctx.proceed();
		} finally {
			roleContext.getRoles().removeFirst();
		}
	}
}
