package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Test the Relation can be extends
 * 
 * @author jason.zhang
 * 
 */
public class TestOneManyMetaSource extends XMLMetadataSource {

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
			MetaSourceBuilder.addManyToOne(test1, test, "test");
			OneToManyAccessor lisToManyAccessor = MetaSourceBuilder.addOneToMany(test, test1, "test1s", "test");
			OrderByMetadata orderByMetadata = new OrderByMetadata();
			//orderByMetadata.setValue("fname");
			lisToManyAccessor.setOrderBy(orderByMetadata);
		}

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());

		writer = new StringWriter();
		MetaSourceBuilder.toJson(entityMaps, writer);
		System.out.println(writer.toString());
		return entityMaps;
	}
}
