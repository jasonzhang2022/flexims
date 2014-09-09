package com.flexdms.flexims.auth;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationListener;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.PrincipalCollection;

import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.util.SessionCtx;

public class LoginListener implements AuthenticationListener {

	@Override
	public void onSuccess(AuthenticationToken token, AuthenticationInfo info) {
		SessionCtx sCtx = BeanProvider.getContextualReference(SessionCtx.class, false);
		Object obj = info.getPrincipals().getPrimaryPrincipal();
		UserPrincipal principal = (UserPrincipal) obj;
		// remove password
		FxUser user = principal.getUser();
		sCtx.putAttr(FxUser.SESSION_USER_KEY, user);
	}

	@Override
	public void onFailure(AuthenticationToken token, AuthenticationException ae) {
		/*
		 * FacesContext is not available at this time. FacesMessage
		 * msgFacesMessage=new FacesMessage("Login failed");
		 * FacesContext.getCurrentInstance().addMessage(null, msgFacesMessage);;
		 */
	}

	@Override
	public void onLogout(PrincipalCollection principals) {
		SessionCtx sCtx = BeanProvider.getContextualReference(SessionCtx.class, false);
		sCtx.getAttrs().remove(FxUser.SESSION_USER_KEY);
	}

}
