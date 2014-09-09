package com.flexdms.flexims.files.google;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import mockit.Mock;
import mockit.MockUp;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileServiceI;

public class CloudDriveFileServiceTest {

	public static class ConfigMock extends MockUp<Configs> {

		@Mock
		public String getConfig(String name) {
			return properties.getProperty(name);
		}

	}

	static Properties properties;

	@BeforeClass
	public static void setLog() {
		final InputStream inputStream = CloudDriveFileServiceTest.class.getClassLoader().getResourceAsStream("logging.properties");
		try {
			LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			Logger.getAnonymousLogger().severe("Could not load default logging.properties file");
			Logger.getAnonymousLogger().severe(e.getMessage());
		}
		properties = new Properties();
		try {
			properties.load(CloudStorageFileService.class.getClassLoader().getResourceAsStream("META-INF/test.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		new ConfigMock();
	}

	public void testFileServiceFlow(FileServiceI fileService) throws Exception {
		List<FileInfo> temps0 = fileService.findTempFiles();
		String test = "this is a test";
		byte[] bytes = test.getBytes();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

		FileInfo fileInfo = new FileInfo();
		fileInfo.setName("test.txt");
		fileInfo.setSize(bytes.length);
		fileInfo.setTemp(true);
		fileInfo.setMimeType("text/plain");

		// save
		fileService.saveFile(inputStream, fileInfo);

		// read content
		IOUtils.copy(fileService.getContent(fileInfo.getFileID()), System.out);

		FileInfo fileInfo1 = fileService.getFileInfo(fileInfo.getFileID());
		assertTrue(fileInfo1.isTemp());

		// change status permanent
		fileService.changeStatus(fileInfo.getFileID(), false, "test", "533");
		FileInfo fileInfo2 = fileService.getFileInfo(fileInfo.getFileID());
		assertFalse(fileInfo2.isTemp());

		// change status: temporary
		fileService.changeStatus(fileInfo.getFileID(), true, "test", "534");
		assertTrue(fileService.getFileInfo(fileInfo.getFileID()).isTemp());

		List<FileInfo> temps = fileService.findTempFiles();
		// temp is modified in last two hours and should not be retuend.
		assertTrue(temps.size() == temps0.size());

		fileService.deleteFile(fileInfo);
	}

	@Test
	public void testCloud() throws Exception {
		CloudStorageFileService fileService = new CloudStorageFileService();
		fileService.init();
		testFileServiceFlow(fileService);

	}

	@Test
	public void testDrive() throws Exception {
		DriveFileService fileService = new DriveFileService();
		fileService.init();
		testFileServiceFlow(fileService);

	}
}
