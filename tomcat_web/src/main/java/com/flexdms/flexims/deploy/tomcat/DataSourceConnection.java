package com.flexdms.flexims.deploy.tomcat;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.sql.DataSource;

@ApplicationScoped
public class DataSourceConnection implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final Logger LOGGER = Logger.getLogger(DataSourceConnection.class.getCanonicalName());
	

	@Inject
	DataSource dataSource;

	@Produces
	@RequestScoped
	public Connection createFleximsConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public void closeFleximsConnection(@Disposes Connection con) {
		try {
			if (!con.isClosed()) {
				LOGGER.info("connection is closed");
				con.close();
			}
		} catch (SQLException e) {

		}
	}

}
