package com.flexdms.flexims.files.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileSchema;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.util.Utils;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.StorageObject;

@Dependent
@FileSchema(CloudStorageFileService.FILE_SCHEMA)
public class CloudStorageFileService implements FileServiceI {
	public static final Logger LOGGER = Logger.getLogger(CloudStorageFileService.class.getCanonicalName());
	public static final int DIRECT_DOWNLOAD_SIZE = 1024 * 1024 * 2;
	public static final int NO_OBJECT_CODE = 404;
	public static final long MAX_RESULT = 1000L;
	public static final String FILE_SCHEMA = "GC";
	public static final String FILENAME = "fxFileName";
	public static final String INSTTYPE = "fxInstType";
	public static final String INSTID = "fxInstID";
	public static final String TEMP = "fxTemp";

	public static final String TEMP_PREFIX = "fxtemp/";
	public static boolean isAppEngine = false;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private HttpTransport httpTransport;

	private Storage client;

	String bucketName;

	@PostConstruct
	public void init() throws Exception {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			InputStream inputStream = DriveFileService.class.getClassLoader().getResourceAsStream("META-INF/serviceaccount_privatekey.p12");
			if (inputStream == null) {
				java.io.File file = new java.io.File(Configs.getConfig("google.api.serviceaccount.p12"));
				if (!file.exists()) {
					LOGGER.warning("Can not find service account private key file. Drive service is not available.");
				}
				inputStream = new FileInputStream(file);
			}

			PrivateKey privatekey = SecurityUtils.loadPrivateKeyFromKeyStore(SecurityUtils.getPkcs12KeyStore(), inputStream, "notasecret",
					"privatekey", "notasecret");
			GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(JSON_FACTORY)
					.setServiceAccountId(Configs.getConfig("google.api.serviceaccount.email"))
					.setServiceAccountScopes(Collections.singleton(StorageScopes.DEVSTORAGE_FULL_CONTROL)).setServiceAccountPrivateKey(privatekey)
					.build();

			client = new Storage.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(Configs.getConfig("google.api.project.name"))
					.build();
			bucketName = Configs.getConfig("google.cloud.storage.bucket");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public FileInfo saveFile(InputStream files, FileInfo fileInfo) {
		StorageObject objectMetadata = new StorageObject();

		String uuidString = UUID.randomUUID().toString();
		String uuid = fileInfo.isTemp() ? TEMP_PREFIX + uuidString : uuidString;

		objectMetadata.setName(uuid);
		objectMetadata.setContentType(fileInfo.getMimeType());

		Map<String, String> metaMap = new HashMap<>();

		metaMap.put(FILENAME, fileInfo.getName());
		if (fileInfo.getInstType() != null && fileInfo.getInstId() != null) {
			metaMap.put(INSTTYPE, fileInfo.getInstType());
			metaMap.put(INSTID, fileInfo.getInstId());
		}
		metaMap.put(TEMP, fileInfo.isTemp() ? "true" : "false");

		if (fileInfo.getAttributes() != null) {
			for (Map.Entry<String, String> entry : fileInfo.getAttributes().entrySet()) {
				metaMap.put(entry.getKey(), entry.getValue());
			}
		}
		objectMetadata.setMetadata(metaMap);
		objectMetadata.setContentDisposition("attachment");
		objectMetadata.setSize(new BigInteger(String.valueOf(fileInfo.getSize())));

		InputStreamContent mediaContent = new InputStreamContent(fileInfo.getMimeType(), files);
		// Knowing the stream length allows server-side optimization, and
		// client-side progress
		// reporting with a MediaHttpUploaderProgressListener.
		mediaContent.setLength(fileInfo.getSize());
		try {
			Storage.Objects.Insert insertObject = client.objects().insert(bucketName, objectMetadata, mediaContent);
			if (mediaContent.getLength() > 0 && mediaContent.getLength() <= DIRECT_DOWNLOAD_SIZE) {
				insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
			}

			insertObject.execute();

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		fileInfo.setFileID(new FileID(FILE_SCHEMA, uuidString));
		// update meta with name,
		return fileInfo;

	}

	public StorageObject getFile(FileID fileID) {

		try {
			Storage.Objects.Get getObject = client.objects().get(bucketName, fileID.getId());
			StorageObject objectMetadata = getObject.execute();
			return objectMetadata;
		} catch (GoogleJsonResponseException e) {
			if (e.getStatusCode() == NO_OBJECT_CODE) {
				try {
					return client.objects().get(bucketName, TEMP_PREFIX + fileID.getId()).execute();
				} catch (IOException e1) {
					throw new RuntimeException(e);
				}
			}
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public FileInfo storageObjecToFileInfo(StorageObject object, boolean trueName) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setSize(object.getSize().longValue());
		fileInfo.setMimeType(object.getContentType());
		fileInfo.setName(object.getMetadata().get(FILENAME));
		String instType = object.getMetadata().get(INSTTYPE);
		if (instType != null) {
			fileInfo.setInstType(instType);
			fileInfo.setInstId(object.getMetadata().get(INSTID));
		}
		fileInfo.setTemp(Utils.stringToBoolean(object.getMetadata().get(TEMP), false));

		FileID fileID = new FileID();
		fileID.setSchema(FILE_SCHEMA);
		if (trueName) {
			fileID.setId(object.getName());
		} else {
			if (object.getName().startsWith(TEMP_PREFIX)) {
				fileID.setId(object.getName().substring(TEMP_PREFIX.length()));
			} else {
				fileID.setId(object.getName());
			}
		}
		fileInfo.setFileID(fileID);
		return fileInfo;
	}

	@Override
	public FileInfo getFileInfo(FileID fileID) {
		StorageObject object = getFile(fileID);
		return storageObjecToFileInfo(object, false);
	}

	@Override
	public InputStream getContent(FileID fileID) {
		Storage.Objects.Get getObject;
		try {
			try {
				getObject = client.objects().get(bucketName, fileID.getId());
				getObject.execute();
			} catch (GoogleJsonResponseException e) {
				if (e.getStatusCode() == NO_OBJECT_CODE) {
					try {
						getObject = client.objects().get(bucketName, TEMP_PREFIX + fileID.getId());
						getObject.execute();
					} catch (IOException e1) {
						throw new RuntimeException(e);
					}
				} else {
					throw new RuntimeException(e);
				}

			}
			getObject.getMediaHttpDownloader().setDirectDownloadEnabled(!isAppEngine);
			return getObject.executeMediaAsInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void changeStatus(FileID fileID, boolean temp, String instType, String instId) {

		try {
			StorageObject objectMetadata = getFile(fileID);

			String newName = objectMetadata.getName();
			if (temp) {
				if (!newName.startsWith(TEMP_PREFIX)) {
					newName = TEMP_PREFIX + newName;
				}
			} else {
				if (newName.startsWith(TEMP_PREFIX)) {
					newName = newName.substring(TEMP_PREFIX.length());
				}
			}
			if (!newName.equals(objectMetadata.getName())) {
				client.objects().copy(bucketName, objectMetadata.getName(), bucketName, newName, null).execute();
				client.objects().delete(bucketName, objectMetadata.getName()).execute();
			}
			StorageObject patchMetaObject = new StorageObject();
			Map<String, String> metaMap = new HashMap<>();
			// metaMap.putAll(objectMetadata.getMetadata());
			if (instType != null && instId != null) {
				metaMap.put(INSTTYPE, instType);
				metaMap.put(INSTID, instId);
			} else {
				metaMap.put(INSTTYPE, null);
				metaMap.put(INSTID, null);
			}
			metaMap.put(TEMP, temp ? "true" : "false");
			patchMetaObject.setMetadata(metaMap);

			client.objects().patch(bucketName, newName, patchMetaObject).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void deleteFile(FileInfo fileInfo) {
		String fileID = fileInfo.getFileID().getId();
		if (fileInfo.isTemp()) {
			fileID = TEMP_PREFIX + fileID;
		}
		try {
			client.objects().delete(bucketName, fileID).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public List<FileInfo> findTempFiles() {
		List<FileInfo> fileInfos = new LinkedList<FileInfo>();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, FileServiceI.TEMPORARY_HOURS);
		long twoHoursAgo = calendar.getTimeInMillis();
		try {
			Storage.Objects.List list = client.objects().list(bucketName).setPrefix(TEMP_PREFIX).setMaxResults(MAX_RESULT);
			com.google.api.services.storage.model.Objects objects;
			do {
				objects = list.execute();
				if (objects.getItems() == null) {
					return fileInfos;
				}
				for (StorageObject object : objects.getItems()) {
					if (object.getUpdated().getValue() < twoHoursAgo) {
						fileInfos.add(storageObjecToFileInfo(object, false));
					}
				}
				list.setPageToken(objects.getNextPageToken());
			} while (null != objects.getNextPageToken());

			return fileInfos;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String getFileSchema() {
		return FILE_SCHEMA;
	}

}
