package com.flexdms.flexims.unit.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import mockit.Mock;
import mockit.MockUp;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.flexdms.flexims.file.FileIDs;
import com.flexdms.flexims.file.FileInfo;
import com.flexdms.flexims.file.FileInfos;
import com.flexdms.flexims.file.FileUtils;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;

public class TestFileUtils {
	@ClassRule
	public static JPA_JAXB_EmbeddedDerby_Rule clientSetupRule = new JPA_JAXB_EmbeddedDerby_Rule();
	@Rule
	public EntityManagerRule emRule = new EntityManagerRule();
	EntityManager em;

	@Before
	public void setup() throws JAXBException {
		em = emRule.em;
	}

	@Test
	public void testFileuploadCheck() {
		XMLEntityMappings mappings = JpaMapHelper.getInternaEntityMappings();
		ClassAccessor accessor = JpaMapHelper.findClassAccessor(mappings, "Filemain");
		assertTrue(FileUtils.isFileUpload(JpaMapHelper.findProp(accessor, "propfile")));
		assertTrue(FileUtils.isFileUpload(JpaMapHelper.findProp(accessor, "propfiles")));

		accessor = JpaMapHelper.findClassAccessor(mappings, "SFilemain");
		assertFalse(FileUtils.isFileUpload(JpaMapHelper.findProp(accessor, "propfile")));
		assertFalse(FileUtils.isFileUpload(JpaMapHelper.findProp(accessor, "mpropfile")));
	}

	@Test
	public void testServerFileChecker() {
		XMLEntityMappings mappings = JpaMapHelper.getInternaEntityMappings();
		ClassAccessor accessor = JpaMapHelper.findClassAccessor(mappings, "Filemain");
		assertFalse(FileUtils.isServerFile(JpaMapHelper.findProp(accessor, "propfile")));
		assertFalse(FileUtils.isServerFile(JpaMapHelper.findProp(accessor, "propfiles")));

		accessor = JpaMapHelper.findClassAccessor(mappings, "SFilemain");
		assertTrue(FileUtils.isServerFile(JpaMapHelper.findProp(accessor, "propfile")));
		assertTrue(FileUtils.isServerFile(JpaMapHelper.findProp(accessor, "mpropfile")));

	}

	@Test
	public void testPathToFileInfo() {
		Path startPath = Paths.get("src", "test", "java", "com", "flexdms", "flexims", "unit");
		Path dirPath = startPath.resolve("file");
		FileInfo fileInfo = FileUtils.pathToFileInfo(dirPath, startPath);
		assertTrue(fileInfo.isDir());
		assertThat(fileInfo.getName(), equalTo("file"));

		Path filePath = dirPath.resolve("TestFileUtils.java");
		fileInfo = FileUtils.pathToFileInfo(filePath, dirPath);
		assertFalse(fileInfo.isDir());
		assertThat(fileInfo.getName(), equalTo("TestFileUtils.java"));
		// assertThat(fileInfo.getMimeType(), equalTo("text/plain"));
		assertTrue(fileInfo.getSize() > 0);

		fileInfo = FileUtils.pathToFileInfo(filePath, startPath);

		assertThat(fileInfo.getName(), equalTo("file" + File.separator + "TestFileUtils.java"));
	}

	@Test
	public void testCollectChildInfo() {
		Path startPath = Paths.get("src", "test", "java", "com", "flexdms", "flexims", "unit", "file");
		FileInfos fileInfos = FileUtils.collectChildFileInfos(startPath, startPath.getParent(), true);
		assertThat(fileInfos.getFileInfos().size(), is(startPath.toFile().list().length));

		FileInfo fileInfo = fileInfos.getFileInfos().get(0);
		assertFalse(fileInfo.isDir());
		assertThat(fileInfo.getName(), startsWith("file" + File.separator));
	}

	@Test
	public void testSearchClientFileIDs() {
		FleximsDynamicEntityImpl fileMain = JpaHelper.createNewEntity(em, "Filemain");
		fileMain.set("propfile", "GD:355");
		fileMain.set("propfiles", Arrays.asList("GD:356", "GD:357"));

		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "Fileembed1");
		embed1.set("spropfile", "GD:358");
		embed1.set("spropfiles", Arrays.asList("GD:359", "GD:360"));
		fileMain.set("singleembed", embed1);

		FleximsDynamicEntityImpl embed2 = JpaHelper.createNewEntity(em, "Fileembed2");
		embed2.set("mpropfile", "GD:361");

