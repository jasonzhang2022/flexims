package com.flexdms.flexims.file;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.EntityContext;
import com.flexdms.flexims.jpa.event.InstanceAction;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;

/**
 * Handle associated files when instance is inserted/modified/deleted.
 * 
 * @author jason.zhang
 * 
 */
@Dependent
public class FileMaintainer {

	public void changeFileStatus(FleximsDynamicEntityImpl de, boolean persist) {
		FileIDs fileIDs = FileUtils.searchFileIDs(de, null, null);
		if (fileIDs.getFileIDs().isEmpty()) {
			return;
		}
		FileServiceI fileService = BeanProvider.getContextualReference(FileServiceI.class, true, new FileSchemaLiteral(fileIDs.getFileIDs().get(0)
				.getSchema()));
		if (fileService == null) {
			return;
		}
		for (FileID fileID : fileIDs.getFileIDs()) {
			if (persist) {
				fileService.changeStatus(fileID, false, de.getClass().getSimpleName(), String.valueOf(de.getId()));
			} else {
				fileService.changeStatus(fileID, true, de.getClass().getSimpleName(), String.valueOf(de.getId()));
			}
		}
	}

	public void deleteItem(@Observes @InstanceAction(actionType = InstanceActionType.PreRemove) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		changeFileStatus(de, false);
	}

	public void editItem(@Observes @InstanceAction(actionType = InstanceActionType.PreUpdate) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		changeFileStatus(de, true);
	}

	public void addItem(@Observes @InstanceAction(actionType = InstanceActionType.PostPersist) EntityContext entityContext) {
		FleximsDynamicEntityImpl de = (FleximsDynamicEntityImpl) entityContext.getEntity();
		changeFileStatus(de, true);
	}
}
