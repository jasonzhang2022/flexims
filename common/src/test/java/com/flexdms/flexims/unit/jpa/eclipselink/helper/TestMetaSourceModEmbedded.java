package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

/**
 * A different way to modify the source. Simuate the PersistenceUnit refresh
 * mechanism in production
 * 
 * @author jason.zhang
 * 
 */
public class TestMetaSourceModEmbedded extends XMLMetadataSource {

	public static XMLEntityMappings entityMappings;
	static {
		entityMappings = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMappings, "Test");
		MetaSourceBuilder.addID(test, entityMappings);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");
		entityMappings = MetaSourceBuilder.roundTripEntityMappings(entityMappings);
	}

	public static void addTest1() {
		EntityAccessor test1 = MetaSourceBuilder.createEntityAccessor(entityMappings, "Test1");
		MetaSourceBuilder.addID(test1, entityMappings);
		MetaSourceBuilder.addVersion(test1);
		MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");

		entityMappings = MetaSourceBuilder.roundTripEntityMappings(entityMappings);

	}

	public static void addEmbedded() {
		EmbeddableAccessor test1 = MetaSourceBuilder.createEmbeddableAccessor(entityMappings, "Test1");
		// MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");
		entityMappings = MetaSourceBuilder.roundTripEntityMappings(entityMappings);

	}

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		return MetaSourceBuilder.roundTripEntityMappings(entityMappings);
	}

}
