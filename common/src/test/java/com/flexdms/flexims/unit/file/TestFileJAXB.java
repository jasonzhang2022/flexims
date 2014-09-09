package com.flexdms.flexims.unit.file;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileIDs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileInfos;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;

/**
 * Test File Marshal
 * 
 * @author jason.zhang
 * 
 */
public class TestFileJAXB {

	@Test
	public void testFileInfos() throws JAXBException {
		FileInfos infos = new FileInfos();
		FileInfo info = new FileInfo();
		info.setFileID(new FileID("DBFILE", "1"));
		info.setName("test1");
		infos.getFileInfos().add(info);

		info = new FileInfo();
		info.setFileID(new FileID("DBFILE", "1"));
		info.setName("test1");
		infos.getFileInfos().add(info);

		javax.xml.bind.JAXBContext jaxbContext = JaxbHelper.createMoxyJaxbContext(FileInfos.class, FileInfo.class, FileID.class, FileIDs.class);

		Marshaller marshaller = JaxbHelper.jsonMarshaller(jaxbContext);
		marshaller.marshal(infos, System.out);

		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(infos, System.out);
	}

	@Test
	public void testFileIds() throws JAXBException {
		FileIDs fileIDs = new FileIDs();
		fileIDs.getFileIDs().add(new FileID("DBFILE", "1"));
		fileIDs.getFileIDs().add(new FileID("DBFILE", "2"));

		javax.xml.bind.JAXBContext jaxbContext = JaxbHelper.createMoxyJaxbContext(FileInfos.class, FileInfo.class, FileID.class, FileIDs.class);

		Marshaller marshaller = JaxbHelper.jsonMarshaller(jaxbContext);
		marshaller.marshal(fileIDs, System.out);

		marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(fileIDs, System.out);
	}

}
