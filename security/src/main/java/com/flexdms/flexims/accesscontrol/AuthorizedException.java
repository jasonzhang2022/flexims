package com.flexdms.flexims.accesscontrol;

import javax.ws.rs.core.Response;

import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;
import com.flexdms.flexims.rsutil.HttpCodeException;

public class AuthorizedException extends HttpCodeException {

	private static final long serialVersionUID = 1L;
	public static final int APP_CODE = 1000;
	InstanceActionType type;
	Object instance;

	public AuthorizedException() {
		super(Response.Status.FORBIDDEN.getStatusCode(), "Access is Denied");
		setAppCode(APP_CODE);
	}

	public AuthorizedException(InstanceActionType type, Object instance) {
		this();
		this.type = type;
		this.instance = instance;
	}

}
