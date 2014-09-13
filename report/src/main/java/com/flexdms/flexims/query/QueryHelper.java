package com.flexdms.flexims.query;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.CacheRetrieveMode;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.SingularAttribute;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ClassFilter;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
@SuppressWarnings("rawtypes")
public final class QueryHelper {

	private QueryHelper() {
	}
	public static List<Class<?>> queryClasses;
	static {
		queryClasses = CPScanner.scanClasses(new ClassFilter().packageName("com.flexdms.flexims.query*").annotation(QuerySubtype.class));
	}

	public static ReadAllQuery createReadAllQuery(String type, EntityManager em) {
		return new ReadAllQuery(JpaHelper.getEntityClass(em, type));
	}

	public static ReportQuery createReportQuery(String type, EntityManager em) {
		ExpressionBuilder builder = new ExpressionBuilder();
		return new ReportQuery(JpaHelper.getEntityClass(em, type), builder);
	}

	@SuppressWarnings("unchecked")
	public static Class<? extends TypedQuery> findQueryClass(String name) {
		for (Class<?> clz : queryClasses) {
			QuerySubtype qSubtype = clz.getAnnotation(QuerySubtype.class);
			if (qSubtype.value().equalsIgnoreCase(name)) {
				return (Class<? extends TypedQuery>) clz;
			}
		}
		return null;
	}

	public static TypedQuery wrapQueryEntity(FleximsDynamicEntityImpl entity) {
		if (entity == null) {
			return null;
		}
		String entityClass = entity.getClass().getSimpleName();
		Class<? extends TypedQuery> queryClass = findQueryClass(entityClass);
		try {
			return queryClass.getConstructor(FleximsDynamicEntityImpl.class).newInstance(entity);
		} catch (Exception e) {
			throw new QueryClassException(e);
		}
	}

	public static TypedQuery loadQuery(String name) {
		EntityManager em = App.getEM();
		Map<String, Object> params = new HashMap<>(2);
		params.put(TypedQuery.PROP_NAME_NAME, name);
		TypedQuery query = createSimpleEqualQuery(TypedQuery.TYPE_NAME, params);
		query.buildQuery(em);
		List list = query.fetchAllResult(em);
		if (list == null || list.isEmpty()) {
			return null;
		}
		FleximsDynamicEntityImpl entity = (FleximsDynamicEntityImpl) list.get(0);
		return wrapQueryEntity(entity);
	}

	
	public static List loadQueriesForType(String typename) {
		EntityManager em = App.getEM();
		Map<String, Object> params = new HashMap<>(2);
		params.put(TypedQuery.PROP_NAME_TYPE, typename);
		TypedQuery query = createSimpleEqualQuery(TypedQuery.TYPE_NAME, params);
		query.buildQuery(em);
		List list = query.fetchAllResult(em);

		return list;
	}

	public static List loadAll(String typename) {
		EntityManager em = App.getEM();
		Query query=em.createQuery("select t from "+typename+" t");
		List list = query.getResultList();

		return list;
	}
	
	public static FleximsDynamicEntityImpl createAllQuery(String typename) {
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl dEntityImpl = JpaHelper.createNewEntity(em, ConditionQuery.TYPE_NAME);
		dEntityImpl.set(TypedQuery.PROP_NAME_NAME, "ALL " + typename);
		dEntityImpl.set(TypedQuery.PROP_NAME_TYPE, typename);
		em.persist(dEntityImpl);
		return dEntityImpl;
	}

