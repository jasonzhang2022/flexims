package com.flexdms.flexims.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.FetchGroup;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.sessions.Session;

import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

@SuppressWarnings({ "rawtypes", "unchecked" })
@QuerySubtype(ConditionQuery.TYPE_NAME)
public class ConditionQuery extends TypedQuery {

	public static final Logger LOGGER = Logger.getLogger(ConditionQuery.class.getName());
	public static final String TYPE_NAME = "DefaultTypedQuery";

	public static final String PROP_NAME_PROP_CONDITION = "Conditions";
	public static final int EXPECTED_EXP_NUM = 5;

	public static final String PREFIX = "param";

	String prefix = PREFIX;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String p) {
		prefix = p;
	}

	public ConditionQuery(FleximsDynamicEntityImpl entity) {
		super(entity);

	}

	// cache it.
	List<PropertyCondition> pcs = null;

	public List<PropertyCondition> getConditions() {
		if (pcs != null) {
			return pcs;
		}
		List<FleximsDynamicEntityImpl> conditions = entity.get(ConditionQuery.PROP_NAME_PROP_CONDITION);
		if (conditions == null) {
			pcs = new ArrayList<>();
			entity.set(PROP_NAME_PROP_CONDITION, pcs);
			return pcs;
		}
		pcs = new ArrayList<>(conditions.size());
		for (FleximsDynamicEntityImpl de : conditions) {
			pcs.add(new PropertyCondition(de, this));
		}
		return pcs;
	}

	// for query optimization
	List<Parameter> parameters;
	ReadAllQuery readAllQuery;

	public List<Parameter> getParameters() {
		return parameters;
	}

	public ReadAllQuery getReadAllQuery() {
		return readAllQuery;
	}

	public void setReadAllQuery(ReadAllQuery readAllQuery) {
		this.readAllQuery = readAllQuery;
	}

	private void rebuildQuery(EntityManager em) {
		List<Parameter> cachedsParams = parameters;
		try {
			built = false;
			buildQuery(em);
		} finally {
			parameters = cachedsParams;
		}
	}

	boolean built = false;

	public void buildQuery(EntityManager em) {
		if (built) {
			return;
		}

		String type = getQueryType();
		readAllQuery = QueryHelper.createReadAllQuery(type, em);

		ExpressionBuilder builder = readAllQuery.getExpressionBuilder();

		List<Expression> exps = new ArrayList<Expression>(EXPECTED_EXP_NUM);
		parameters = new ArrayList<Parameter>(EXPECTED_EXP_NUM);
		for (PropertyCondition pc : getConditions()) {
			Expression exp = pc.processProperty(builder, parameters);
			if (exp != null) {
				exps.add(exp);
			}
		}
		if (exps.size() == 1) {
			readAllQuery.setSelectionCriteria(exps.get(0));
		} else if (exps.size() > 1) {
			Expression finalExp = exps.get(0);
			for (int i = 1; i < exps.size(); i++) {
				finalExp = finalExp.and(exps.get(i));
			}
			readAllQuery.setSelectionCriteria(finalExp);
		}
		QueryContext queryContext = getQueryContext();

		boolean hasFetchGroup = false;
		if (queryContext != null) {
			setOrderFields(queryContext);
			hasFetchGroup = setFetchGroup(queryContext);
			setCachePolicy(hasFetchGroup);
		}
		for (Parameter p : parameters) {
			readAllQuery.addArgument(p.getName());
		}

		built = true;
	}

	public void setOrderFields(QueryContext queryContext) {
		if (queryContext.getOrderFields() == null) {
			return;
		}
		for (OrderField of : queryContext.getOrderFields()) {

			String[] paths = of.getPropname().split("\\.");
			ExpressionBuilder builder = readAllQuery.getExpressionBuilder();
			Expression expression = builder;
			for (String p : paths) {
				expression = expression.get(p);
			}
			if (of.getDirection() == OrderDirection.ASC) {

				expression = expression.ascending();

			} else {
				expression = expression.descending();
			}
			readAllQuery.addOrdering(expression);
		}
	}

	public boolean setFetchGroup(QueryContext queryContext) {
		if (queryContext.getProps() == null || queryContext.getProps().isEmpty()) {
			return false;
		}
		FetchGroup fetchGroup = new FetchGroup();
		for (String propName : queryContext.getProps()) {
			fetchGroup.addAttribute(propName);
		}
		// fetchGroup.setShouldLoad(true);
		readAllQuery.setFetchGroup(fetchGroup);

		ManagedType<?> mType = JpaMetamodelHelper.getManagedType(JpaMetamodelHelper.getMetamodel(), this.getQueryType());

		// ONLY conside one-level right now.
		List<Expression> joinsExpressions = new ArrayList<>();
		ExpressionBuilder builder = readAllQuery.getExpressionBuilder();
		for (String propName : queryContext.getProps()) {
			String[] path = propName.split("\\.");
			buildJoinExpression(mType, path[0], joinsExpressions, builder);
		}
		if (!joinsExpressions.isEmpty()) {
			readAllQuery.setJoinedAttributeExpressions(joinsExpressions);
		}
		return true;
	}

	public void buildJoinExpression(ManagedType<?> currentType, String prop, List<Expression> joinsExpressions, ExpressionBuilder builder) {
		Attribute<?, ?> attr = JpaMetamodelHelper.getAttribute(currentType, prop);
		switch (attr.getPersistentAttributeType()) {
		case BASIC:
		case EMBEDDED:
			break;
		case ELEMENT_COLLECTION:
			joinsExpressions.add(builder.anyOf(prop));
			break;
		case MANY_TO_MANY:
		case ONE_TO_MANY:
			joinsExpressions.add(builder.anyOf(prop));
			break;
		case MANY_TO_ONE:
		case ONE_TO_ONE:
			joinsExpressions.add(builder.get(prop));
			break;
		default:
			break;
		}

	}

	public void setCachePolicy(boolean hasFetchGroup) {
		boolean useCache = false;
		boolean setCache = false;
		if (!useCache) {
			readAllQuery.dontCheckCache();
		}
		if (!setCache) {
			readAllQuery.doNotCacheQueryResults();
			readAllQuery.dontMaintainCache();
		}
		// https://www.eclipse.org/forums/index.php/m/1346759/#msg_1346759
		// if this is not set, related still use cache.
		// readAllQuery.setCascadePolicy(ObjectLevelReadQuery.CascadeAllParts);

	}

	List result;
	int offset = -1;
	int limit = -1;

	@Override
	public List fetchAllResult(EntityManager em) {
		// return cache result;
		if (result != null && offset == -1 && limit == -1) {
			return result;
		}
		offset = -1;
		limit = -1;
		if (result != null) {
			// query is executed before.
			rebuildQuery(em);
		}
		execute(em);
		return result;
	}

	private void execute(EntityManager em) {
		Vector values = new Vector();
		for (Parameter p : parameters) {
			values.add(p.getValueForQuery());
		}
		JpaEntityManager jem = JpaHelper.getEntityManager(em);
		Session s = jem.getActiveSession();
		Object tmpresult = null;
		// Expression offset=new Expression();
		if (offset != -1) {
			readAllQuery.setFirstResult(offset);
		}
		if (limit != -1) {
			int maxrows = offset == -1 ? limit : offset + limit;
			// maxrows is the last row counting from offset zero.
			readAllQuery.setMaxRows(maxrows);
		}

		if (values.size() > 0) {
			tmpresult = s.executeQuery(readAllQuery, values);
		} else {
			tmpresult = s.executeQuery(readAllQuery);
		}
		result = (List) tmpresult;
	}

	private int resultCount = -1;

	@Override
	public int getResultCount(EntityManager em) {
		if (resultCount != -1) {
			return resultCount;
		}
		String type = getQueryType();
		ReportQuery rquery = QueryHelper.createReportQuery(type, em);
		ExpressionBuilder builder = rquery.getExpressionBuilder();
		rquery.addCount(FleximsDynamicEntityImpl.ID_NAME, builder.get(FleximsDynamicEntityImpl.ID_NAME).distinct());

		List<Expression> exps = new ArrayList<Expression>(EXPECTED_EXP_NUM);
		List<Parameter> parameters = new ArrayList<Parameter>(EXPECTED_EXP_NUM);
		for (PropertyCondition pc : getConditions()) {
			Expression exp = pc.processProperty(builder, parameters);
			if (exp != null) {
				exps.add(exp);
			}
		}
		if (exps.size() == 1) {
			rquery.setSelectionCriteria(exps.get(0));
		} else if (exps.size() > 1) {
			Expression finalExp = exps.get(0);
			for (int i = 1; i < exps.size(); i++) {
				finalExp = finalExp.and(exps.get(i));
			}
			rquery.setSelectionCriteria(finalExp);
		}
		for (Parameter p : parameters) {
			rquery.addArgument(p.getName());
		}

		Vector values = new Vector();
		// VERY IMPORTANT. the value is set from caller for read all query.
		for (Parameter p : this.parameters) {
			values.add(p.getValueForQuery());
		}
		JpaEntityManager jem = JpaHelper.getEntityManager(em);
		Session s = jem.getActiveSession();
		Object tmpresult = null;
		// Expression offset=new Expression();
		if (values.size() > 0) {

			tmpresult = s.executeQuery(rquery, values);
		} else {
			tmpresult = s.executeQuery(rquery);
		}
		Vector list = (Vector) tmpresult;
		ReportQueryResult ret = (ReportQueryResult) list.get(0);
		resultCount = ((Number) ret.getByIndex(0)).intValue();
		return resultCount;
	}

	@Override
	public List fetchPartialResult(int offset, int length, EntityManager em) {
		if (this.offset != offset || this.limit != length) {
			this.offset = offset;
			this.limit = length;
			if (result != null) {
				rebuildQuery(em);
			}
			execute(em);
		}
		return result;
	}

	@Override
	public List getResult() {
		return result;
	}

	@Override
	public void cleanCache(EntityManager em) {

		result = null;
		offset = -1;
		limit = -1;
		resultCount = -1;
		rebuildQuery(em);
	}

	@Override
	public boolean isValid() {

		String type = getQueryType();
		if (type == null) {
			return false;
		}
		ClassAccessor clz = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), type);
		if (clz == null) {
			return false;
		}
		for (PropertyCondition cond : getConditions()) {
			String propName = cond.getProp();
			if (JpaMapHelper.findProp(clz, propName) == null) {
				return false;
			}
		}
		return true;
	}

}
