package com.flexdms.flexims.accesscontrol;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import com.flexdms.flexims.AppInitializer;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.util.Utils;

public class ACLInitializer {
	public static final Logger LOGGER = Logger.getLogger(ACLInitializer.class.getCanonicalName());
	public static String securityUnit = "fxsecurity";

	public void init(@Observes AppInitializer.AppInitalizeContext ctx) throws SQLException, IOException {
		ACLHelper.emf = Persistence.createEntityManagerFactory(securityUnit);
		setAvailableActions();

		EntityManager securityEntityManager = ACLHelper.emf.createEntityManager();
		loadPredefinedAcls(securityEntityManager);
		loadAllTypeACL(securityEntityManager);
		securityEntityManager.close();

	}

	public static void loadPredefinedAcls(EntityManager securityEntityManager) throws IOException {

		List<URL> resources = CPScanner.scanResources(new ResourceFilter().resourceName("META-INF").resourceName("*_acl.fxext.sql"));
		resources = Utils.sortUrlsByPath(resources);

		for (URL url : resources) {
			LOGGER.info("-----loading SQL statement. Ignore the warn if module is not loaded the first time " + url.toExternalForm());
			String sqlString = IOUtils.toString(url);
			String[] queries = sqlString.split(";");
			try {
				securityEntityManager.getTransaction().begin();
				Connection connection = securityEntityManager.unwrap(Connection.class);
				Statement statement = connection.createStatement();

				for (String query : queries) {
					if (query.trim().length() > 0) {
						LOGGER.info("----EXECUTING SQL " + query);
						statement.execute(query);
					}
				}
				statement.close();
				securityEntityManager.getTransaction().commit();
			} catch (SQLException e) {
				LOGGER.warning("Error in executing " + url.toExternalForm() + " " + e.getMessage() + " "
						+ (e.getNextException() != null ? e.getNextException().getMessage() : ""));
				try {
					securityEntityManager.getTransaction().rollback();
				} catch (Exception e1) {
					// ignore.
				}

			}
		}
	}

	public void setAvailableActions() {
		ACLHelper.actions = BeanProvider.getContextualReferences(Action.class, false);

	}

	public void loadAllTypeACL(EntityManager em) {
		Query query = em.createQuery("SELECT tacl FROM TypeACL tacl");
		for (Object object : query.getResultList()) {
			TypeACL tacl = (TypeACL) object;
			ACLHelper.typeacls.put(tacl.getTypeid(), tacl);
		}
	}
}
