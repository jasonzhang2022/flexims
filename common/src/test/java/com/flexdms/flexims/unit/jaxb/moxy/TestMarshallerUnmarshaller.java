package com.flexdms.flexims.unit.jaxb.moxy;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.Subgraph;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jaxb.moxy.RelationAsDataMoxyMetaSource;
import com.flexdms.flexims.jaxb.moxy.RelationAsIDMoxyMetaSource;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.TestEntityUtil;
import com.flexdms.flexims.unit.TestOXMSetup;

/**
 * Test marshall/unmarshall for variaous scenario. You can also find out what
 * the xml/json looks like. Useful for client development
 * 
 * @author jason.zhang
 * 
 */
@RunWith(JUnit4.class)
public class TestMarshallerUnmarshaller {

	public static boolean dumpSchema = false;
	public static boolean dumpMapping = false;

	@ClassRule
	public static JPA_JAXB_EmbeddedDerby_Rule clientSetupRule = new JPA_JAXB_EmbeddedDerby_Rule();

	@Rule
	public EntityManagerRule emRule = new EntityManagerRule();
	JaxbHelper helper;
	EntityManager em;

	@Before
	public void setup() throws JAXBException {
		em = emRule.em;
		helper = new JaxbHelper();
		helper.init(TestOXMSetup.factory, TestOXMSetup.dcl);
		if (dumpSchema) {
			System.out.println(helper.generateSchema());
		}
		if (dumpMapping) {
			dumpMappingXml();
		}
	}

	public static void dumpMappingXml() throws JAXBException {

		RelationAsIDMoxyMetaSource src = new RelationAsIDMoxyMetaSource(TestOXMSetup.factory);
		XmlBindings bindings = src.getXmlBindings(null, TestOXMSetup.dcl);
		RelationAsIDMoxyMetaSource.toOXM(bindings, new OutputStreamWriter(System.out));
	}

	public static void dumpMappingXmlNested() throws JAXBException {

		RelationAsIDMoxyMetaSource src = new RelationAsDataMoxyMetaSource(TestOXMSetup.factory);
		XmlBindings bindings = src.getXmlBindings(null, TestOXMSetup.dcl);
		RelationAsIDMoxyMetaSource.toOXM(bindings, new OutputStreamWriter(System.out));
	}

	public void outputXMlJson(FleximsDynamicEntityImpl de) {
		helper.toXml(de, new OutputStreamWriter(System.out));
		helper.toJson(de, new OutputStreamWriter(System.out));
	}

