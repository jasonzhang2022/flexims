package com.flexdms.flexims.deploy.tomcat;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicClassLoader;

@ApplicationScoped
public class TomcatEM implements Serializable, EntityManagerFactoryProvider {

	public static final Logger LOGGER = java.util.logging.Logger.getLogger(TomcatEM.class.getName());
	Map<String, Object> properties;
	EntityManagerFactory factory;
	public static final String RESOURCE_NAME = "flexims";

	@PostConstruct
	public void init() {
		properties = new HashMap<String, Object>();

		DynamicClassLoader dcl = new FleximsDynamicClassLoader(EntityManagerFactory.class.getClassLoader());
		properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
		properties.put(PersistenceUnitProperties.WEAVING, "static");
		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);

		factory = Persistence.createEntityManagerFactory(RESOURCE_NAME, properties);

	}

	public EntityManagerFactory getEMF() {
		return factory;
	}

	public DynamicClassLoader getEMFClassLoader() {
		return (DynamicClassLoader) properties.get(PersistenceUnitProperties.CLASSLOADER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.flexdms.flexims.tomcat.PersistenceUnit#refresh(boolean)
	 */

	public void refresh(boolean changeSuper) {
		if (!changeSuper) {
			((JpaEntityManagerFactory) factory).refreshMetadata(properties);
		} else {
			factory.close();
			DynamicClassLoader dcl = new FleximsDynamicClassLoader(EntityManagerFactory.class.getClassLoader());
			properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
			factory = Persistence.createEntityManagerFactory(RESOURCE_NAME, properties);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EntityManager createEM() {
		return factory.createEntityManager();
	}

	/**
	 * Convenient Utility.
	 * 
	 * @return
	 */
	public EntityManager getEM() {
		return App.getEM();
	}

	public void closeEntityManager(EntityManager entityManager) {
		try {
			if (entityManager.isOpen()) {
				entityManager.close();
			}
		} catch (Exception e) {
			LOGGER.info("Exception when close em");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getSchema() throws Exception {

		// do not touch database
		properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
		try {
			// critical.
			// refresh is needed
			refresh(false);

			Map properties = new HashMap();
			/*
			 * properties.put("javax.persistence.database-product-name",
			 * "Postgresql");
			 * properties.put("javax.persistence.database-major-version", "9");
			 * properties.put("javax.persistence.database-minor-version", "");
			 */

			// We do not want to touch database
			properties.put("javax.persistence.schema-generation.database.action", "none");

			// we want both drop and create script.
			properties.put("javax.persistence.schema-generation.scripts.action", "drop-and-create");
			// when generate script, also include schema generation
			properties.put("javax.persistence.schema-generation.create-database-schemas", "true");

			// use meta(annotation or xml) to extract schema information.
			properties.put("javax.persistence.schema-generation.create-source", "metadata");
			properties.put("javax.persistence.schema-generation.drop-source", "metadata");
			StringWriter create = new StringWriter();
			StringWriter drop = new StringWriter();
			properties.put("javax.persistence.schema-generation.scripts.drop-target", drop);
			properties.put("javax.persistence.schema-generation.scripts.create-target", create);

			// need this so dynamic class can be looked up
			properties.put(PersistenceUnitProperties.CLASSLOADER, this.properties.get(PersistenceUnitProperties.CLASSLOADER));

			Persistence.generateSchema(RESOURCE_NAME, properties);
			String schema = drop.toString() + "\n\n" + create.toString();

			return schema;
		} finally {
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
		}

	}
}
