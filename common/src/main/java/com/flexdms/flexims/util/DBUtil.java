package com.flexdms.flexims.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DBUtil {

	private DBUtil() {
	}
	public static Map<String, Set<String>> collectTableUniqieKeys(String table, Connection con) throws SQLException {
		table = normalizeTableName(table, con);
		Map<String, Set<String>> ukeys = new HashMap<String, Set<String>>();
		ResultSet rs = con.getMetaData().getIndexInfo(null, null, table, true, false);
		while (rs.next()) {
			String idxName = rs.getString("INDEX_NAME");
			String colName = rs.getString("COLUMN_NAME");
			if (ukeys.containsKey(idxName)) {
				ukeys.get(idxName).add(colName);
			} else {
				Set<String> columns = new HashSet<String>();
				columns.add(colName);
				ukeys.put(idxName, columns);
			}
		}
		return ukeys;
	}

	public static List<String> getTables(Connection con) throws SQLException {
		String[] types = { "TABLE" };
		ResultSet rs = con.getMetaData().getTables(null, null, "%", types);
		List<String> tables = new ArrayList<String>();
		while (rs.next()) {
			tables.add(rs.getString("TABLE_NAME"));
		}
		rs.close();
		return tables;

	}

	// derby has uppercase table name,
	// postgresal has lowercase
	public static String normalizeTableName(String table, Connection con) throws SQLException {
		for (String t : getTables(con)) {
			if (t.equalsIgnoreCase(table)) {
				return t;
			}
		}
		return table;
	}

	public static String getForeignKey(String fromTable, String fromKey, String toTable, String toKey, Connection con) throws SQLException {
		fromTable = normalizeTableName(fromTable, con);
		toTable = normalizeTableName(toTable, con);

		String ret = null;
		ResultSet rs = con.getMetaData().getCrossReference(null, null, toTable, null, null, fromTable);
	

		while (rs.next()) {
			String pcolumn = rs.getString("PKCOLUMN_NAME");
			String fcolumn = rs.getString("FKCOLUMN_NAME");
			if (fromKey.equalsIgnoreCase(fcolumn) && toKey.equalsIgnoreCase(pcolumn)) {
				ret = rs.getString("FK_NAME");
			}

		}
		rs.close();
		return ret;

	}

	public static void dropForeignKeys(String toTable, Connection con, boolean force) throws SQLException {
		toTable = normalizeTableName(toTable, con);
		List<String> statements = new LinkedList<String>();
		ResultSet rs = con.getMetaData().getExportedKeys(null, null, toTable);
		while (rs.next()) {
			String tablename = rs.getString("FKTABLE_NAME");
			String fkname = rs.getString("FK_NAME");
			if (fkname != null) {
				statements.add("alter table " + tablename + " drop constraint " + fkname);
			}
		}
		rs.close();
		for (String sql : statements) {
			try {
				con.createStatement().execute(sql);
			} catch (SQLException e) {
				if (!force) {
					throw e;
				} 
			}
		}

	}

	public static boolean checkTableExist(String tableName, Connection con) throws SQLException {

		for (String t : getTables(con)) {
			if (t.equalsIgnoreCase(tableName)) {
				return true;
			}
		}
		return false;
	}

	public static void dropTable(String tableName, Connection con) throws SQLException {
		tableName = normalizeTableName(tableName, con);
		con.createStatement().execute("drop table " + tableName);

	}

	public static List<String> getColumns(String table, Connection con) throws SQLException {
		List<String> columns = new ArrayList<String>();

		// in some system, the table name is lower-case
		// in some system, the table name is upper case.
		// we find out the case first.
		DatabaseMetaData metaData = con.getMetaData();
		table = normalizeTableName(table, con);

		ResultSet rs = metaData.getColumns(null, null, table, null);
		try {
			while (rs.next()) {
				String column = rs.getString("COLUMN_NAME").toUpperCase();
				columns.add(column.toUpperCase());
			}
		} finally {
			rs.close();
		}
		return columns;

	}

	public static boolean columnExist(String table, String column, Connection con) throws SQLException {
		return getColumns(table, con).contains(column.toUpperCase());
	}
}
