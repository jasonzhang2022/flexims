package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
public class TestDemoRelation extends XMLMetadataSource {

	public static boolean twoAttributes = false;

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.fromXml(new InputStreamReader(Thread.currentThread().getContextClassLoader()
				.getResourceAsStream("META-INF/relation_model_orm.xml")));
		MetaSourceBuilder.toXml(entityMaps, new OutputStreamWriter(System.out));
		return entityMaps;

	}

}
