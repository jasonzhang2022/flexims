package com.flexdms.flexims.files.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.StorageClass;
import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileSchema;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.util.Utils;

@ApplicationScoped
@FileSchema(S3FileService.FILE_SCHEMA)
public class S3FileService implements FileServiceI {

	public static final int MAX_RESULT = 1000;
	public static final String FILE_SCHEMA = "s3";
	// s3 meta is case-insensitive, all lower case
	public static final String FILENAME = "fxfilefame";
	public static final String INSTTYPE = "fxtnsttype";
	public static final String INSTID = "fxtnstid";
	public static final String TEMP = "fxtemp";

	public static final String TEMP_PREFIX = "fxtemp/";

	String bucket = null;
	AmazonS3 s3 = null;

	boolean useReducedRedunacy = true;

	@PostConstruct
	public void init() throws IOException {

		String awss3props = Configs.getConfig("aws.s3.access.propertyfile");
		if (awss3props != null) {
			s3 = new AmazonS3Client(new PropertiesCredentials(new File(awss3props)));
		} else {
			s3 = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
		}
		String region = Configs.getConfig("aws.s3.region");

		if (region != null) {
			s3.setRegion(RegionUtils.getRegion(region));
		} else {
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			s3.setRegion(usWest2);
		}
		bucket = Configs.getConfig("aws.s3.bucket");
		useReducedRedunacy = Configs.getConfigAsBoolean("aws.s3.reducedRedunacy", true);
	}

	@Override
	public FileInfo saveFile(InputStream files, FileInfo fileInfo) {
		String uuidString = UUID.randomUUID().toString();
		String uuid = fileInfo.isTemp() ? TEMP_PREFIX + uuidString : uuidString;

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentLength(fileInfo.getSize());
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
		objectMetadata.setUserMetadata(metaMap);

		PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, uuid, files, objectMetadata);
		if (useReducedRedunacy) {
			putObjectRequest.setStorageClass(StorageClass.ReducedRedundancy);
		} else {
			putObjectRequest.setStorageClass(StorageClass.Standard);
		}

		s3.putObject(putObjectRequest);
		fileInfo.setFileID(new FileID(FILE_SCHEMA, uuidString));
		return fileInfo;
	}

	public FileInfo s3OjecttoFileInfo(ObjectMetadata objectMetadata, String key) {

		FileInfo fileInfo = new FileInfo();
		fileInfo.setSize(objectMetadata.getContentLength());
		fileInfo.setMimeType(objectMetadata.getContentType());
		fileInfo.setName(objectMetadata.getUserMetadata().get(FILENAME));
		String instType = objectMetadata.getUserMetadata().get(INSTTYPE);
		if (instType != null) {
			fileInfo.setInstType(instType);
			fileInfo.setInstId(objectMetadata.getUserMetadata().get(INSTID));
		}
		fileInfo.setTemp(Utils.stringToBoolean(objectMetadata.getUserMetadata().get(TEMP), false));

		FileID fileID = new FileID();
		fileID.setSchema(FILE_SCHEMA);
		fileID.setId(key);
		if (key.startsWith(TEMP_PREFIX)) {
			fileID.setId(key.substring(TEMP_PREFIX.length()));
		}
		fileInfo.setFileID(fileID);
		return fileInfo;
	}

	@Override
	public FileInfo getFileInfo(FileID fileID) {
		ObjectMetadata objectMetadata;
		try {

			objectMetadata = s3.getObjectMetadata(new GetObjectMetadataRequest(bucket, fileID.getId()));
		} catch (Exception e) {

			try {
				objectMetadata = s3.getObjectMetadata(new GetObjectMetadataRequest(bucket, TEMP_PREFIX + fileID.getId()));
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}

		}

		return s3OjecttoFileInfo(objectMetadata, fileID.getId());

	}

	@Override
	public InputStream getContent(FileID fileID) {
		S3Object object;
		try {

			object = s3.getObject(new GetObjectRequest(bucket, fileID.getId()));
		} catch (Exception e) {

			try {
				object = s3.getObject(new GetObjectRequest(bucket, TEMP_PREFIX + fileID.getId()));
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}

		}
		return object.getObjectContent();
	}

	@Override
	public void changeStatus(FileID fileID, boolean temp, String instType, String instId) {

		String key = null;
		ObjectMetadata objectMetadata;
		try {

			objectMetadata = s3.getObjectMetadata(new GetObjectMetadataRequest(bucket, fileID.getId()));
			key = fileID.getId();
		} catch (Exception e) {

			try {
				objectMetadata = s3.getObjectMetadata(new GetObjectMetadataRequest(bucket, TEMP_PREFIX + fileID.getId()));
				key = TEMP_PREFIX + fileID.getId();
			} catch (Exception e1) {
				throw new RuntimeException(e);
			}

		}
		try {

			String newName = key;
			if (temp) {
				if (!newName.startsWith(TEMP_PREFIX)) {
					newName = TEMP_PREFIX + newName;
				}
			} else {
				if (newName.startsWith(TEMP_PREFIX)) {
					newName = newName.substring(TEMP_PREFIX.length());
				}
			}

			ObjectMetadata patchMetadata = new ObjectMetadata();
			patchMetadata.setContentLength(objectMetadata.getContentLength());
			patchMetadata.setContentType(objectMetadata.getContentType());
			patchMetadata.setUserMetadata(objectMetadata.getUserMetadata());

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
			patchMetadata.getUserMetadata().putAll(metaMap);
			CopyObjectRequest copyObjectRequest = new CopyObjectRequest(bucket, key, bucket, newName);
			copyObjectRequest.setNewObjectMetadata(patchMetadata);
			if (useReducedRedunacy) {
				copyObjectRequest.setStorageClass(StorageClass.ReducedRedundancy);
			} else {
				copyObjectRequest.setStorageClass(StorageClass.Standard);
			}
			s3.copyObject(copyObjectRequest);
			if (!newName.equals(key)) {
				s3.deleteObject(bucket, key);
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public void deleteFile(FileInfo fileInfo) {
		String fileID = fileInfo.getFileID().getId();
		if (fileInfo.isTemp()) {
			fileID = TEMP_PREFIX + fileID;
		}
		s3.deleteObject(bucket, fileID);

	}

	@Override
	public List<FileInfo> findTempFiles() {
		List<FileInfo> fileInfos = new LinkedList<FileInfo>();

		ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucket).withPrefix(TEMP_PREFIX).withMaxKeys(MAX_RESULT);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, FileServiceI.TEMPORARY_HOURS);
		long twoHoursAgo = calendar.getTimeInMillis();

		ObjectListing objectListing;

		do {
			objectListing = s3.listObjects(listObjectsRequest);
			for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
				if (objectSummary.getLastModified().getTime() < twoHoursAgo) {
					ObjectMetadata object = s3.getObjectMetadata(bucket, objectSummary.getKey());
					fileInfos.add(s3OjecttoFileInfo(object, objectSummary.getKey()));
				}
			}
			listObjectsRequest.setMarker(objectListing.getNextMarker());
		} while (objectListing.isTruncated());

		return fileInfos;
	}

	public String getFileSchema() {
		return FILE_SCHEMA;
	}
}
