package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
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
public class TestBasicMetaSource extends XMLMetadataSource {

	public static boolean twoAttributes = false;

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		BasicAccessor timeAccessor = MetaSourceBuilder.AddBasicProperty(test, "time1", "java.util.Calendar");
		TemporalMetadata tMetadata = new TemporalMetadata();
		tMetadata.setTemporalType("Time");
		timeAccessor.setTemporal(tMetadata);

		ColumnMetadata columnMetadata = new ColumnMetadata();
		columnMetadata.setPrecision(20);
		columnMetadata.setScale(5);
		columnMetadata.setLength(254);
		columnMetadata.setNullable(true);
		columnMetadata.setUnique(true);
		timeAccessor.setColumn(columnMetadata);

		BasicAccessor fnameAccessor = MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		if (twoAttributes)
			MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

		List<PropertyMetadata> props = new ArrayList<>(3);
		PropertyMetadata propertyMetadata = new PropertyMetadata();
		propertyMetadata.setName("System");
		propertyMetadata.setValue("true");
		props.add(propertyMetadata);
		fnameAccessor.setProperties(props);
		test.setProperties(props);

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
