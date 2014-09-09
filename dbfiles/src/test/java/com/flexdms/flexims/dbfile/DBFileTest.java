package com.flexdms.flexims.dbfile;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileServiceI;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.RequestScopeRule;

public class DBFileTest {

	@ClassRule
	public static CDIContainerRule cdiContainerRule = new CDIContainerRule();
	@ClassRule
	public static JPA_JAXB_EmbeddedDerby_Rule clientSetupRule = new JPA_JAXB_EmbeddedDerby_Rule();

	@Rule
	public EntityManagerRule eManagerRule = new EntityManagerRule();
	@Rule
	public RequestScopeRule requestScopeRule = new RequestScopeRule();

	public EntityManager em;

	@Before
	public void emsetup() throws JAXBException, SQLException {
		em = eManagerRule.em;
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
		em.getTransaction().begin();
		fileService.saveFile(inputStream, fileInfo);
		em.getTransaction().commit();
		// read content
		IOUtils.copy(fileService.getContent(fileInfo.getFileID()), System.out);

		FileInfo fileInfo1 = fileService.getFileInfo(fileInfo.getFileID());
		assertTrue(fileInfo1.isTemp());

		// change status permanent
		em.getTransaction().begin();
		fileService.changeStatus(fileInfo.getFileID(), false, "test", "533");
		em.getTransaction().commit();
		FileInfo fileInfo2 = fileService.getFileInfo(fileInfo.getFileID());
		assertFalse(fileInfo2.isTemp());

		// change status: temporary
		em.getTransaction().begin();
		fileService.changeStatus(fileInfo.getFileID(), true, "test", "534");
		em.getTransaction().commit();
		assertTrue(fileService.getFileInfo(fileInfo.getFileID()).isTemp());

		List<FileInfo> temps = fileService.findTempFiles();
		// temp is modified in last two hours and should not be retuend.
		assertTrue(temps.size() == temps0.size());
		em.getTransaction().begin();
		fileService.deleteFile(fileInfo);
		em.getTransaction().commit();
	}

	@Test
	public void testDBFileFlow() throws Exception {
		DBFileService fileService = new DBFileService();
		fileService.em = em;
		testFileServiceFlow(fileService);

	}

}