	@Test
	public void testBasicType() throws Exception {

		String testName = "Basictype";
		FleximsDynamicEntityImpl de, de1;
		StringWriter writer;
		String string;

		de = JpaHelper.createNewEntity(em, testName);
		de.setId(3);
		de.setVersion(new Timestamp(new Date().getTime()));
		TestEntityUtil.setBasicType(de);
		outputXMlJson(de);
		// xml
		writer = new StringWriter();
		helper.toXml(de, writer);
		string = writer.toString();

		de1 = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(string), em);
		assertEquals(5, (int)de1.get("propint"));
		assertEquals(de1.getClass().getSuperclass(), FleximsDynamicEntityImpl.class);
		// json
		writer = new StringWriter();
		helper.toJson(de, writer);
		string = writer.toString();
		System.out.println(string);
		de1 = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(string), em);
		assertEquals(5, (int)de1.get("propint"));
	}

	@Test
	public void testEntities() throws Exception {

		String testName = "Basictype";
		FleximsDynamicEntityImpl de, de1;
		StringWriter writer;
		String string;

		de = JpaHelper.createNewEntity(em, testName);
		de.setId(3);
		de.setVersion(new Timestamp(new Date().getTime()));
		TestEntityUtil.setBasicType(de);

		de1 = JpaHelper.createNewEntity(em, testName);
		de1.setId(4);
		de1.setVersion(new Timestamp(new Date().getTime()));
		TestEntityUtil.setBasicType(de1);

		Entities entities = new Entities();
		entities.getItems().add(de);
		entities.getItems().add(de1);

		FleximsDynamicEntityImpl de2;
		de2 = JpaHelper.createNewEntity(em, "Mspecial");
		TestEntityUtil.setSpecialtype(de2);
		de2.setId(3);
		de2.setVersion(new Timestamp(new Date().getTime()));
		entities.getItems().add(de2);

		StringWriter stringWriter = new StringWriter();
		helper.toXml(entities, stringWriter);
		String xmlString = stringWriter.toString();
		System.out.println(xmlString);
		helper.toJson(entities, new OutputStreamWriter(System.out));

		Entities newEntities = (Entities) helper.fromXml(new StringReader(xmlString), em);
		FleximsDynamicEntityImpl de4 = (FleximsDynamicEntityImpl) newEntities.getItems().get(2);
		assertEquals(newEntities.getItems().size(), 3);
		assertEquals(de4.getClass().getSimpleName(), "Mspecial");
	}

	@Test
	public void testMSpecial() throws Exception {
		// MetaSourceBuilder.toXml(DynamicMetaSource.entityMaps, new
		// OutputStreamWriter(System.out));

		String testName = "Mspecial";
		FleximsDynamicEntityImpl de;
		de = JpaHelper.createNewEntity(em, testName);
		TestEntityUtil.setSpecialtype(de);
		de.setId(3);
		de.setVersion(new Timestamp(new Date().getTime()));

		outputXMlJson(de);
	}

	@Test
	public void testCollection1() throws Exception {

		String testName = "Collection1";
		FleximsDynamicEntityImpl de;

		if (dumpSchema) {
			System.out.println(helper.generateSchema());
		}

		de = JpaHelper.createNewEntity(em, testName);
		de.setId(3);
		de.setVersion(new Timestamp(new Date().getTime()));
		TestEntityUtil.setCollection1(de);
		// xml
		outputXMlJson(de);
	}

	@Test
	public void testEmbedded() throws Exception {

		String testName = "Embedmain";
		FleximsDynamicEntityImpl de;

		de = JpaHelper.createNewEntity(em, testName);
		TestEntityUtil.setEmbedmain(de, em);
		outputXMlJson(de);
	}

	@Test
	public void testPrivateRelation() throws Exception {

		FleximsDynamicEntityImpl build = TestEntityUtil.createDoomBuild(em);
		// EntityTransaction tx = em.getTransaction();

		em.refresh(build);

		StringWriter sWriter = new StringWriter();
		helper.toJson(build, sWriter);
		String jsonString = sWriter.toString();
		System.out.print(jsonString);

		// rooms are private relationshio, but it is inverse relation. So it is
		// mapped to ID and resolved by ID
		build = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(jsonString), em);
		List<FleximsDynamicEntityImpl> rooms = build.get("rooms");
		assertThat(rooms, hasSize(5));

		// reverse relation does not use the same object.
		// room is loaded from database, associated build is loaded when room is
		// loaded from database. The loaded build is different from
		// the one from XML marshalled.
		// assertTrue(rooms.get(0).get("doombuild")==build);

	}

	@Test
	public void testRelationMarshallID() throws Exception {
		FleximsDynamicEntityImpl de = TestEntityUtil.createAndPersisteDemoRelationObject(em);
		outputXMlJson(de);

		StringWriter sWriter = new StringWriter();
		helper.toJson(de, sWriter);
		String jsonString = sWriter.toString();
		FleximsDynamicEntityImpl de1 = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(jsonString), em);

		List<FleximsDynamicEntityImpl> onemanys = de1.get("OneManys");
		assertThat(onemanys, hasSize(2));
		helper.toJson(de1, new OutputStreamWriter(System.out));
		
		sWriter = new StringWriter();
		helper.toXml(de, sWriter);
		String xmlString = sWriter.toString();
		System.out.println(xmlString);
		
		FleximsDynamicEntityImpl de2 = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(xmlString), em);
		List<FleximsDynamicEntityImpl> courses = de2.get("Courses");
		assertThat(courses, hasSize(2));
	}

	@Test
	public void testRelationObjectGraph() throws Exception {
		FleximsDynamicEntityImpl de = TestEntityUtil.createDoomBuild(em);
		em.refresh(de);
		Object room = ((List) de.get("rooms")).get(0);
		em.refresh(de);
		ObjectGraph objectGraph = JaxbHelper.buildObjectGraph(em, "Mdoomroom", Arrays.asList("name", "doombuild.name"), helper.getJcNotIdRef());
		StringWriter sWriter = new StringWriter();
		helper.output(room, sWriter, false, true, objectGraph);
		String jsonString = sWriter.toString();
		System.out.println(jsonString);

		FleximsDynamicEntityImpl room1 = (FleximsDynamicEntityImpl) helper.input(new StringReader(jsonString), em, false, true);
		FleximsDynamicEntityImpl de1 = room1.get("doombuild");
		assertNotNull(room1.get("name"));
		assertNotNull(de1.get("name"));
		assertNull(room1.get("number"));

		objectGraph = JaxbHelper.buildObjectGraph(em, "Mdoomroom", Arrays.asList("number", "doombuild.id"), helper.getJcNotIdRef());
		sWriter = new StringWriter();
		helper.output(room, sWriter, false, true, objectGraph);
		jsonString = sWriter.toString();
		System.out.println(jsonString);

		room1 = (FleximsDynamicEntityImpl) helper.input(new StringReader(jsonString), em, false, true);
		de1 = room1.get("doombuild");
		assertNull(room1.get("name"));
		assertNull(de1.get("name"));
		assertNotNull(room1.get("number"));

	}

	@Test
	public void testEntitiesObjectGraph() throws Exception {
		FleximsDynamicEntityImpl de = TestEntityUtil.createDoomBuild(em);
		em.refresh(de);
		List rooms = de.get("rooms");
		Entities entities = new Entities();
		entities.getItems().addAll(rooms);

		ObjectGraph topGraph = JAXBHelper.getJAXBContext(helper.getJcNotIdRef()).createObjectGraph(Entities.class);
		Subgraph subGraph = topGraph.addSubgraph("items");
		JaxbHelper.populateSubgraph(subGraph, Arrays.asList("name", "doombuild.name"));

		StringWriter sWriter = new StringWriter();
		helper.output(entities, sWriter, true, true, topGraph);
		String jsonString = sWriter.toString();
		System.out.println(jsonString);
		//
		// FleximsDynamicEntityImpl de1=(FleximsDynamicEntityImpl)
		// helper.input(new StringReader(jsonString), em, false, true);
		// FleximsDynamicEntityImpl room=(FleximsDynamicEntityImpl)
		// ((List)de1.get("rooms")).get(0);
		// assertNull(room.get("number"));
		// assertNull(room.get("doombuild"));
	}

	@Test
	public void testRelationMarshallNested() throws Exception {
		FleximsDynamicEntityImpl student = TestEntityUtil.createAndPersisteDemoRelationObject(em);
		student.set("doomroom", null);
		student.set("doombuild", null);

		// we only have oneManys and Courses
		StringWriter sWriter = new StringWriter();
		helper.output(student, sWriter, false, true);
		String jsonString = sWriter.toString();
		System.out.println(jsonString);
		// dumpMappingXmlNested();
		student = (FleximsDynamicEntityImpl) helper.input(new StringReader(jsonString), em, false, true);
		List<FleximsDynamicEntityImpl> oneManys = student.get("OneManys");
		assertThat(oneManys, hasSize(2));

		// students for courses should be marshalled as ID.
		List<FleximsDynamicEntityImpl> courses = student.get("Courses");
		List<FleximsDynamicEntityImpl> ss = courses.get(0).get("Students");
		assertTrue(!ss.isEmpty());
		assertTrue(ss.get(0) == student);

		// ID based marshall does not expect data for oneManys
		student = (FleximsDynamicEntityImpl) helper.input(new StringReader(jsonString), em, false, false);
		oneManys = student.get("OneManys");
		assertTrue(oneManys == null || oneManys.isEmpty());

	}

}
