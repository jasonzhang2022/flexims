package com.flexdms.flexims.unit.rs.report.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.query.PropertyCondition;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;

public class QueryUtil {

	public static int index = 0;

	public static void saveReport(FxReportWrapper report, EntityManager em) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		saveQueryInternal((ConditionQuery) report.getQuery(), em);
		em.persist(report.getEntity());
		tx.commit();
	}

	public static void saveQuery(ConditionQuery query, EntityManager em) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		saveQueryInternal(query, em);
		tx.commit();
	}

	public static void saveQueryInternal(ConditionQuery query, EntityManager em) {
		List<FleximsDynamicEntityImpl> conds = new ArrayList<>(3);
		for (PropertyCondition pc : query.getConditions()) {
			conds.add(pc.getEntity());
		}
		query.getEntity().set(ConditionQuery.PROP_NAME_PROP_CONDITION, conds);
		em.persist(query.getEntity());
	}

	public static FxReportWrapper prepareSimpleReport(ConditionQuery query, EntityManager em, String orderProp, String... props) {
		FleximsDynamicEntityImpl entityImpl = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);

		FxReportWrapper report = new FxReportWrapper();

		report.setEntity(entityImpl).setName("testreport " + QueryUtil.index++).setQuery(query).setProperties(props);
		if (orderProp != null) {
			List<OrderField> os = new ArrayList<>(1);
			os.add(new OrderField(orderProp, OrderDirection.ASC));
			report.setOrderBy(os);
		}
		return report;
	}

	public static ConditionQuery createSimpleQuery(String qType, String propName, Operator op, EntityManager em) {
		FleximsDynamicEntityImpl entity = JpaHelper.createNewEntity(em, ConditionQuery.TYPE_NAME);
		ConditionQuery query = new ConditionQuery(entity);
		query.setQueryType(qType);
		query.setQueryName("TestGenerated " + index++);

		List<PropertyCondition> conditions = query.getConditions();

		entity = JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME);
		PropertyCondition pc = new PropertyCondition(entity, query);
		pc.setProp(propName);
		pc.setOperator(op);

		conditions.add(pc);
		return query;
	}

	public static void deleteData(String type, EntityManager em) {
		String qlString = "delete from " + type;
		javax.persistence.Query query = em.createQuery(qlString);
		query.executeUpdate();

	}

	public static void deleteByQuery(String type, EntityManager em) {
		String qlString = "select m from " + type + " m";
		javax.persistence.Query query = em.createQuery(qlString);
		List list = query.getResultList();
		for (Object obj : list) {
			em.remove(obj);
		}

	}

	public static void deleteAllQuery(EntityManager em) {
		javax.persistence.Query query = em.createQuery("delete from DefaultTypedQuery where Name like :name");
		query.setParameter("name", "TestGenerated %");
		query.executeUpdate();

	}

	public static void deleteAllReport(EntityManager em) {
		javax.persistence.Query query = em.createQuery("delete from FxReport where Name like :name");
		query.setParameter("name", "testreport %");
		query.executeUpdate();
	}
}
