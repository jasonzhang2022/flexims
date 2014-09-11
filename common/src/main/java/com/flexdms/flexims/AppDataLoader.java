package com.flexdms.flexims;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.apache.commons.io.IOUtils;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.util.Utils;

/**
 * Listen for app initialization event and execute sql file and xml entity file
 * <ul>
 * <li>execute META-INF/*_data.fxext.sql files in all jar. All sql files are
 * sorted alphabetically first.</li>
 * <li>execute META-INF/*_data.fxext.xml in all jar. All xml files are sorted
 * alphabetically first. The xml file follows the same format from <b>Export</b>
 * in the Report page.</li>
 * </ul>
 * 
 * @author jason.zhang
 * 
 */
public class AppDataLoader {

	public static final Logger LOGGER = Logger.getLogger(AppDataLoader.class.getCanonicalName());
	@Inject
	JaxbHelper jaxbHelper;

	public void loadData(@Observes AppInitializer.AppInitalizeContext ctx) throws SQLException, IOException {

		try (Connection connection = App.getDataSource().getConnection()) {
			if (connection.getAutoCommit()) {
				connection.setAutoCommit(false);
			}

			List<URL> resources = CPScanner.scanResources(new ResourceFilter().resourceName("META-INF").resourceName("*_data.fxext.sql"));
			resources = Utils.sortUrlsByPathLastSegment(resources);
			LOGGER.info("-----loading SQL statement. Ignore the warn if module is not loaded the first time");
			for (URL url : resources) {
				String sqlString = IOUtils.toString(url);
				String[] queries = sqlString.split(";");
				try {
					LOGGER.info("-----loading SQL statement from " + url.toExternalForm());
					Statement statement = connection.createStatement();

					for (String query : queries) {
						if (query.trim().length() > 0) {
							LOGGER.info("----EXECUTING SQL " + query);
							statement.execute(query);
						}
					}
					statement.close();
					connection.commit();
				} catch (SQLException e) {
					LOGGER.warning("Error in executing " + url.toExternalForm());
					try {
						connection.rollback();
					} catch (Exception e1) {
						// ignore.
					}

				}
			}
		}

		EntityManager eManager = ctx.em;
		List<URL> resources = CPScanner.scanResources(new ResourceFilter().resourceName("META-INF").resourceName("*_data.fxext.xml"));
		resources = Utils.sortUrlsByPathLastSegment(resources);
		LOGGER.info("-----loading Dynamic Entities. Ignore the warn if module is not loaded the first time");
		for (URL url : resources) {
			LOGGER.info("-----loading entities from " + url.toExternalForm());
			Entities entities = (Entities) jaxbHelper.fromXml(new InputStreamReader(url.openStream()), eManager);
			try {

				eManager.getTransaction().begin();
				for (Object obj : entities.getItems()) {
					eManager.persist(obj);
				}
				eManager.getTransaction().commit();
			} catch (Exception e) {
				LOGGER.warning("Error in executing " + url.toExternalForm());
				try {
					eManager.getTransaction().rollback();
				} catch (Exception e1) {
					// ignore.
				}

			}
		}

	}
}
