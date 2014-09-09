package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Test the Relation can be extends
 * 
 * @author jason.zhang
 * 
 */
public class TestEmbeddedCollectionMetaSource extends XMLMetadataSource {

	public static boolean hasRel = false;

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

		EmbeddableAccessor test1 = MetaSourceBuilder.createEmbeddableAccessor(entityMaps, "Test1");
		BasicAccessor basicAccessor = MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");
		ColumnMetadata columnMetadata = new ColumnMetadata();
		columnMetadata.setName("test1_fname");
		basicAccessor.setColumn(columnMetadata);

		if (hasRel) {
			MetaSourceBuilder.AddElementCollectionProperty(test, "test1", "Test1");
		}

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());
		return entityMaps;
	}
}
