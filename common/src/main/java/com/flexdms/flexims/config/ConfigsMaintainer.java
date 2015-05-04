package com.flexdms.flexims.config;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.EntityContext;
import com.flexdms.flexims.jpa.event.InstanceAction;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;

/**
 * Synchronize cache configuration if configuration is modified.
 * 
 * @author jason.zhang
 * 
 */
@Dependent
public class ConfigsMaintainer {

	public boolean isConfig(FleximsDynamicEntityImpl de) {
		return de.getClass().getSimpleName().equals(ConfigItem.TYPE_NAME);
	}

	public void deleteItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostRemove) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isConfig(de)) {
			return;
		}
		if (Configs.getItems() != null) {
			Configs.getItems().put((String) de.get(ConfigItem.PROP_NAME_PROP_NAME), new ConfigItem(de));
		}

	}

	public void editItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostUpdate) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isConfig(de)) {
			return;
		}
		if (Configs.getItems() != null) {
			Configs.getItems().put((String) de.get(ConfigItem.PROP_NAME_PROP_NAME), new ConfigItem(de));
		}

	}

	public void addItem(
			@Observes(during = TransactionPhase.AFTER_SUCCESS) @InstanceAction(actionType = InstanceActionType.PostPersist) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		if (!isConfig(de)) {
			return;
		}
		if (Configs.getItems() != null) {
			Configs.getItems().put((String) de.get(ConfigItem.PROP_NAME_PROP_NAME), new ConfigItem(de));
		}

	}
}
