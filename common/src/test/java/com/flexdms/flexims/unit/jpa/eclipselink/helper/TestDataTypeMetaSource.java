package com.flexdms.flexims.unit.jpa.eclipselink.helper;

/**
 * Many Data type to test
 */
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;

/**
 * Test the basic properties can be extends
 * 
 * @author jason.zhang
 * 
 */
public class TestDataTypeMetaSource extends XMLMetadataSource {

	public static boolean twoAttributes = false;

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.fromJson(new InputStreamReader(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("com/flexdms/flexims/unit/jpa/eclipselink/basic_types.json")));

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
