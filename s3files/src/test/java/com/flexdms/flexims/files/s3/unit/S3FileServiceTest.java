package com.flexdms.flexims.files.s3.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import mockit.Mock;
import mockit.MockUp;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.record.formula.functions.If;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.sym.Name;
import com.flexdms.flexims.config.Configs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.files.s3.S3FileService;
import com.flexdms.flexims.util.Utils;

public class S3FileServiceTest {

	public static class ConfigMock extends MockUp<Configs> {

		@Mock
		public String getConfig(String name) {
			if (name.equals("aws.s3.access.propertyfile")) {
				return null;
			}
			return properties.getProperty(name);
		}

		@Mock
		public boolean getConfigAsBoolean(String name, boolean defaultValue) {

			if (name.startsWith("aws.s3.")) {
				String value = properties.getProperty(name);
				if (value != null) {
					return Utils.stringToBoolean(value, false);
				}
			}
			return defaultValue;
		}
	}

	static Properties properties;

	@BeforeClass
	public static void setLog() {
		properties = new Properties();
		try {
			properties.load(S3FileService.class.getClassLoader().getResourceAsStream("test.properties"));
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
		S3FileService fileService = new S3FileService();
		fileService.init();
		testFileServiceFlow(fileService);

	}

}
