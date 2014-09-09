package com.flexdms.flexims.unit;

import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.derby.drda.NetworkServerControl;

public class Util {

	public static Connection getConnection() throws SQLException {
		return getDerbyUnitConnection();
	}

	public static Connection getPostgresConnection() throws SQLException {
		String url = "jdbc:postgresql://192.168.231.10:5432/flexims";
		Properties props = new Properties();
		props.setProperty("user", "flexims");
		props.setProperty("password", "123456");
		return DriverManager.getConnection(url, props);

	}

	public static Connection getDerbyUnitConnection() throws SQLException {
		String url = "jdbc:derby:memory:flexims;create=true";
		Properties props = new Properties();
		props.setProperty("user", "flexims");
		props.setProperty("password", "123456");
		return DriverManager.getConnection(url, props);

	}

	public static void removeUnitDB() {
		try {
			// shut down embedded database connection if there is one.
			DriverManager.getConnection("jdbc:derby:memory:flexims;drop=true", "flexims", "123456");
		} catch (SQLException e) {
			// ignore
		}
//		// // avoid table created from other test
//		// FileUtils.deleteQuietly(new File("target/flexims"));
//		if (new File("target/flexims").exists()) {
//			throw new RuntimeException("database exists. It can not be deleted");
//		}
	}

	public static Connection getDerbyRSConnection() throws SQLException {
		// org.apache.derby.jdbc.ClientDriver.class.newInstance();
		String url = "jdbc:derby://localhost:1527/memory:flexims;create=true";
		Properties props = new Properties();
		props.setProperty("user", "flexims");
		props.setProperty("password", "123456");
		return DriverManager.getConnection(url, props);

	}

	public static void removeRSDB() {
		try {
			// shut down embedded database connection if there is one.
			DriverManager.getConnection("jdbc:derby://localhost:1527/memory:flexims;shutdown=true", "flexims", "123456");
		} catch (SQLException e) {
			// ignore
		}
		// // avoid table created from other test
		// FileUtils.deleteQuietly(new File("target/flexims"));
		// if (new File("target/flexims").exists()) {
		// throw new RuntimeException("database exists. It can not be deleted");
		// }
		System.out.println("---------------removed RS test database");
	}

	static NetworkServerControl server;

	public static void bootRsNetworkDB() {

		try {
			if (server != null) {
				server.shutdown();
			}
			server = new NetworkServerControl();
			server.start(new PrintWriter(System.out));
			System.out.println("---------------Start Derby network server");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void shutdownRsNetworkDB() {

		try {

			if (server != null) {
				server.shutdown();
				System.out.println("---------------Shutdown Derby network server");
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String args[]) {
		bootRsNetworkDB();
		System.out.println("booted");
	}
}
