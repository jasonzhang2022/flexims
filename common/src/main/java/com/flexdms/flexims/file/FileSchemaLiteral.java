package com.flexdms.flexims.file;

import javax.enterprise.util.AnnotationLiteral;

public class FileSchemaLiteral extends AnnotationLiteral<FileSchema> implements FileSchema {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String value;

	public FileSchemaLiteral(String value) {
		super();
		this.value = value;
	}

	@Override
	public String value() {
		return value;
	}

}
