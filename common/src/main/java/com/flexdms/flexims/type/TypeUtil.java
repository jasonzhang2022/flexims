package com.flexdms.flexims.type;

import java.sql.Connection;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import com.flexdms.flexims.App;
import com.flexdms.flexims.util.DBUtil;

public final class TypeUtil {
	private TypeUtil() {
	}
	public static final String[] SQL_KEYWORDS = { "ABSOLUTE", "ACTION", "ADD", "AFTER", "ALL", "ALLOCATE", "ALTER", "AND", "ANY", "ARE", "ARRAY",
			"AS", "ASC", "ASENSITIVE", "ASSERTION", "ASYMMETRIC", "AT", "ATOMIC", "AUTHORIZATION", "AVG", "BEFORE", "BEGIN", "BETWEEN", "BIGINT",
			"BINARY", "BIT", "BIT_LENGTH", "BLOB", "BOOLEAN", "BOTH", "BREADTH", "BY", "CALL", "CALLED", "CASCADE", "CASCADED", "CASE", "CAST",
			"CATALOG", "CHAR", "CHARACTER", "CHARACTER_LENGTH", "CHAR_LENGTH", "CHECK", "CLOB", "CLOSE", "COALESCE", "COLLATE", "COLLATION",
			"COLUMN", "COMMIT", "CONDITION", "CONNECT", "CONNECTION", "CONSTRAINT", "CONSTRAINTS", "CONSTRUCTOR", "CONTAINS", "CONTINUE", "CONVERT",
			"CORRESPONDING", "COUNT", "CREATE", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_DEFAULT_TRANSFORM_GROUP", "CURRENT_PATH",
			"CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_TRANSFORM_GROUP_FOR_TYPE", "CURRENT_USER", "CURSOR", "CYCLE", "DATA",
			"DATE", "DAY", "DEALLOCATE", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DEPTH", "DEREF", "DESC",
			"DESCRIBE", "DESCRIPTOR", "DETERMINISTIC", "DIAGNOSTICS", "DISCONNECT", "DISTINCT", "DO", "DOMAIN", "DOUBLE", "DROP", "DYNAMIC", "EACH",
			"ELEMENT", "ELSE", "ELSEIF", "END", "EQUALS", "ESCAPE", "EXCEPT", "EXCEPTION", "EXEC", "EXECUTE", "EXISTS", "EXIT", "EXTERNAL",
			"EXTRACT", "FALSE", "FETCH", "FILTER", "FIRST", "FLOAT", "FOR", "FOREIGN", "FOUND", "FREE", "FROM", "FULL", "FUNCTION", "GENERAL", "GET",
			"GLOBAL", "GO", "GOTO", "GRANT", "GROUP", "GROUPING", "HANDLER", "HAVING", "HOLD", "HOUR", "IDENTITY", "IF", "IMMEDIATE", "IN",
			"INDICATOR", "INITIALLY", "INNER", "INOUT", "INPUT", "INSENSITIVE", "INSERT", "INT", "INTEGER", "INTERSECT", "INTERVAL", "INTO", "IS",
			"ISOLATION", "ITERATE", "JOIN", "KEY", "LANGUAGE", "LARGE", "LAST", "LATERAL", "LEADING", "LEAVE", "LEFT", "LEVEL", "LIKE", "LOCAL",
			"LOCALTIME", "LOCALTIMESTAMP", "LOCATOR", "LOOP", "LOWER", "MAP", "MATCH", "MAX", "MEMBER", "MERGE", "METHOD", "MIN", "MINUTE",
			"MODIFIES", "MODULE", "MONTH", "MULTISET", "NAMES", "NATIONAL", "NATURAL", "NCHAR", "NCLOB", "NEW", "NEXT", "NO", "NONE", "NOT", "NULL",
			"NULLIF", "NUMERIC", "OBJECT", "OCTET_LENGTH", "OF", "OLD", "ON", "ONLY", "OPEN", "OPTION", "OR", "ORDER", "ORDINALITY", "OUT", "OUTER",
			"OUTPUT", "OVER", "OVERLAPS", "PAD", "PARAMETER", "PARTIAL", "PARTITION", "PATH", "POSITION", "PRECISION", "PREPARE", "PRESERVE",
			"PRIMARY", "PRIOR", "PRIVILEGES", "PROCEDURE", "PUBLIC", "RANGE", "READ", "READS", "REAL", "RECURSIVE", "REF", "REFERENCES",
			"REFERENCING", "RELATIVE", "RELEASE", "REPEAT", "RESIGNAL", "RESTRICT", "RESULT", "RETURN", "RETURNS", "REVOKE", "RIGHT", "ROLE",
			"ROLLBACK", "ROLLUP", "ROUTINE", "ROW", "ROWS", "SAVEPOINT", "SCHEMA", "SCOPE", "SCROLL", "SEARCH", "SECOND", "SECTION", "SELECT",
			"SENSITIVE", "SESSION", "SESSION_USER", "SET", "SETS", "SIGNAL", "SIMILAR", "SIZE", "SMALLINT", "SOME", "SPACE", "SPECIFIC",
			"SPECIFICTYPE", "SQL", "SQLCODE", "SQLERROR", "SQLEXCEPTION", "SQLSTATE", "SQLWARNING", "START", "STATE", "STATIC", "SUBMULTISET",
			"SUBSTRING", "SUM", "SYMMETRIC", "SYSTEM", "SYSTEM_USER", "TABLE", "TABLESAMPLE", "TEMPORARY", "THEN", "TIME", "TIMESTAMP",
			"TIMEZONE_HOUR", "TIMEZONE_MINUTE", "TO", "TRAILING", "TRANSACTION", "TRANSLATE", "TRANSLATION", "TREAT", "TRIGGER", "TRIM", "TRUE",
			"UNDER", "UNDO", "UNION", "UNIQUE", "UNKNOWN", "UNNEST", "UNTIL", "UPDATE", "UPPER", "USAGE", "USER", "USING", "VALUE", "VALUES",
			"VARCHAR", "VARYING", "VIEW", "WHEN", "WHENEVER", "WHERE", "WHILE", "WINDOW", "WITH", "WITHIN", "WITHOUT", "WORK", "WRITE", "YEAR",
			"ZONE" };
	public static final String[] JAVA_KEYWORDS = { "abstract", "continue", "for", "new", "switch", "assert", "default", "if", "package",
			"synchronized", "boolean", "do", "goto", "private", "this", "break", "double", "implements", "protected", "throw", "byte", "else",
			"import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int", "short", "try", "char",
			"final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const", "float", "native", "super", "while" };

