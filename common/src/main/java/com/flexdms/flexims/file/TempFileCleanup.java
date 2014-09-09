package com.flexdms.flexims.file;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.flexdms.flexims.RunAsAdmin;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

@Dependent
public class TempFileCleanup implements Runnable {

	public static final Logger LOGGER = Logger.getLogger(TempFileCleanup.class.getCanonicalName());
	@Inject
	EntityManager em;
	@Inject
	@Any
	Instance<FileServiceI> fileServices;

	@Override
	@RunAsAdmin
	public void run() {
		LOGGER.info("trying to clean up temporary files");
		fileServices.isUnsatisfied();
		fileServices.isAmbiguous();
		for (FileServiceI fileService : fileServices) {
			LOGGER.info("file clean from file service " + fileService.getFileSchema());
			try {
				deleteTempFile(fileService);
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "error when clean up file from one file service " + fileService.getFileSchema(), e);
			}
		}
	}

	public void deleteTempFile(FileServiceI fileService) {
		List<FileInfo> fileInfos = fileService.findTempFiles();
		for (FileInfo fileInfo : fileInfos) {
			boolean removed = true;
			if (fileInfo.isAssociatedWithInstance()) {
				Class<?> entityClass = JpaHelper.getEntityClass(em, fileInfo.getInstType());
				// class is deleted if entityClass is null
				if (entityClass != null) {
					FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) em.find(entityClass, fileInfo.getLongInstId());
					// if inst is null, the inst the file is associated with is
					// deleted.
					if (inst != null) {
						FileIDs iDs = FileUtils.searchFileIDs(inst, null, null);
						if (iDs.getFileIDs().contains(fileInfo.getFileID())) {
							removed = false; // still associated with an
												// instance, do not remove.
						}
					}
				}
			}
			if (removed) {
				try {
					// problem. the ID is not
					fileService.deleteFile(fileInfo);
				} catch (Exception e) {
					LOGGER.log(Level.INFO, "error when remove one file from one file service " + fileService.getFileSchema() + ":"
							+ fileInfo.getFileID().getId(), e);
				}
			}

		}
	}
}
