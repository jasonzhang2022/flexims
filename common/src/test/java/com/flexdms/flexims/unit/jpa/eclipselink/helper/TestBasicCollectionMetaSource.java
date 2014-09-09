package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.OrderColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Test the basic properties can be extends
 * 
 * @author jason.zhang
 * 
 */
public class TestBasicCollectionMetaSource extends XMLMetadataSource {

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddElementCollectionProperty(test, "propstrs", "java.lang.String");

		ElementCollectionAccessor dateAccessor = MetaSourceBuilder.AddElementCollectionProperty(test, "propdates", "java.util.Calendar");
		// OrderByMetadata orderBy=new OrderByMetadata();
		// dateAccessor.setOrderBy(orderBy);
		dateAccessor.setAttributeType("java.util.List");

//		OrderColumnMetadata orderColumnMetadata = new OrderColumnMetadata();
//		orderColumnMetadata.setName("idx");
//		dateAccessor.setOrderColumn(orderColumnMetadata);
		TemporalMetadata tMetadata = new TemporalMetadata();
		tMetadata.setTemporalType("DATE");
		dateAccessor.setTemporal(tMetadata);

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
