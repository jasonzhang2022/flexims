package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.ManyToManyMapping;

/**
 * Test the Relation can be extends
 * 
 * @author jason.zhang
 * 
 */
public class TestManyManyMetaSource extends XMLMetadataSource {

	public static boolean hasRel = false;

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

		EntityAccessor test1 = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test1");
		MetaSourceBuilder.addID(test1, entityMaps);
		MetaSourceBuilder.addVersion(test1);
		MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");

		if (hasRel) {
			MetaSourceBuilder.addManyToMany(test1, test, "tests", null);
			MetaSourceBuilder.addManyToMany(test, test1, "test1s", "tests");
		}

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());
		return entityMaps;
	}
}
