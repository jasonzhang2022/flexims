package com.flexdms.flexims.file;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.flexdms.flexims.util.Utils;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class FileInfo {

	String name;
	long size;

	FileID fileID;
	String instType;
	String instId;
	String mimeType;
	boolean temp;
	Map<String, String> attributes;
	boolean isDir;

	public long getLongInstId() {
		return Long.parseLong(instId);
	}

	public void setNameAndJudgeType(String fileName) {
		name = fileName;
		mimeType = Utils.getMimeType(fileName);
	}

	public void parseAndSetInstTypeAndId(String typeAndId) {
		String[] partsS = typeAndId.split(":");
		instType = partsS[0];
		instId = partsS[1];
	}

	public String getTypeAndInstId() {
		return instType + ":" + instId;
	}

	public boolean isAssociatedWithInstance() {
		return instType != null && instId != null;
	}

	// ------------------basic
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public FileID getFileID() {
		return fileID;
	}

	public void setFileID(FileID fileID) {
		this.fileID = fileID;
	}

	public String getInstType() {
		return instType;
	}

	public void setInstType(String instType) {
		this.instType = instType;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public boolean isTemp() {
		return temp;
	}

	public void setTemp(boolean temp) {
		this.temp = temp;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}

}
