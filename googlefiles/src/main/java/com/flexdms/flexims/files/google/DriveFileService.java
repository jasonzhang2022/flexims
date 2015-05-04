package com.flexdms.flexims.files.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.SecurityUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;

@Dependent
@FileSchema(DriveFileService.FILE_SCHEMA)
public class DriveFileService implements FileServiceI {
	public static final Logger LOGGER = Logger.getLogger(DriveFileService.class.getCanonicalName());

	public static final int DIRECT_DOWNLOAD_SIZE = 1024 * 1024 * 2;
	public static final int NO_OBJECT_CODE = 404;
	public static final int MAX_RESULT = 500;
	public static final String FILE_SCHEMA = "GD";
	public static final String INSTTYPE = "fxInstType";
	public static final String INSTID = "fxInstID";
	public static final String TEMP = "fxTemp";

	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	public static boolean isAppEngine = false;

	private Drive client;
	private HttpTransport httpTransport;
	private String topFolderId = null;

	@PostConstruct
	public void init() {
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
					.setServiceAccountScopes(Collections.singleton(DriveScopes.DRIVE)).setServiceAccountPrivateKey(privatekey).build();

			client = new Drive.Builder(httpTransport, JSON_FACTORY, null).setApplicationName(Configs.getConfig("google.api.project.name"))
					.setHttpRequestInitializer(credential).build();

			topFolderId = findByTitle(Configs.getConfig("google.drive.topfolder")).getId();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Property getProperty(String name, String value) {
		Property property = new Property();
		property.setKey(name);
		property.setValue(value);
		property.setVisibility("PRIVATE");
		return property;
	}

	@Override
	public FileInfo saveFile(InputStream files, FileInfo fileInfo) {
		File file = new File();
		file.setTitle(fileInfo.getName());
		file.setMimeType(fileInfo.getMimeType());
		ParentReference parentReference = new ParentReference();
		parentReference.setId(topFolderId);
		file.setParents(Arrays.asList(parentReference));
		List<Property> props = new ArrayList<Property>();

		if (fileInfo.getInstType() != null && fileInfo.getInstId() != null) {
			props.add(getProperty(INSTTYPE, fileInfo.getInstType()));
			props.add(getProperty(INSTID, fileInfo.getInstId()));
		}
		props.add(getProperty(TEMP, fileInfo.isTemp() ? "true" : "false"));

		if (fileInfo.getAttributes() != null) {
			for (Map.Entry<String, String> entry : fileInfo.getAttributes().entrySet()) {
				props.add(getProperty(entry.getKey(), entry.getValue()));
			}
		}
		file.setProperties(props);
		file.setFileSize(fileInfo.getSize());

		InputStreamContent mediaContent = new InputStreamContent(fileInfo.getMimeType(), files);
		// Knowing the stream length allows server-side optimization, and
		// client-side progress
		// reporting with a MediaHttpUploaderProgressListener.
		mediaContent.setLength(fileInfo.getSize());
		try {
			Files.Insert insertObject = client.files().insert(file, mediaContent);
			if (mediaContent.getLength() > 0 && mediaContent.getLength() <= DIRECT_DOWNLOAD_SIZE) {
				insertObject.getMediaHttpUploader().setDirectUploadEnabled(true);
			}

			File file1 = insertObject.execute();
			fileInfo.setFileID(new FileID(FILE_SCHEMA, file1.getId()));
			return fileInfo;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public String getPropertyValue(File file, String propName) {
		for (Property prop : file.getProperties()) {
			if (prop.getKey().equals(propName)) {
				return prop.getValue();
			}
		}
		return null;
	}

	public FileInfo fileToFileInfo(File file) {
		FileInfo fileInfo = new FileInfo();
		fileInfo.setSize(file.getFileSize());
		fileInfo.setMimeType(file.getMimeType());
		fileInfo.setName(file.getTitle());
		String instType = getPropertyValue(file, INSTTYPE);
		if (instType != null) {
			fileInfo.setInstType(instType);
			fileInfo.setInstId(getPropertyValue(file, INSTID));
		}
		fileInfo.setTemp(Utils.stringToBoolean(getPropertyValue(file, TEMP), false));

		FileID fileID = new FileID();
		fileID.setSchema(FILE_SCHEMA);
		fileID.setId(file.getId());
		fileInfo.setFileID(fileID);

		return fileInfo;
	}

	@Override
	public FileInfo getFileInfo(FileID fileID) {
		try {
			return fileToFileInfo(client.files().get(fileID.getId()).execute());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public InputStream getContent(FileID fileID) {
		File file;
		try {
			file = client.files().get(fileID.getId()).execute();
			HttpResponse resp = client.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();
			return resp.getContent();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void changeStatus(FileID fileID, boolean temp, String instType, String instId) {
		try {
			List<Property> props = new ArrayList<Property>();

			if (instType != null && instId != null) {
				props.add(getProperty(INSTTYPE, instType));
				props.add(getProperty(INSTID, instId));
			} else {
				props.add(getProperty(INSTTYPE, null));
				props.add(getProperty(INSTID, null));
			}
			props.add(getProperty(TEMP, temp ? "true" : "false"));
			File file = new File();
			file.setProperties(props);

			client.files().patch(fileID.getId(), file).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void deleteFile(FileInfo fileInfo) {
		try {
			client.files().delete(fileInfo.getFileID().getId()).execute();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public File findByTitle(String title) {
		String q = "title = 'fleximsapp' and mimeType='application/vnd.google-apps.folder'";
		try {
			Files.List list = client.files().list().setMaxResults(MAX_RESULT).setQ(q);
			List<File> files = list.execute().getItems();
			return files.get(0);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<FileInfo> findTempFiles() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, FileServiceI.TEMPORARY_HOURS);

		String dateStr = Utils.iso8601Formatter.print(calendar.getTimeInMillis());
		List<FileInfo> fileInfos = new LinkedList<FileInfo>();
		String q = "'" + topFolderId + "' in parents and properties has { key='" + TEMP
				+ "' and value='true' and visibility='PRIVATE'} and modifiedDate <'" + dateStr + "' ";
		try {
			Files.List list = client.files().list().setMaxResults(MAX_RESULT).setQ(q);

			FileList objects;
			do {
				objects = list.execute();
				for (File file : objects.getItems()) {
					fileInfos.add(fileToFileInfo(file));
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
