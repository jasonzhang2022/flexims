package com.flexdms.flexims.unit.jpa.eclipselink;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import mockit.Mock;
import mockit.MockUp;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;
import org.junit.After;
import org.junit.Before;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicClassLoader;
import com.flexdms.flexims.unit.Util;
import com.flexdms.flexims.util.ThreadContext;
import com.flexdms.flexims.util.Utils;

public class TestORMSetup {
	public static final String RESOURCE_NAME = "test";
	public Map<String, Object> properties;
	public DynamicClassLoader dcl;
	public EntityManagerFactory factory;
	public JpaEntityManagerFactory eclipseFactory;

	public Class<?> metaSrcClass;

	public TestORMSetup() {
		super();
	}

	public class EntityManagerFactoryProviderInternal implements EntityManagerFactoryProvider {

		public EntityManagerFactoryProviderInternal() {
			Util.removeUnitDB();
			properties = new HashMap<String, Object>();

			dcl = new FleximsDynamicClassLoader(EntityManagerFactory.class.getClassLoader());
			properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
			properties.put(PersistenceUnitProperties.WEAVING, "");
			// properties.put(PersistenceUnitProperties.DDL_GENERATION,
			// PersistenceUnitProperties.DROP_AND_CREATE);
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
			// properties.put("eclipselink.orm.validate.schema", true);
			// properties.put("javax.persistence.schema-generation.create-database-schemas",
			// true);
			if (metaSrcClass != null) {
				properties.put(PersistenceUnitProperties.METADATA_SOURCE, metaSrcClass.getCanonicalName());
			}
			//

			// test PU is defined in test resources
			factory = Persistence.createEntityManagerFactory(RESOURCE_NAME, properties);
			eclipseFactory = (JpaEntityManagerFactory) factory;
			// make sure metamodel is initialized.
			factory.getMetamodel();
		}

		@Override
		public EntityManagerFactory getEMF() {
			return factory;
		}

		@Override
		public EntityManager createEM() {
			return factory.createEntityManager();
		}

		@Override
		public String getSchema() throws Exception {
			// do not touch database
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
			try {
				// critical.
				// refresh is needed
				eclipseFactory.refreshMetadata(properties);

				Map properties = new HashMap();
				/*
				 * properties.put("javax.persistence.database-product-name",
				 * "Postgresql");
				 * properties.put("javax.persistence.database-major-version",
				 * "9");
				 * properties.put("javax.persistence.database-minor-version",
				 * "");
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
				properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);

				Persistence.generateSchema(RESOURCE_NAME, properties);
				String schema = drop.toString() + "\n\n" + create.toString();
				return schema;
			} finally {
				properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
			}

		}

		// TODO
		@Override
		public void refresh(boolean changeSuper) {
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
			if (!changeSuper) {
				eclipseFactory.refreshMetadata(properties);
			} else {
				factory.close();
				dcl = new FleximsDynamicClassLoader(EntityManagerFactory.class.getClassLoader());
				properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
				factory = Persistence.createEntityManagerFactory(RESOURCE_NAME, properties);
			}
		}

		@Override
		public DynamicClassLoader getEMFClassLoader() {
			return dcl;
		}

	}

	public static class AppMock extends MockUp<App> {

		TestORMSetup testORMSetup;

		public AppMock(TestORMSetup t) {
			super();
			testORMSetup = t;
		}

		@Mock
		public EntityManagerFactoryProvider getPersistenceUnit() {
			return testORMSetup.emfp;
		}

		@Mock
		public EntityManager getEM() {
			EntityManager eManager = (EntityManager) ThreadContext.get("_entityManager");

			if (eManager == null) {
				eManager = testORMSetup.factory.createEntityManager();
				ThreadContext.put(App.EM_KEY, eManager);
			}
			return eManager;
		}

		@Mock
		public void fireEvent(Object event, Annotation... qualifers) {
			// do nothing
		}

	}

	@Before
	public void setup() {

		// start up mock
		new AppMock(this);
	}

	@After
	public void tearDown() {
		factory.close();
		ThreadContext.remove(App.EM_KEY);
		//remove database
		Util.removeUnitDB();
	}

	public EntityManagerFactoryProvider emfp;

	public void initEmf() {
		emfp = new EntityManagerFactoryProviderInternal();
	}

	public void refreshEmf() {
		emfp.refresh(false);
		ThreadContext.remove(App.EM_KEY);
	}
}
