package com.flexdms.flexims.urlctrl;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.OncePerRequestFilter;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.RoleUtils;
import com.flexdms.flexims.util.SessionCtx;

public class UrlControlFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
		HttpServletRequest request2 = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		String path = request2.getRequestURI().substring(request2.getContextPath().length());
		FxRole role = (FxRole) App.getInstance(SessionCtx.class).getAttr(FxUser.SESSION_USER_KEY);
		if (role == null || forbidden(path, role)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		chain.doFilter(request2, response);

	}

	public static boolean forbidden(String path, FxRole role) {
		if (role.isIncludedBy(RoleUtils.getAdminRole())) {
			return false;
		}
		URlCtrlCache uRlCtrlCache = App.getInstance(URlCtrlCache.class);
		for (FleximsDynamicEntityImpl de : uRlCtrlCache.getURLs()) {
			Pattern pattern = (Pattern) de.getMetaAttribute(URlCtrlCache.PATTERN_KEY);
			if (!pattern.matcher(path).lookingAt()) {
				continue;
			}
			List<FleximsDynamicEntityImpl> roles = de.get(URlCtrlCache.ROLES_PROP_STRING);
			boolean inRole = false;
			for (FleximsDynamicEntityImpl r : roles) {
				FxRole r1 = new FxRole(r);
				if (role.isIncludedBy(r1)) {
					inRole = true;
					break;
				}
			}
			if (!inRole) {
				return true;
			}
		}
		return false;
	}

}
