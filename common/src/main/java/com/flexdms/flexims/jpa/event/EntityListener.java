package com.flexdms.flexims.jpa.event;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;

public class EntityListener {

	@PrePersist
	public void prePersist(Object obj) {
		fire(obj, InstanceActionType.PrePersist);
	}

	@PostPersist
	public void postPersist(Object obj) {
		fire(obj, InstanceActionType.PostPersist);
	}

	@PostLoad
	public void postLoad(Object obj) {
		fire(obj, InstanceActionType.PostLoad);
	}

	@PreRemove
	public void preRemove(Object obj) {
		fire(obj, InstanceActionType.PreRemove);
	}

	@PostRemove
	public void postRemove(Object obj) {
		fire(obj, InstanceActionType.PostRemove);
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		fire(obj, InstanceActionType.PreUpdate);
	}

	@PostUpdate
	public void postUpdate(Object obj) {
		fire(obj, InstanceActionType.PostUpdate);
	}

	public void fire(Object obj, InstanceActionType type) {
		EntityContext ctxContext = new EntityContext();
		ctxContext.setEntity(obj);
		ctxContext.setActionType(type);
		App.fireEvent(ctxContext, new InstanceActionLiteral(type));
	}

}
