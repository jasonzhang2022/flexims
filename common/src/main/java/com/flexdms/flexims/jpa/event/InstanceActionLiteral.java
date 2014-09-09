package com.flexdms.flexims.jpa.event;

import javax.enterprise.util.AnnotationLiteral;

public class InstanceActionLiteral extends AnnotationLiteral<InstanceAction> implements InstanceAction {

	private static final long serialVersionUID = 1L;
	InstanceActionType actionType;

	public InstanceActionLiteral(InstanceActionType actionType) {
		super();
		this.actionType = actionType;
	}

	@Override
	public InstanceActionType actionType() {
		return actionType;
	}

}
