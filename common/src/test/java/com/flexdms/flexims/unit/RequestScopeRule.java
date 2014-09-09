package com.flexdms.flexims.unit;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.rules.ExternalResource;

public class RequestScopeRule extends ExternalResource {
	protected ContextControl ctxCtrl;

	boolean startSession = false;

	public RequestScopeRule() {
		this(false);
	}

	public RequestScopeRule(boolean session) {
		this.startSession = session;
	}

	@Override
	protected void before() throws Throwable {
		ctxCtrl = BeanProvider.getContextualReference(ContextControl.class);

		if (startSession) {
			ctxCtrl.startContext(SessionScoped.class);
		}
		// and now immediately restart the context again
		ctxCtrl.startContext(RequestScoped.class);

	}

	@Override
	protected void after() {
		if (startSession) {
			ctxCtrl.stopContext(SessionScoped.class);
		}
		ctxCtrl.stopContext(RequestScoped.class);
	};

}
