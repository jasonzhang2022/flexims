package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.config.ConfigItem;
import com.flexdms.flexims.dbfile.DBFileService;
import com.flexdms.flexims.file.FileID;
import com.flexdms.flexims.file.FileIDs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileInfos;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.RSMsg;

@RunWith(Arquillian.class)
@RunAsClient
public class TestFileService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		return ArchiveUtil.buildRsWebArchive();
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	boolean sameDB = true;

	public TestFileService() {
		super();
		servicePrefixString = "rs/file";
	}

	@Test
	public void testClientFileBasic() throws JAXBException {

		// upload without size
		StreamDataBodyPart part = new StreamDataBodyPart("file", new ByteArrayInputStream("This is a tese".getBytes()), "test.txt",
				MediaType.TEXT_PLAIN_TYPE);
		final FormDataMultiPart multipart = new FormDataMultiPart();
		multipart.bodyPart(part);

		JAXBContext jaxbContext = JaxbHelper.createMoxyJaxbContext(FileInfos.class, FileInfo.class, FileID.class, FileIDs.class);
		String ret = target.path("upload").path(DBFileService.FILE_SCHEMA).request()
				.post(Entity.entity(multipart, multipart.getMediaType()), String.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		FileInfo fileInfo = (FileInfo) unmarshaller.unmarshal(new StringReader(ret));

		assertNotNull(fileInfo.getFileID());
		assertTrue(fileInfo.getSize() == 14);

		// upload with size
		File file = new File("src/test/java/com/flexdms/flexims/unit/service/TestFileService.java");
		FileDataBodyPart filepart = new FileDataBodyPart("file", file);
		FormDataMultiPart multipart1 = new FormDataMultiPart();
		multipart1.bodyPart(filepart);
		ret = target.path("upload").path(DBFileService.FILE_SCHEMA).request()
				.post(Entity.entity(multipart1, multipart1.getMediaType()), String.class);
		FileInfo fileInfo1 = (FileInfo) unmarshaller.unmarshal(new StringReader(ret));
		assertNotNull(fileInfo1.getFileID());
		assertTrue(fileInfo1.getSize() == file.length());
		assertTrue(fileInfo1.isTemp());

		AppMsg msg = getJson(target.path("topermanent").path(fileInfo1.getFileID().getSchema()).path(fileInfo1.getFileID().getId()));
		assertTrue(msg.getStatuscode() == 0);

		ret = createBuilder(target.path("fileinfo").path(fileInfo1.getFileID().getSchema()).path(fileInfo1.getFileID().getId())).get(String.class);
		FileInfo temp = (FileInfo) unmarshaller.unmarshal(new StringReader(ret));
		assertFalse(temp.isTemp());

		msg = getJson(target.path("totemp").path(fileInfo1.getFileID().getSchema()).path(fileInfo1.getFileID().getId()));
		assertTrue(msg.getStatuscode() == 0);

		ret = createBuilder(target.path("fileinfo").path(fileInfo1.getFileID().getSchema()).path(fileInfo1.getFileID().getId())).get(String.class);
		temp = (FileInfo) unmarshaller.unmarshal(new StringReader(ret));
		assertTrue(temp.isTemp());

		ret = createBuilder(target.path("clientfile").path(fileInfo.getFileID().getSchema()).path(fileInfo.getFileID().getId())).get(String.class);
		assertEquals("This is a tese", ret);

		FileIDs fileIDs = new FileIDs();
		fileIDs.getFileIDs().add(fileInfo.getFileID());
		fileIDs.getFileIDs().add(fileInfo1.getFileID());
		javax.xml.bind.Marshaller marshaller = JaxbHelper.jsonMarshaller(jaxbContext);
		StringWriter writer = new StringWriter();
		marshaller.marshal(fileIDs, writer);
		String jasonIDs = writer.toString();
		ret = target.path("fileinfos").request(MediaType.APPLICATION_JSON_TYPE)
				.post(Entity.entity(jasonIDs, MediaType.APPLICATION_JSON), String.class);
		FileInfos fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertEquals(fileInfos.getFileInfos().size(), 2);

		Response response = target.path("clientfiles").request().post(Entity.entity(jasonIDs, MediaType.APPLICATION_JSON));
		assertEquals(response.getMediaType().getType(), "application");
		assertEquals(response.getMediaType().getSubtype(), "zip");

	}

	public FileInfo uploadAFile(Unmarshaller unmarshaller, String filename) throws JAXBException {
		StreamDataBodyPart part = new StreamDataBodyPart("file", new ByteArrayInputStream("This is a tese".getBytes()), filename,
				MediaType.TEXT_PLAIN_TYPE);
		final FormDataMultiPart multipart = new FormDataMultiPart();
		multipart.bodyPart(part);

		String ret = target.path("upload").path(DBFileService.FILE_SCHEMA).request()
				.post(Entity.entity(multipart, multipart.getMediaType()), String.class);
		FileInfo fileInfo = (FileInfo) unmarshaller.unmarshal(new StringReader(ret));
		return fileInfo;
	}

	public List<String> extraZipNames(Response response) throws IOException {
		assertThat(response.getMediaType().getSubtype(), equalTo("zip"));

		ZipInputStream inputStream = new ZipInputStream(response.readEntity(InputStream.class));
		ZipEntry entry = null;
		List<String> names = new ArrayList<String>(6);
		while ((entry = inputStream.getNextEntry()) != null) {
			names.add(entry.getName());
			inputStream.closeEntry();
		}
		return names;
	}

	@Test
	public void testClientFileWithInstance() throws JAXBException, IOException {
		JAXBContext jaxbContext = JaxbHelper.createMoxyJaxbContext(FileInfos.class, FileInfo.class, FileID.class, FileIDs.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");

		// upload a file
		FileInfo fileInfo1 = uploadAFile(unmarshaller, "test1.txt");
		FileInfo fileInfo2 = uploadAFile(unmarshaller, "test2.txt");
		FileInfo fileInfo3 = uploadAFile(unmarshaller, "test3.txt");
		FileInfo fileInfo4 = uploadAFile(unmarshaller, "test4.txt");
		FileInfo fileInfo5 = uploadAFile(unmarshaller, "test5.txt");
		FileInfo fileInfo6 = uploadAFile(unmarshaller, "test6.txt");

		// associate file with instance
		FleximsDynamicEntityImpl fileMain = JpaHelper.createNewEntity(em, "Filemain");
		fileMain.set("propfile", fileInfo1.getFileID().toString());
		fileMain.set("propfiles", Arrays.asList(fileInfo2.getFileID().toString(), fileInfo3.getFileID().toString()));
		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "Fileembed1");
		embed1.set("spropfile", fileInfo4.getFileID().toString());
		embed1.set("spropfiles", Arrays.asList(fileInfo5.getFileID().toString(), fileInfo6.getFileID().toString()));
		fileMain.set("singleembed", embed1);

		em.getTransaction().begin();
		em.persist(fileMain);
		em.getTransaction().commit();

		WebTarget instTarget = target.path("clientfiles").path("Filemain").path(String.valueOf(fileMain.getId()));
		// retrieve client files
		Response response = instTarget.request().get();
		List<String> names = extraZipNames(response);
		assertThat(names, hasSize(6));
		assertThat(
				names,
				hasItems("propfile/test1.txt", "propfiles/test2.txt", "propfiles/test3.txt", "singleembed/spropfile/test4.txt",
						"singleembed/spropfiles/test5.txt", "singleembed/spropfiles/test6.txt"));

		response = instTarget.path("propfile").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(1));
		assertThat(names, hasItems("propfile/test1.txt"));

		response = instTarget.path("propfiles").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(2));
		assertThat(names, hasItems("propfiles/test2.txt", "propfiles/test3.txt"));

		response = instTarget.path("singleembed").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(3));
		assertThat(names, hasItems("singleembed/spropfile/test4.txt", "singleembed/spropfiles/test5.txt", "singleembed/spropfiles/test6.txt"));

		response = instTarget.path("singleembed.spropfile").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(1));
		assertThat(names, hasItems("singleembed/spropfile/test4.txt"));

		response = instTarget.path("singleembed.spropfiles").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(2));
		assertThat(names, hasItems("singleembed/spropfiles/test5.txt", "singleembed/spropfiles/test6.txt"));

	}

	@Test
	public void testServerFile() throws JAXBException, IOException {

		JAXBContext jaxbContext = JaxbHelper.createMoxyJaxbContext(FileInfos.class, FileInfo.class, FileID.class, FileIDs.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");

		// prepare server prefix.
		FleximsDynamicEntityImpl configItem = JpaHelper.createNewEntity(em, ConfigItem.TYPE_NAME);
		configItem.set(ConfigItem.PROP_NAME_PROP_NAME, "file.server.topdirectory");
		Path path = Paths.get(".").toAbsolutePath().resolve("src");

		configItem.set(ConfigItem.PROP_NAME_PROP_VALUE, path.toString());
		FleximsDynamicEntityImpl retEntity = postJson(jerseyClientRule.target.path("rs/inst").path("save"), configItem);

		FleximsDynamicEntityImpl fileMain = JpaHelper.createNewEntity(em, "SFilemain");
		fileMain.set("propfile", "test/resources/arquillian.xml");
		fileMain.set("propdirectory", "test/java");

		fileMain.set("mpropfile", Arrays.asList("test/resources/tomcat7x/conf/server.xml", "test/resources/tomcat7x/conf/tomcat-users.xml"));
		fileMain.set("mpropdirectory", Arrays.asList("test/resources/rs"));

		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "SFileembed1");
		embed1.set("spropfile", "test/testfile/WEB-INF/web.xml");
		embed1.set("spropdirectory", "test/testfile/java");
		embed1.set("smpropfile", Arrays.asList("test/testfile/WEB-INF/beans.xml"));
		embed1.set("smpropdirectory", Arrays.asList("test/testfile/META-INF"));

		fileMain.set("singleembed", embed1);

		em.getTransaction().begin();
		em.persist(fileMain);
		em.getTransaction().commit();

		WebTarget instTarget = target.path("listserverfiles").path("SFilemain").path(String.valueOf(fileMain.getId()));
		// retrieve client files
		String ret = createBuilder(instTarget).get(String.class);
		FileInfos fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(9));

		ret = createBuilder(instTarget.path("propfile")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(1));

		ret = createBuilder(instTarget.path("propdirectory")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(1));

		// can walk down directory entry.
		ret = createBuilder(instTarget.path("propdirectory").path("test/java/com")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(path.resolve("test/java/com").toFile().listFiles().length));

		ret = createBuilder(instTarget.path("mpropfile")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(2));

		ret = createBuilder(instTarget.path("singleembed")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(4));

		ret = createBuilder(instTarget.path("singleembed.smpropfile")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(1));

		// extra path is only for directory exentry.
		ret = createBuilder(instTarget.path("singleembed.smpropfile").path("test/resources/tomcat7x/")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(0));

		ret = createBuilder(instTarget.path("singleembed.smpropfile").path("test/resources/bad")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(0));

		// trace down directory
		ret = createBuilder(instTarget.path("singleembed.smpropdirectory").path("test/testfile/META-INF")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(path.resolve("test/testfile/META-INF").toFile().listFiles().length));

		ret = createBuilder(instTarget.path("singleembed.smpropdirectory").path("test/testfile/bad")).get(String.class);
		fileInfos = (FileInfos) unmarshaller.unmarshal(new StringReader(ret));
		assertThat(fileInfos.getFileInfos(), hasSize(0));

		// download
		WebTarget downloadTarget = target.path("serverfiles").path("SFilemain").path(String.valueOf(fileMain.getId()));
		Response response = downloadTarget.request().get();
		List<String> names = extraZipNames(response);
		// directory is downloaded recursively.
		assertTrue(names.size() > 10);

		// a single file is downloaded directly.
		response = downloadTarget.path("propfile").request().get();
		assertThat(response.getMediaType().getSubtype(), equalTo("xml"));
		assertThat(response.getHeaderString("Content-Disposition"), containsString("arquillian.xml"));

		// a directory.
		response = downloadTarget.path("propdirectory").request().get();
		names = extraZipNames(response);
		assertTrue(names.size() > 2);

		// sub directory
		response = downloadTarget.path("propdirectory").path("test/java/com/flexdms").request().get();
		names = extraZipNames(response);
		assertTrue(names.size() > 2);

		// download multiple files.
		response = downloadTarget.path("mpropfile").request().get();
		names = extraZipNames(response);
		assertThat(names, hasSize(2));

		response = downloadTarget.path("singleembed").request().get();
		names = extraZipNames(response);
		assertTrue(names.size() > 5);

	}
}
