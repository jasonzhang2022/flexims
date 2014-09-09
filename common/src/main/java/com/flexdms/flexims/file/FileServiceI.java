package com.flexdms.flexims.file;

import java.io.InputStream;
import java.util.List;

public interface FileServiceI {

	int TEMPORARY_HOURS = -2;

	/**
	 * @return File Schema for this file service
	 */
	String getFileSchema();

	/**
	 * save file.
	 * 
	 * @param files
	 *            file content
	 * @param fileInfo
	 *            detailed file information such as name, size, mime type.
	 * @return
	 */
	FileInfo saveFile(InputStream files, FileInfo fileInfo);

	/**
	 * 
	 * @param fileID
	 * @return file information
	 */
	FileInfo getFileInfo(FileID fileID);

	/**
	 * retrieve file content.
	 * 
	 * @param fileID
	 * @return
	 */
	InputStream getContent(FileID fileID);

	/**
	 * associate file with instance
	 * 
	 * @param fileID
	 * @param temp
	 *            is the file temporary file
	 * @param instType
	 *            instance type
	 * @param instId
	 *            instance ID
	 */
	void changeStatus(FileID fileID, boolean temp, String instType, String instId);

	/**
	 * Delete file permanently
	 * 
	 * @param fileInfo
	 */
	void deleteFile(FileInfo fileInfo);

	/**
	 * return the temporary files that are not modified in last 2 hours.
	 * Returned list does not need being exhaustive.
	 * 
	 * @return
	 */
	List<FileInfo> findTempFiles();

}
