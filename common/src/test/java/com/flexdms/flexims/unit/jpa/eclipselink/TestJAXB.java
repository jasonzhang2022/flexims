package com.flexdms.flexims.unit.jpa.eclipselink;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.IOUtils;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.unit.jpa.eclipselink.helper.MetaSourceBuilder;

/**
 * Make sure we can serialize JPA mapping to/from xml/json
 * 
 * @author jason.zhang
 * 
 */
@RunWith(JUnit4.class)
public class TestJAXB {

	XMLEntityMappings entityMaps;

	@Before
	public void setup() {
		entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

	}

	@Test
	public void testXml() throws JAXBException {
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());

		MetaSourceBuilder.fromXml(new StringReader(writer.toString()));

	}

	@Test
	public void testJson() throws JAXBException {
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toJson(entityMaps, writer);
		System.out.println(writer.toString());

		MetaSourceBuilder.fromJson(new StringReader(writer.toString()));

	}

	@Test
	public void testJsonName() throws JAXBException, IOException {
		String jsonString = IOUtils.toString(TestJAXB.class.getClassLoader().getResourceAsStream("com/flexdms/flexims/unit/jpa/rs/type.json"));
		XMLEntityMappings tempMappings = MetaSourceBuilder.fromJson(new StringReader(jsonString));
		assertThat(tempMappings.getEntities().get(0).getName(), equalTo("Test4"));

	}

}
