package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.RSMsg;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.MetaSourceBuilder;

/**
 * TODO: test case for create type with inheritance, Test case for check
 * property name
 * 
 * @author jason.zhang
 * 
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TestTypeService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		return ArchiveUtil.buildRsWebArchive();
	}

	@ArquillianResource
	protected URL baseURL;

	public TestTypeService() {
		super();
		servicePrefixString = "rs/type";
	}

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	@Test
	public void testCheckTypeName() throws Exception {

		WebTarget resource;
		AppMsg ret;

		// test
		resource = target.path("checkname").path("test");
		ret = getJson(resource);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("type with the same name already exists. Please use another name"));

		// table
		resource = target.path("checkname").path("table");
		ret = getJson(resource);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("is a reserved word. Please choose another name"));

		// unsafe character
		resource = target.path("checkname").path(" ?");
		ret = getJson(resource);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("unsafe character is found."));

	}

	@Test
	@InSequence(1)
	public void testMeta() throws Exception {
		WebTarget resource;

		// test
		resource = target.path("meta");
		// System.out.println(resource.request(MediaType.APPLICATION_JSON).get(String.class));
		resource.request(MediaType.APPLICATION_JSON).get(XMLEntityMappings.class);

	}

	@Test
	@InSequence(1)
	public void testSchema() throws Exception {
		WebTarget resource;

		// test
		resource = target.path("schema");

		String schema = resource.request(MediaType.TEXT_PLAIN).get(String.class);
		assertTrue(schema.length() > 3);
	}

	static String typeName = "Test";

	/**
	 * This is for enttity TODO check embedded
	 * 
	 * @throws Exception
	 */
	@Test
	@InSequence(5)
	public void testAddType() throws Exception {
		WebTarget resource;
		// test
		resource = target.path("savetype");
		String jsonString = IOUtils.toString(TestTypeService.class.getClassLoader().getResourceAsStream("com/flexdms/flexims/unit/jpa/rs/type.json"));
		XMLEntityMappings tempMappings = MetaSourceBuilder.fromJson(new StringReader(jsonString));
		assertThat(tempMappings.getEntities().get(0).getName(), equalTo("Test4"));
		XMLEntityMappings entityMappings = resource.request(MediaType.APPLICATION_JSON).post(
				Entity.entity(jsonString, MediaType.APPLICATION_JSON_TYPE), XMLEntityMappings.class);
		assertThat(entityMappings.getEntities(), hasSize(1));
		EntityAccessor entityAccessor = entityMappings.getEntities().get(0);
		assertThat("Test4", equalTo(entityAccessor.getName()));
		assertThat("Test4seq", equalTo(entityAccessor.getAttributes().getIds().get(0).getGeneratedValue().getGenerator()));
	}

	@Test
	@InSequence(10)
	public void testGetType() throws Exception {
		WebTarget resource;

		// test
		resource = target.path("getsingle").path(typeName);

		XMLEntityMappings ret = resource.request(MediaType.APPLICATION_JSON).get(XMLEntityMappings.class);
		StringWriter sWriter = new StringWriter();
		MetaSourceBuilder.toJson(ret, sWriter);
		System.out.println(sWriter.toString());
		assertThat(ret.getEntities().get(0).getName(), equalTo(typeName));

		// bad request
		resource = target.path("getsingle").path("bad");

		Response response = resource.request(MediaType.APPLICATION_JSON).buildGet().invoke();
		assertThat(response.getStatus(), equalTo(204));

	}

	@Test
	public void testCheckPropName() throws Exception {

		WebTarget typepath = target.path("checkname").path("test");
		WebTarget resource = null;
		AppMsg ret;

		// test
		resource = typepath.path("fname");
		// ret = getJson(resource);
		ret = resource.request(MediaType.APPLICATION_JSON).get(AppMsg.class);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("property with the same name already exists. Please use another name"));

		// table
		resource = typepath.path("table");
		ret = getJson(resource);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("is a reserved word. Please choose another name"));

		// unsafe character
		resource = typepath.path(" ?");
		ret = getJson(resource);
		assertThat(ret.getStatuscode(), equalTo(1l));
		System.out.println("response message " + ret.getMsg());
		assertThat(ret.getMsg(), equalTo("unsafe character is found."));
	}

	@Test
	@InSequence(10)
	public void testSavePropBasicForEntity() throws Exception {

		String typeName = "test";

		// basic property
		XMLEntityMappings ret = target.path("getsingle").path(typeName).request(MediaType.APPLICATION_JSON).get(XMLEntityMappings.class);
		EntityAccessor entityAccessor = ret.getEntities().get(0);
		MetaSourceBuilder.AddBasicProperty(entityAccessor, "propstr", "java.lang.String");
		ret = target.path("saveprop").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(ret, MediaType.APPLICATION_JSON_TYPE), XMLEntityMappings.class);

		// the new property goes to meta model
		AppMsg msg = getJson(target.path("checkname").path(typeName).path("propstr"));
		assertThat(msg.getStatuscode(), equalTo(1l));
		assertThat(msg.getMsg(), equalTo("property with the same name already exists. Please use another name"));

		// collection ptroperty
		ret = target.path("getsingle").path(typeName).request(MediaType.APPLICATION_JSON).get(XMLEntityMappings.class);
		entityAccessor = ret.getEntities().get(0);
		MetaSourceBuilder.AddElementCollectionProperty(entityAccessor, "propCollection", "java.lang.String");
		ret = target.path("saveprop").request(MediaType.APPLICATION_JSON)
				.post(Entity.entity(ret, MediaType.APPLICATION_JSON_TYPE), XMLEntityMappings.class);
		// the new property goes to meta model
		msg = getJson(target.path("checkname").path(typeName).path("propCollection"));
		assertThat(msg.getStatuscode(), equalTo(1l));
		assertThat(msg.getMsg(), equalTo("property with the same name already exists. Please use another name"));
	}
}
