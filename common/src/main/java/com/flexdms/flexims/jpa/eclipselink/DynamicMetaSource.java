package com.flexdms.flexims.jpa.eclipselink;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

/**
 * Load JPA mapping file from META-INF/*_model_orm.xml of all jars and database.
 * 
 * @author jason.zhang
 * 
 */
public class DynamicMetaSource extends XMLMetadataSource {
	public static final Logger LOGGER = Logger.getLogger(DynamicMetaSource.class.getCanonicalName());
	private static XMLEntityMappings entityMaps;
	public static List<URL> resources;
	public static final String FIXED_NAME="fixed";

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {
		/*
		 * This method should be called only once by EMF Moreover emf could
		 * modify it for its purpose or schema generation. So we need to
		 * renormalize it before return it.
		 */
		return MetaSourceBuilder.roundTripEntityMappings(entityMaps);
	}

	static {
		entityMaps = MetaSourceBuilder.createEmptyEntityMappings();

		resources = CPScanner.scanResources(new ResourceFilter().resourceName("META-INF").resourceName("*_model_orm.xml"));
		for (URL url : resources) {
			LOGGER.info("----------------find model resource " + url.toExternalForm());
			try {
				XMLEntityMappings xmlEntityMappings = MetaSourceBuilder.fromXml(new InputStreamReader(url.openStream()));
				mergeMap(xmlEntityMappings, entityMaps);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		addFixedProperty();
		
		DynamicMetaSource.entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);

	}
	
	public static final void mergeMap(XMLEntityMappings from, XMLEntityMappings to) {
		to.getTableGenerators().addAll(from.getTableGenerators());
		to.getEntities().addAll(from.getEntities());
		to.getEmbeddables().addAll(from.getEmbeddables());
		to.getMappedSuperclasses().addAll(from.getMappedSuperclasses());
	}

	public static XMLEntityMappings getEntityMaps() {
		return entityMaps;
	}

	public static void setEntityMaps(XMLEntityMappings entityMaps) {
		DynamicMetaSource.entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
	}

	public static void normalize() {
		DynamicMetaSource.entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
	}

	public static void addFixedProperty() {
		for (EntityAccessor e : entityMaps.getEntities()) {
			PropertyMetadata pMetadata = new PropertyMetadata();
			pMetadata.setName(FIXED_NAME);
			pMetadata.setValue("true");
			e.getProperties().add(pMetadata);
		}
		for (EmbeddableAccessor e : entityMaps.getEmbeddables()) {
			PropertyMetadata pMetadata = new PropertyMetadata();
			pMetadata.setName(FIXED_NAME);
			pMetadata.setValue("true");
			e.getProperties().add(pMetadata);
		}
		for (MappedSuperclassAccessor e : entityMaps.getMappedSuperclasses()) {
			PropertyMetadata pMetadata = new PropertyMetadata();
			pMetadata.setName(FIXED_NAME);
			pMetadata.setValue("true");
			e.getProperties().add(pMetadata);
		}
	}
	
	

}