	public static boolean isKeyWord(String word) {
		if (word == null) {
			return false;
		}
		String uw = word.toUpperCase();
		String lw = word.toLowerCase();
		for (String w : SQL_KEYWORDS) {
			if (uw.equals(w)) {
				return true;
			}
		}
		for (String w : JAVA_KEYWORDS) {
			if (lw.equals(w)) {
				return true;
			}
		}
		return false;
	}

	public static String validateTypeName(String typename, Connection con, boolean checkTable) throws Exception {
		typename = typename.trim();
		if (!typename.matches("^[a-zA-Z][0-9a-zA-Z_-]+$")) {
			return "unsafe character is found.";

		}
		if (isKeyWord(typename)) {
			return "is a reserved word. Please choose another name";
		}
		Metamodel metamodel = App.getPersistenceUnit().getEMF().getMetamodel();
		for (ManagedType<?> mType : metamodel.getManagedTypes()) {
			if (mType.getJavaType().getSimpleName().equalsIgnoreCase(typename)) {
				return "type with the same name already exists. Please use another name";
			}
		}

		if (checkTable && DBUtil.checkTableExist(typename, con)) {
			return "Underlying table for this type already exists. Please use another name";
		}

		return null;
	}

	public static String validatePropName(String typeName, String propName, Connection con, boolean checkTable) throws Exception {
		if (!propName.matches("^[a-zA-Z][0-9a-zA-Z_-]*[a-zA-Z0-9]$")) {
			return "unsafe character is found.";

		}
		if (isKeyWord(propName)) {
			return "is a reserved word. Please choose another name";
		}
		Metamodel metamodel = App.getPersistenceUnit().getEMF().getMetamodel();
		ManagedType<?> managedType = null;
		for (ManagedType<?> mType : metamodel.getManagedTypes()) {
			if (mType.getJavaType().getSimpleName().equalsIgnoreCase(typeName)) {
				managedType = mType;
				break;
			}
		}
		for (Attribute<?, ?> da : managedType.getDeclaredAttributes()) {
			if (da.getName().equalsIgnoreCase(propName)) {
				return "property with the same name already exists. Please use another name";
			}
		}

		if (checkTable && DBUtil.columnExist(typeName, propName, con)) {
			return "Underlying table for this type already has a column with this name. Please use another name";
		}
		return null;
	}

}
