package com.flexdms.flexims.unit;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import mockit.Mock;
import mockit.MockUp;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jpa.JpaEntityManagerFactory;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicClassLoader;
import com.flexdms.flexims.util.ThreadContext;

public class TestOXMSetup {

	public static String RESOURCE_NAME = "test";
	public static Map<String, Object> properties;
	public static DynamicClassLoader dcl;
	public static EntityManagerFactory factory;
	public static JpaEntityManagerFactory eclipseFactory;
	public static String persistenceXml = "persistence.xml";

	public static class EntityManagerFactoryProviderInternal implements EntityManagerFactoryProvider {

		public static void setup() {
			Util.removeUnitDB();
			properties = new HashMap<String, Object>();

			dcl = new FleximsDynamicClassLoader(EntityManagerFactory.class.getClassLoader());
			properties.put(PersistenceUnitProperties.CLASSLOADER, dcl);
			properties.put(PersistenceUnitProperties.WEAVING, "static");
			properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/" + persistenceXml);

			// properties.put(PersistenceUnitProperties.DDL_GENERATION,
			// PersistenceUnitProperties.DROP_AND_CREATE);
			properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
			// properties.put("eclipselink.orm.validate.schema", true);
			// properties.put("javax.persistence.schema-generation.create-database-schemas",
			// true);
			properties.put(PersistenceUnitProperties.METADATA_SOURCE, DynamicMetaSource.class.getCanonicalName());

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
				refresh(false);

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
			eclipseFactory.refreshMetadata(null);

		}

		@Override
		public DynamicClassLoader getEMFClassLoader() {
			return dcl;
		}

	}

	public static EntityManagerFactoryProvider emfp = new EntityManagerFactoryProviderInternal();

	public static class AppMock extends MockUp<App> {
		Connection connection;

		public AppMock(Connection connection2) {
			super();
			this.connection = connection2;
		}

		@Mock
		public EntityManagerFactoryProvider getPersistenceUnit() {
			return emfp;
		}

		@Mock
		public EntityManager getEM() {
			EntityManager eManager = (EntityManager) ThreadContext.get("_entityManager");

			if (eManager == null) {
				eManager = factory.createEntityManager();
				ThreadContext.put(App.EM_KEY, eManager);
			}
			return eManager;
		}

		@Mock
		public void fireEvent(Object event, Annotation... qualifers) {
			// do nothing
		}

		@Mock
		public DataSource getDataSource() {
			return new MockUp<DataSource>() {
				@Mock
				Connection getConnection() throws SQLException {
					return connection;
				}
			}.getMockInstance();
		}

	}

}
