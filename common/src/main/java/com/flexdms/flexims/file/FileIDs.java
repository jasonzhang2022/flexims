package com.flexdms.flexims.file;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileIDs {

	List<FileID> fileIDs = new LinkedList<FileID>();

	public List<FileID> getFileIDs() {
		return fileIDs;
	}

	public void setFileIDs(List<FileID> fileIDs) {
		this.fileIDs = fileIDs;
	}

}