	public static FleximsDynamicEntityImpl createAllReport(FleximsDynamicEntityImpl allQuery) {
		String typename = allQuery.get(TypedQuery.PROP_NAME_TYPE);
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl dEntityImpl = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		FxReportWrapper wrapper = new FxReportWrapper();
		wrapper.setEntity(dEntityImpl);
		wrapper.setName("ALL " + typename);
		wrapper.setQuery(allQuery);
		LinkedList<String> props = new LinkedList<>();
		ManagedType<?> t = JpaMetamodelHelper.getManagedType(JpaMetamodelHelper.getMetamodel(), typename);
		for (Attribute<?, ?> attr : t.getAttributes()) {
			if (attr.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC) {
				SingularAttribute singularAttribute = (SingularAttribute) attr;
				if (singularAttribute.isId() || singularAttribute.isVersion()) {
					continue;
				}
				if (JpaMapHelper.isSummaryProp(typename, attr.getName())) {
					props.addFirst(attr.getName());
				} else {
					props.add(attr.getName());
				}
			}
		}
		if (props.isEmpty()) {
			props.add(FleximsDynamicEntityImpl.ID_NAME);
		}
		wrapper.setProperties(props);
		em.persist(wrapper.getEntity());
		return dEntityImpl;
	}

	public static List loadReportForType(String typename) {
		EntityManager em = App.getEM();
		String jpqlString = "select  r from " + FxReportWrapper.TYPE_NAME + " r where r.Query.TargetedType=:TargetedType";
		Query query = em.createQuery(jpqlString);
		query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
		query.setParameter("TargetedType", typename);
		return query.getResultList();
	}

	public static TypedQuery loadQuery(String type, String name) {
		EntityManager em = App.getEM();
		Map<String, Object> params = new HashMap<>(2);
		params.put(TypedQuery.PROP_NAME_TYPE, type);
		params.put(TypedQuery.PROP_NAME_NAME, name);
		TypedQuery query = createSimpleEqualQuery(TypedQuery.TYPE_NAME, params);
		query.buildQuery(em);
		List list = query.fetchAllResult(em);
		if (list == null || list.isEmpty()) {
			return null;
		}
		FleximsDynamicEntityImpl entity = (FleximsDynamicEntityImpl) list.get(0);
		return wrapQueryEntity(entity);
	}

	public static TypedQuery loadQuery(long id) {
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl dEntityImpl = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, TypedQuery.PROP_NAME_TYPE), id);
		if (dEntityImpl != null) {
			return wrapQueryEntity(dEntityImpl);
		}
		return null;

	}

	public static ConditionQuery createSimpleQuery(String qType, String propName, Operator op, EntityManager em) {
		FleximsDynamicEntityImpl entity = JpaHelper.createNewEntity(em, ConditionQuery.TYPE_NAME);
		ConditionQuery query = new ConditionQuery(entity);
		query.setQueryType(qType);

		List<PropertyCondition> conditions = query.getConditions();

		entity = JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME);
		PropertyCondition pc = new PropertyCondition(entity, query);
		pc.setProp(propName);
		pc.setOperator(op);

		conditions.add(pc);
		return query;
	}

	public static TypedQuery createSimpleEqualQuery(String type, String propName, Object propValue) {

		Map<String, Object> params = new HashMap<String, Object>(1);
		params.put(propName, propValue);
		return createSimpleEqualQuery(type, params);
	}

	public static TypedQuery createSimpleEqualQuery(String type, Map<String, Object> params) {
		EntityManager em = App.getEM();
		ConditionQuery query = new ConditionQuery(JpaHelper.createNewEntity(em, ConditionQuery.TYPE_NAME));
		query.setQueryType(type);
		query.setQueryName("Generated " + type);

		List<PropertyCondition> conditions = query.getConditions();

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			PropertyCondition pc = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
			pc.setProp(entry.getKey());
			javax.persistence.metamodel.Attribute<?, ?> attribute = JpaMetamodelHelper.getAttribute(type, entry.getKey());
			if (attribute.isCollection()) {
				if (entry.getValue() == null) {
					pc.setOperator(Operator.empty);
				} else {
					pc.setOperator(Operator.eq);
				}
			} else {
				if (entry.getValue() == null) {
					pc.setOperator(Operator.isnull);
				} else {
					pc.setOperator(Operator.eq);
				}
			}
			conditions.add(pc);
		}

		query.buildQuery(em);
		for (Parameter parameter : query.getParameters()) {
			ConditionQueryParameter param = (ConditionQueryParameter) parameter;
			PropertyCondition cond = param.getCondition();
			String prop = cond.getProp();
			param.setValue(params.get(prop));
		}

		return query;
	}
}
