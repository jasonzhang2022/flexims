package com.flexdms.flexims.dbfile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;

import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileSchema;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.helper.ByteArray;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.util.Utils;
@Dependent
@FileSchema(DBFileService.FILE_SCHEMA)
public class DBFileService implements FileServiceI {

	public static final int MAX_RESULT = 500;
	public static final String FILE_SCHEMA = "DBFILE";
	public static final String TYPE_NAME = "FxDbFile";

	public static final String PROP_NAME = "Name";
	public static final String PROP_SIZE = "Size";
	public static final String PROP_MIMETYPE = "MimeType";
	public static final String PROP_ATTRIBUTES = "fxExtraProp";
	public static final String PROP_TEMP = "Temp";
	public static final String PROP_CONTENT = "Content";
	public static final String PROP_INST_TYPE = "InstType";
	public static final String PROP_INST_ID = "InstId";

	// Can not inject EntityManager since EntityManager is dependent scoped.
	// we could hold a copy of stale EntityManager.
	@Inject
	EntityManager em;

	public static FleximsDynamicEntityImpl populate(FleximsDynamicEntityImpl de, FileInfo fileInfo) {
		// never populate schema and id.
		de.set(PROP_NAME, fileInfo.getName());
		if (fileInfo.getMimeType() != null) {
			de.set(PROP_MIMETYPE, fileInfo.getMimeType());
		} else {
			de.set(PROP_MIMETYPE, Utils.getMimeType(fileInfo.getName()));
		}

		de.set(PROP_SIZE, fileInfo.getSize());
		de.set(PROP_TEMP, fileInfo.isTemp());
		if (fileInfo.getInstType() != null && fileInfo.getInstId() != null) {
			de.set(PROP_INST_TYPE, fileInfo.getInstType());
			de.set(PROP_INST_ID, fileInfo.getInstId());
		}
		if (fileInfo.getAttributes() != null && !fileInfo.getAttributes().isEmpty()) {
			de.set(PROP_ATTRIBUTES, NameValueList.fromMap(fileInfo.getAttributes()));
		}
		return de;
	}

	public static FileInfo convert(FleximsDynamicEntityImpl de) {

		FileInfo fileInfo = new FileInfo();
		FileID id = new FileID(FILE_SCHEMA, String.valueOf(de.getId()));
		fileInfo.setFileID(id);

		fileInfo.setName((String) de.get(PROP_NAME));
		fileInfo.setSize((long) de.get(PROP_SIZE));
		fileInfo.setMimeType((String) de.get(PROP_MIMETYPE));
		fileInfo.setTemp((boolean) de.get(PROP_TEMP));
		fileInfo.setInstType((String) de.get(PROP_INST_TYPE));
		fileInfo.setInstId((String) de.get(PROP_INST_ID));
		NameValueList nvs = de.get(PROP_ATTRIBUTES);
		if (nvs != null) {
			fileInfo.setAttributes(nvs.toMap());
		}
		return fileInfo;
	}

	@Override
	@Transactional
	public FileInfo saveFile(InputStream files, FileInfo fileInfo) {
		FleximsDynamicEntityImpl dEntityImpl = JpaHelper.createNewEntity(em, TYPE_NAME);
		populate(dEntityImpl, fileInfo);
		try {
			dEntityImpl.set(PROP_CONTENT, new ByteArray(files));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		em.persist(dEntityImpl);
		// flush it, we need an id.
		em.flush();

		// try {
		// Connection connection=em.unwrap(Connection.class);
		// java.sql.PreparedStatement pstmt =
		// connection.prepareStatement("update fxdbfile set content=? where id=?");
		// pstmt.setBinaryStream(1, files, (int)size);
		// //pstmt.setBlob(1, files);
		// pstmt.setLong(1, dEntityImpl.getId());
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
		FileID fileID = new FileID();
		fileID.setSchema(FILE_SCHEMA);
		fileID.setLongId(dEntityImpl.getId());
		fileInfo.setFileID(fileID);

		return fileInfo;
	}

	@Override
	@Transactional
	public void changeStatus(FileID fileID, boolean temp, String instType, String instId) {
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, TYPE_NAME), fileID.getLongId());
		// em.refresh(dynamicEntityImpl);;

		dynamicEntityImpl.set(PROP_TEMP, temp);
		if (instId != null && instType != null) {
			dynamicEntityImpl.set(PROP_INST_TYPE, instType);
			dynamicEntityImpl.set(PROP_INST_ID, instId);
		}
		// UnitOfWorkImpl unitOfWork=(UnitOfWorkImpl)
		// em.unwrap(UnitOfWork.class);
		// unitOfWork.updateObject(dynamicEntityImpl);

	}

	@Override
	public FileInfo getFileInfo(FileID fileID) {
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, TYPE_NAME), fileID.getLongId());
		em.refresh(dynamicEntityImpl);
		return convert(dynamicEntityImpl);
	}

	@Override
	public InputStream getContent(FileID fileID) {
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, TYPE_NAME), fileID.getLongId());

		ByteArray byteArray = dynamicEntityImpl.get(PROP_CONTENT);
		return new ByteArrayInputStream(byteArray.getValue());
	}

	@Override
	@Transactional
	public void deleteFile(FileInfo fileInfo) {
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, TYPE_NAME), fileInfo.getFileID()
				.getLongId());
		em.remove(dynamicEntityImpl);

	}

	@Override
	public List<FileInfo> findTempFiles() {
		List<FileInfo> fileInfos = new LinkedList<FileInfo>();
		Query query = em.createQuery("Select f from FxDbFile f where f.Temp = :temp and f.fxversion < :version").setMaxResults(MAX_RESULT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, FileServiceI.TEMPORARY_HOURS);

		query.setParameter("temp", true);
		query.setParameter("version", new java.sql.Timestamp(calendar.getTimeInMillis()));
		for (Object obj : query.getResultList()) {
			FleximsDynamicEntityImpl fileEntity = (FleximsDynamicEntityImpl) obj;
			FileInfo fileInfo = DBFileService.convert(fileEntity);
			fileInfos.add(fileInfo);
		}
		return fileInfos;
	}

	public String getFileSchema() {
		return FILE_SCHEMA;
	}

}
