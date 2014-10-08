package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.TestEntityUtil;

/**
 * TODO: test case for create type with inheritance, Test case for check
 * property name
 * 
 * @author jason.zhang
 * 
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TestInstService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		return ArchiveUtil.buildRsWebArchive();
	}

	boolean sameDB = true;

	public TestInstService() {
		servicePrefixString = "rs/inst";
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	public FleximsDynamicEntityImpl createEntityImpl(String name) {
		return JpaHelper.createNewEntity(em, name);
	}

	static String testName = "Basictype";
	static Long testid = null;

	@Test
	@InSequence(1)
	public void test1SaveBasic() throws Exception {
		FleximsDynamicEntityImpl entity = createEntityImpl(testName);
		TestEntityUtil.setBasicType(entity);

		// test
		FleximsDynamicEntityImpl retEntity = postJson(target.path("save"), entity);
		testid = retEntity.getId();
		System.out.printf("!!!!!!!!!!!id=%d, version=%s\n", testid, retEntity.getVersion().toGMTString());
		retEntity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));
		assertThat(retEntity.get("shortstring").toString(), equalTo(entity.get("shortstring").toString()));
	}

	@Test
	@InSequence(5)
	public void test2Update() throws Exception {
		if (testid == null) {
			test1SaveBasic();
		}

		FleximsDynamicEntityImpl retEntity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));
		retEntity.set("shortstring", "update");
		retEntity = postJson(target.path("save"), retEntity);
		// test
		retEntity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));

		assertThat(retEntity.get("shortstring").toString(), equalTo("update"));
	}

	@Test
	@InSequence(10)
	public void test3Delete() throws Exception {
		if (testid == null) {
			test1SaveBasic();
		}
		 deleteJson(target.path("delete").path(testName).path(String.valueOf(testid)));
		
		FleximsDynamicEntityImpl entity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));
		assertNull(entity);

	}

	public int getListSize(FleximsDynamicEntityImpl de, String propName) {
		Collection collectnion = de.get(propName);
		return collectnion.size();
	}

	@Test
	@InSequence(1)
	public void test4SaveCollection() throws Exception {
		String testName = "Collection1";
		FleximsDynamicEntityImpl entity = createEntityImpl(testName);
		TestEntityUtil.setCollection1(entity);

		// test
		FleximsDynamicEntityImpl retEntity = postJson(target.path("save"), entity);
		testid = retEntity.getId();
		System.out.printf("!!!!!!!!!!!id=%d, version=%s\n", testid, retEntity.getVersion().toGMTString());
		retEntity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));

		assertEquals(getListSize(entity, "shortstring"), getListSize(retEntity, "shortstring"));
	}

	@Test
	@InSequence(1)
	public void test5Embedded() throws Exception {
		String testName = "Embedmain";
		FleximsDynamicEntityImpl de;

		de = JpaHelper.createNewEntity(em, testName);
		TestEntityUtil.setEmbedmain(de, em);

		// test
		FleximsDynamicEntityImpl retEntity = postJson(target.path("save"), de);
		testid = retEntity.getId();
		System.out.printf("!!!!!!!!!!!id=%d, version=%s\n", testid, retEntity.getVersion().toGMTString());
		retEntity = getJson(target.path("get").path(testName).path(String.valueOf(testid)));

		deleteJson(target.path("delete").path(testName).path(String.valueOf(testid)));
	}

	@Test
	@InSequence(1)
	public void testRelation() throws Exception {
		String testName = "Mdoombuild";
		FleximsDynamicEntityImpl doombuild;

		doombuild = JpaHelper.createNewEntity(em, testName);
		doombuild.set("name", "Porter");

		// test
		FleximsDynamicEntityImpl retEntity = postJson(target.path("save"), doombuild);

		// save first room
		FleximsDynamicEntityImpl doomroom = JpaHelper.createNewEntity(em, "Mdoomroom");
		doomroom.set("number", 100);
		doomroom.set("doombuild", retEntity);
		doomroom = postJson(target.path("save"), doomroom);

		// save second room
		FleximsDynamicEntityImpl doomroom1 = JpaHelper.createNewEntity(em, "Mdoomroom");
		doomroom1.set("number", 101);
		doomroom1.set("doombuild", retEntity);
		doomroom1 = postJson(target.path("save"), doomroom1);

		// roload build
		retEntity =getJson(target.path("get").path(testName).path(String.valueOf(retEntity.getId())));
		deleteJson(target.path("delete").path(testName).path(String.valueOf(retEntity.getId())));

		List<FleximsDynamicEntityImpl> rooms = retEntity.get("rooms");
		assertThat(rooms, hasSize(2));

		doomroom1 = getJson(target.path("get").path("Mdoomroom").path(String.valueOf(doomroom1.getId())));
		assertNull(doomroom1);

	}

	@Test
	@InSequence(1)
	public void testRelationCascadeSave() throws Exception {
		FleximsDynamicEntityImpl student = TestEntityUtil.createStudentOneManys(em);

		JaxbHelper helper = JerseyClientRule.jaxHelper;
		StringWriter stringWriter = new StringWriter();
		helper.output(student, stringWriter, false, true);
		// test
		String retJson = target.path("savenested").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(stringWriter.toString(), MediaType.APPLICATION_JSON_TYPE), String.class);
		FleximsDynamicEntityImpl retEntity = (FleximsDynamicEntityImpl) helper.input(new StringReader(retJson), em, false, true);
		deleteJson(target.path("delete").path("Mstudent").path(String.valueOf(retEntity.getId())));

		List<FleximsDynamicEntityImpl> oneManysList = student.get("OneManys");
		assertThat(oneManysList, hasSize(2));
	}
}
