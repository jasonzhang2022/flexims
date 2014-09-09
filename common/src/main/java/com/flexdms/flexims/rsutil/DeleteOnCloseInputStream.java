package com.flexdms.flexims.rsutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.input.AutoCloseInputStream;

public class DeleteOnCloseInputStream extends AutoCloseInputStream {

	Path file;

	public DeleteOnCloseInputStream(Path path) throws IOException {
		super(Files.newInputStream(path));
		this.file = path;
	}

	@Override
	public void close() throws IOException {
		super.close();
		Files.deleteIfExists(file);
	}

}
