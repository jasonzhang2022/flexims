package com.flexdms.flexims.jpa.event;

public class EntityContext {

	Object entity;
	InstanceAction.InstanceActionType actionType;

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public InstanceAction.InstanceActionType getActionType() {
		return actionType;
	}

	public void setActionType(InstanceAction.InstanceActionType actionType) {
		this.actionType = actionType;
	}

}
