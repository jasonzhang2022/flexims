package com.flexdms.flexims.jpa;

import java.sql.Connection;
import java.sql.SQLData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;

import org.apache.poi.hssf.record.formula.functions.Var;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.dynamic.DynamicHelper;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.jpa.dynamic.JPADynamicHelper;
import org.eclipse.persistence.jpa.dynamic.JPADynamicTypeBuilder;
import org.eclipse.persistence.mappings.AggregateCollectionMapping;
import org.eclipse.persistence.mappings.AggregateMapping;
import org.eclipse.persistence.mappings.AggregateObjectMapping;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.mappings.OneToOneMapping;
import org.eclipse.persistence.platform.database.SQLAnywherePlatform;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.util.DBUtil;
import com.flexdms.flexims.util.Utils;

public final class JpaHelper {
	private JpaHelper() {

	}

	public static Class<? extends DynamicEntity> getEntityClass(EntityManager em, String name) {
		name = Utils.upperFirstLetter(name);
		DynamicHelper dhelper = new JPADynamicHelper(em);
		return dhelper.getType(name).getJavaClass();
	}

	public static FleximsDynamicEntityImpl createNewEntity(EntityManager em, String name) {
		Class<? extends DynamicEntity> clz = getEntityClass(em, name);
		try {
			return (FleximsDynamicEntityImpl) clz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<String> deletePropDBStructure(EntityManager em, String typename) throws SQLException {
		DynamicHelper dhelper = new JPADynamicHelper(em);
		ClassDescriptor cd = dhelper.getType(typename).getDescriptor();
		Connection con = em.unwrap(Connection.class);
		List<String> sqls = new ArrayList<>(3);
		for (DatabaseMapping mapping : cd.getMappings()) {
			if (mapping.isPrimaryKeyMapping()) {
				// leave at least column to the table.
				continue;
			}
			sqls.addAll(deletePropDBStructure(con, mapping));
		}
		if (!cd.isAggregateDescriptor()) {
			sqls.add("drop table " + DBUtil.normalizeTableName(cd.getTableName(), con));
		}
		return sqls;

	}

	public static List<String> deletePropDBStructure(EntityManager em, String typename, String propName) throws SQLException {
		DynamicHelper dhelper = new JPADynamicHelper(em);
		ClassDescriptor cd = dhelper.getType(typename).getDescriptor();
		Connection con = em.unwrap(Connection.class);
		DatabaseMapping mapping = cd.getMappingForAttributeName(propName);
		return deletePropDBStructure(con, mapping);
	}

	public static List<String> deletePropDBStructure(Connection con, DatabaseMapping mapping) throws SQLException {

		List<String> sqls = new ArrayList<>(3);
		if (mapping instanceof ForeignReferenceMapping) {
			if (((ForeignReferenceMapping) mapping).getMappedBy() != null) {
				return sqls;
			}
		}
		if (mapping.isReadOnly()) {
			return sqls;
		}
		
		if (mapping instanceof DirectToFieldMapping) {
			DirectToFieldMapping dMapping = (DirectToFieldMapping) mapping;
			DatabaseField field = dMapping.getField();
			sqls.add("alter table " + DBUtil.normalizeTableName(field.getTableName(), con) + " drop column " + field.getName());

		} else if (mapping instanceof DirectCollectionMapping) {
			DirectCollectionMapping dcMapping = (DirectCollectionMapping) mapping;
			sqls.add("drop table " + DBUtil.normalizeTableName(dcMapping.getReferenceTableName(), con));
		} else if (mapping instanceof AggregateObjectMapping) {
			AggregateObjectMapping dcMapping = (AggregateObjectMapping) mapping;
			for (DatabaseMapping mapping1 : dcMapping.getReferenceDescriptor().getMappings()) {
				sqls.addAll(deletePropDBStructure(con, mapping1));
			}
		} else if (mapping instanceof AggregateCollectionMapping) {
			AggregateCollectionMapping dMapping = (AggregateCollectionMapping) mapping;
			sqls.add("drop table " + DBUtil.normalizeTableName(dMapping.getReferenceDescriptor().getTableName(), con));
		} else if (mapping instanceof OneToOneMapping) {
			OneToOneMapping oneToOneMapping = (OneToOneMapping) mapping;

			for (Entry<DatabaseField, DatabaseField> fields : oneToOneMapping.getSourceToTargetKeyFields().entrySet()) {
				DatabaseField srcField = fields.getKey();
				DatabaseField destField = fields.getValue();
				String forgeinKey = DBUtil.getForeignKey(srcField.getTableName(), srcField.getName(), destField.getTableName(), destField.getName(),
						con);
				if (forgeinKey != null) {
					sqls.add("alter table " + DBUtil.normalizeTableName(srcField.getTableName(), con) + " drop constraint " + forgeinKey);
				}

			}
		} else if (mapping instanceof ManyToOneMapping) {
			ManyToOneMapping oneToOneMapping = (ManyToOneMapping) mapping;
			for (Entry<DatabaseField, DatabaseField> fields : oneToOneMapping.getSourceToTargetKeyFields().entrySet()) {
				DatabaseField srcField = fields.getKey();
				DatabaseField destField = fields.getValue();
				String forgeinKey = DBUtil.getForeignKey(srcField.getTableName(), srcField.getName(), destField.getTableName(), destField.getName(),
						con);
				sqls.add("alter table " + DBUtil.normalizeTableName(srcField.getTableName(), con) + " drop constraint " + forgeinKey);
			}

		} else if (mapping instanceof ManyToManyMapping) {
			ManyToManyMapping dmapping = (ManyToManyMapping) mapping;
			String relationTable = DBUtil.normalizeTableName(dmapping.getRelationTable().getName(), con);
			/*
			 * not needed List<String> sqlStrings =
			 * DBUtil.getForeignKeys(relationTable, con, true);
			 * sqls.addAll(sqlStrings);
			 */
			sqls.add("drop table " + relationTable);

		}

		return sqls;

	}

	@SuppressWarnings("rawtypes")
	private static Subgraph populateSubgraph(Subgraph subgraph, List<String> props) {
		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				subgraph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}
		for (String key : subkeysMap.keySet()) {
			Subgraph subgraph1 = subgraph.addSubgraph(key);
			populateSubgraph(subgraph1, subkeysMap.get(key));
		}
		return subgraph;

	}

	/**
	 * Not used right now. Using FetchGroup for property filtering
	 * 
	 * @param em
	 * @param typename
	 * @param props
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static EntityGraph buildEntityGraph(EntityManager em, String typename, List<String> props) {
		EntityGraph graph = em.createEntityGraph(getEntityClass(em, typename));
		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				graph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}

		for (String key : subkeysMap.keySet()) {
			Subgraph subgraph = graph.addSubgraph(key);
			populateSubgraph(subgraph, subkeysMap.get(key));
		}
		return graph;
	}

}