		FleximsDynamicEntityImpl embed3 = JpaHelper.createNewEntity(em, "Fileembed2");
		embed3.set("mpropfile", "GD:362");

		fileMain.set("multiembed", Arrays.asList(embed2, embed3));

		FileIDs fileIDs = null;

		fileIDs = FileUtils.searchFileIDs(fileMain, "propfiles", null);
		assertThat(fileIDs.getFileIDs(), hasSize(2));

		fileIDs = FileUtils.searchFileIDs(fileMain, "propfile", null);
		assertThat(fileIDs.getFileIDs(), hasSize(1));

		fileIDs = FileUtils.searchFileIDs(fileMain, "singleembed.spropfile", null);
		assertThat(fileIDs.getFileIDs(), hasSize(1));

		fileIDs = FileUtils.searchFileIDs(fileMain, "singleembed.spropfiles", null);
		assertThat(fileIDs.getFileIDs(), hasSize(2));

		fileIDs = FileUtils.searchFileIDs(fileMain, "singleembed", null);
		assertThat(fileIDs.getFileIDs(), hasSize(3));

		fileIDs = FileUtils.searchFileIDs(fileMain, "multiembed", null);
		assertThat(fileIDs.getFileIDs(), hasSize(2));

		fileIDs = FileUtils.searchFileIDs(fileMain, "multiembed.mpropfile", null);
		assertThat(fileIDs.getFileIDs(), hasSize(2));

		fileIDs = FileUtils.searchFileIDs(fileMain, null, null);
		assertThat(fileIDs.getFileIDs(), hasSize(8));

	}

	public static class FileUtilsMock extends MockUp<FileUtils> {

		@Mock
		public Path serverFileTopDir() {
			return Paths.get("src", "test", "java", "com", "flexdms", "flexims", "unit");
		}
	}

	@Test
	public void testSearchServerFiles() {
		new FileUtilsMock();

		FleximsDynamicEntityImpl fileMain = JpaHelper.createNewEntity(em, "SFilemain");
		fileMain.set("propfile", "file/TestFileUtils.java");
		fileMain.set("propdirectory", "file");

		fileMain.set("mpropfile", Arrays.asList("file/TestFileUtils.java", "file/TestFileJAXB.java"));
		fileMain.set("mpropdirectory", Arrays.asList("file", "jaxb/moxy"));

		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "SFileembed1");
		embed1.set("spropfile", "file/TestFileUtils.java");
		embed1.set("spropdirectory", "file");
		embed1.set("smpropfile", Arrays.asList("file/TestFileUtils.java", "file/TestFileJAXB.java"));
		embed1.set("smpropdirectory", Arrays.asList("file", "jaxb/moxy"));

		fileMain.set("singleembed", embed1);

		FleximsDynamicEntityImpl embed2 = JpaHelper.createNewEntity(em, "SFileembed2");
		embed2.set("propfile", "file/TestFileUtils.java");
		embed2.set("propdirectory", "file");

		FleximsDynamicEntityImpl embed3 = JpaHelper.createNewEntity(em, "SFileembed2");
		embed3.set("propfile", "file/TestFileUtils.java");
		embed3.set("propdirectory", "file");

		fileMain.set("multiembed", Arrays.asList(embed2, embed3));
		FileInfos fileInfos = null;

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "propfile", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(1));
		assertFalse(fileInfos.getFileInfos().get(0).isDir());

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "propdirectory", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(1));
		assertTrue(fileInfos.getFileInfos().get(0).isDir());

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "mpropfile", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));
		assertFalse(fileInfos.getFileInfos().get(0).isDir());

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "mpropdirectory", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));
		assertTrue(fileInfos.getFileInfos().get(0).isDir());

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "singleembed", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(6));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "singleembed.spropfile", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(1));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "singleembed.spropdirectory", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(1));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "singleembed.smpropfile", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "singleembed.smpropdirectory", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "multiembed", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(4));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "multiembed.propfile", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));
		assertThat(fileInfos.getFileInfos().get(0).getName(), equalTo("file" + File.separator + "TestFileUtils.java"));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, "multiembed.propdirectory", null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(2));
		assertThat(fileInfos.getFileInfos().get(0).getName(), equalTo("file"));

		fileInfos = FileUtils.searchServerFileInfos(fileMain, null, null, null);
		assertThat(fileInfos.getFileInfos(), hasSize(16));
	}

}
