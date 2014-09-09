package com.flexdms.flexims.query;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.queries.ReportQuery;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.util.TimeUnit;

public class PropertyCondition {

	FleximsDynamicEntityImpl entity;
	ConditionQuery query;

	public PropertyCondition(FleximsDynamicEntityImpl entityImpl, ConditionQuery query) {
		super();
		this.entity = entityImpl;
		this.query = query;
	}

	public static final String TYPE_NAME = "PropertyCondition";

	public static final String PROP_NAME_PROP_NAME = "Property";
	public static final String PROP_NAME_OP = "Operator";
	public static final String PROP_NAME_FIRSTVALUE = "FirstValue";
	public static final String PROP_NAME_SECONDVALUE = "SecondValue";
	public static final String PROP_NAME_DESCRIPTION = "Description";
	public static final String PROP_NAME_IGNORECASE = "IgnoreCase";
	public static final String PROP_NAME_RELATIVESTARTDATE = "RelativeStartDate";
	public static final String PROP_NAME_RELATIVEENDDATE = "RelativeEndDate";
	public static final String PROP_NAME_RELATIVESTARTUNIT = "RelativeStartUnit";
	public static final String PROP_NAME_RELATIVEENDUNIT = "RelativeEndUnit";
	public static final String PROP_NAME_TRACEDOWN = "TraceDown";
	public static final String PROP_NAME_WHOLE_TIME = "WholeTime";
	public static final String PROP_NAME_COLLECTION_MODE = "CollectionMode";
	public static final long TYPEID = 200L;

	public ConditionQuery getQuery() {
		return query;
	}

	public String getCollectionMode() {
		String cmode = (String) entity.get(PROP_NAME_COLLECTION_MODE);
		if (cmode == null) {
			return "Some";
		}
		return cmode;
	}

	public boolean isAllColllectionMode() {
		return getCollectionMode().equals("All");
	}

	public void setAllColllectionMode() {
		entity.set(PROP_NAME_COLLECTION_MODE, "All");
	}

	public boolean isNoneColllectionMode() {
		return getCollectionMode().equals("None");
	}

	public void setNoneColllectionMode() {
		entity.set(PROP_NAME_COLLECTION_MODE, "None");
	}

	public boolean isSomeColllectionMode() {
		return getCollectionMode().equals("Some");
	}

	public void setSomeColllectionMode() {
		entity.set(PROP_NAME_COLLECTION_MODE, "Some");
	}

	public TimeUnit getStartUnit() {
		String unitValue = (String) entity.get(PROP_NAME_RELATIVESTARTUNIT);
		if (unitValue == null) {
			return null;
		}
		return TimeUnit.valueOf(unitValue);
	}

	public void setStartUnit(TimeUnit tu) {
		entity.set(PROP_NAME_RELATIVESTARTUNIT, tu.name());
	}

	public FleximsDynamicEntityImpl getTraceDown() {
		return entity.get(PROP_NAME_TRACEDOWN);
	}

	public void setTracceDown(FleximsDynamicEntityImpl td) {
		entity.set(PROP_NAME_TRACEDOWN, td);
	}

	public void setTracceDown(ConditionQuery cq) {
		entity.set(PROP_NAME_TRACEDOWN, cq.getEntity());
	}

	ConditionQuery traceDownConditionQuery = null;

	public ConditionQuery getTraceDownQuery() {

		if (traceDownConditionQuery != null) {
			return traceDownConditionQuery;
		}
		traceDownConditionQuery = (ConditionQuery) QueryHelper.wrapQueryEntity(getTraceDown());
		return traceDownConditionQuery;
	}

	public Long getStartTime() {
		Long v = (Long) entity.get(PROP_NAME_RELATIVESTARTDATE);
		return v == null ? 0L : v;
	}

	public void setStartTime(long l) {
		entity.set(PROP_NAME_RELATIVESTARTDATE, l);
	}

	public Long getEndTime() {
		Long v = (Long) entity.get(PROP_NAME_RELATIVEENDDATE);
		return v == null ? 0L : v;

	}

	public void setEndTime(long l) {
		entity.set(PROP_NAME_RELATIVEENDDATE, l);
	}

	public boolean isAdjustToWholeTime() {
		return (Boolean) entity.get(PROP_NAME_WHOLE_TIME);
	}

	public void setAdjustToWholeTime(boolean v) {
		entity.set(PROP_NAME_WHOLE_TIME, v);
	}

	public TimeUnit getEndUnit() {
		String unitValue = (String) entity.get(PROP_NAME_RELATIVEENDUNIT);
		if (unitValue == null) {
			return null;
		}
		return TimeUnit.valueOf(unitValue);
	}

	public void setEndUnit(TimeUnit tu) {
		entity.set(PROP_NAME_RELATIVEENDUNIT, tu.name());
	}

	public String getProp() {
		return (String) entity.get(PROP_NAME_PROP_NAME);
	}

	public void setProp(String value) {
		entity.set(PROP_NAME_PROP_NAME, value);
	}

	public String getDescription() {
		String desc = (String) entity.get(PROP_NAME_DESCRIPTION);
		return desc == null ? getProp() : desc;
	}

	public void setDescription(String d) {
		entity.set(PROP_NAME_DESCRIPTION, d);
	}

	public boolean isIgnoreCase() {
		return (Boolean) entity.get(PROP_NAME_IGNORECASE);
	}

	public void setIgnoreCase(boolean v) {
		entity.set(PROP_NAME_IGNORECASE, v);
	}

	public Operator getOperator() {
		// return Operator.fromSymbol();
		return Operator.valueOf((String) entity.get(PROP_NAME_OP));
	}

	public void setOperator(Operator op) {
		entity.set(PROP_NAME_OP, op.name());
	}

	public void setOperator(String opName) {
		entity.set(PROP_NAME_OP, opName);
	}

	public String getFirstValue() {
		return (String) entity.get(PROP_NAME_FIRSTVALUE);
	}

	public void getFirstValue(String value) {
		entity.set(PROP_NAME_FIRSTVALUE, value);
	}

	public String getSecondValue() {
		return (String) entity.get(PROP_NAME_SECONDVALUE);
	}

	public void getSecondValue(String value) {
		entity.set(PROP_NAME_SECONDVALUE, value);
	}

	/*
	 * private Object strToObject(PropertyConfig pc, String str) { return
	 * Property.toSingleObjectValue(str, pc.getTypeData() .getPrimitiveType());
	 * }
	 */

	public MappingAccessor getTargetPropertyConfig() {
		ClassAccessor queryType = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), query.getQueryType());
		return JpaMapHelper.findProp(queryType, getProp());
	}

	// cache
	Attribute<?, ?> attr = null;

	public Attribute<?, ?> getAttribute() {
		if (attr != null) {
			return attr;
		}
		Metamodel metamodel = App.getPersistenceUnit().getEMF().getMetamodel();
		ManagedType<?> type = JpaMetamodelHelper.getManagedType(metamodel, query.getQueryType());
		attr = JpaMetamodelHelper.getAttribute(type, getProp());
		return attr;
	}

	/**
	 * If it has
	 * 
	 * @param type
	 * @return true if it contains subquery
	 */
	private boolean isReturnSimpleExpression() {
		Attribute<?, ?> attr = getAttribute();
		if (JpaMetamodelHelper.isPrimitiveType(attr)) {
			if (attr.isCollection()) {
				if (getOperator().isSizeOperator()) {
					// size op always uses subquery.
					return false;
				}

				if (isSomeColllectionMode()) {
					// use join
					return true;
				}

				// all or none mode: noExist query.
				return false;
			} else {
				// judge or a single value.
				return true;
			}

		} else {
			// relation.
			if (attr.isCollection()) {
				if (getOperator().isSizeOperator()) {
					return false;
				}

				if (isAllColllectionMode() || isNoneColllectionMode()) {
					return false;
				}
				if (getOperator() != Operator.tracedown) {
					// ne, eq, oneof, notoneof, null, notnull
					return true;
				} else {
					return isComplexTracedown(attr);
				}
			} else {
				if (getOperator() != Operator.tracedown) {
					// ne, eq, oneof, notoneof, null, notnull
					return true;
				} else {
					return isComplexTracedown(attr);
				}
			}
		}
	}

	protected boolean isComplexTracedown(Attribute<?, ?> attr) {
		ConditionQuery subQuery = getTraceDownQuery();

		for (PropertyCondition subPc : subQuery.getConditions()) {
			if (!subPc.isReturnSimpleExpression()) {
				return false;
			}
		}
		return true;
	}

	protected ReportQuery formSizeReportQuery(Attribute<?, ?> attr, Expression builder) {
		ReportQuery sizeQuery = new ReportQuery(attr.getDeclaringType().getJavaType(), builder);
		sizeQuery.setExpressionBuilder(builder.getBuilder());
		sizeQuery.addCount(getProp(), sizeQuery.getExpressionBuilder().anyOf(getProp()), Integer.class);
		return sizeQuery;
	}

	protected ReportQuery formRelationReportQuery(Attribute<?, ?> attr, Expression builder) {
		ReportQuery subQuery = new ReportQuery(JpaMetamodelHelper.getAttributeType(attr), builder);
		subQuery.setExpressionBuilder(builder.getBuilder());
		return subQuery;
	}

	protected ReportQuery formReportQueryForDirectCollection(Attribute<?, ?> attr, Expression builder) {
		ReportQuery subQuery = new ReportQuery(attr.getDeclaringType().getJavaType(), builder);
		subQuery.addAttribute("propvalue", builder.anyOf(getProp()));
		return subQuery;
	}

	protected Expression processPropertyLt(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).greaterThanEqual(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).lessThan(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).lessThan(builder.getParameter(p.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).lessThan(builder.getParameter(p.getParamName()));
		}
		return exp;
	}

	protected Expression processPropertyLe(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).greaterThan(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).lessThanEqual(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).lessThanEqual(builder.getParameter(p.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).lessThanEqual(builder.getParameter(p.getParamName()));
		}
		return exp;
	}

	protected Expression processPropertyEq(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		boolean isString = JpaMetamodelHelper.isString(attr);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isString && isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notEqual(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notEqual(builder.getParameter(p.getParamName())));
				}

				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isString && isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).equalsIgnoreCase(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).equal(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isString && isIgnoreCase()) {
					exp = builder.anyOf(getProp()).equalsIgnoreCase(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).equal(builder.getParameter(p.getParamName()));
				}
			}
		} else {
			if (isString && isIgnoreCase()) {
				exp = builder.get(getProp()).equalsIgnoreCase(builder.getParameter(p.getParamName()));
			} else {
				exp = builder.get(getProp()).equal(builder.getParameter(p.getParamName()));
			}

		}
		return exp;
	}

	protected Expression processPropertyGt(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).lessThanEqual(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).greaterThan(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).greaterThan(builder.getParameter(p.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).greaterThan(builder.getParameter(p.getParamName()));
		}
		return exp;
	}

	protected Expression processPropertyGe(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).lessThan(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).greaterThanEqual(builder.getParameter(p.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).greaterThanEqual(builder.getParameter(p.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).greaterThanEqual(builder.getParameter(p.getParamName()));
		}
		return exp;
	}

	protected Expression processPropertyNe(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		boolean isString = JpaMetamodelHelper.isString(attr);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isString && isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).equalsIgnoreCase(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).equal(builder.getParameter(p.getParamName())));
				}

				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isString && isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notEqual(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notEqual(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isString && isIgnoreCase()) {
					exp = builder.anyOf(getProp()).toUpperCase().notEqual(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).notEqual(builder.getParameter(p.getParamName()));
				}

			}
		} else {
			if (JpaMetamodelHelper.isRelation(attr)) {
				if (JpaMetamodelHelper.isRelationOwner(attr)) {
					Expression exp1 = builder.get(getProp()).notEqual(builder.getParameter(p.getParamName()));
					// null is not considered as equal
					Expression exp2 = builder.get(getProp()).isNull();
					exp = exp1.or(exp2);
				} else {
					exp = builder.get(getProp()).notEqual(builder.getParameter(p.getParamName()));
				}

			} else {
				if (isString && isIgnoreCase()) {
					exp = builder.get(getProp()).toUpperCase().notEqual(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.get(getProp()).notEqual(builder.getParameter(p.getParamName()));
				}
			}

		}

		return exp;
	}

	protected Expression processPropertyBetween(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		ConditionQueryParameter p1;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		p1 = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		p1.setIndex(2);
		parameters.add(p1);

		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).notBetween(builder.getParameter(p.getParamName()),
						builder.getParameter(p1.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).between(builder.getParameter(p.getParamName()),
						builder.getParameter(p1.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).between(builder.getParameter(p.getParamName()), builder.getParameter(p1.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).between(builder.getParameter(p.getParamName()), builder.getParameter(p1.getParamName()));
		}

		return exp;

	}

	protected Expression processPropertyNotBetween(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		ConditionQueryParameter p1;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		p1 = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		p1.setIndex(2);
		parameters.add(p1);

		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).between(builder.getParameter(p.getParamName()),
						builder.getParameter(p1.getParamName())));
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				subQuery.setSelectionCriteria(builder.anyOf(getProp()).notBetween(builder.getParameter(p.getParamName()),
						builder.getParameter(p1.getParamName())));
				exp = builder.notExists(subQuery);
			} else {
				exp = builder.anyOf(getProp()).notBetween(builder.getParameter(p.getParamName()), builder.getParameter(p1.getParamName()));
			}
		} else {
			exp = builder.get(getProp()).notBetween(builder.getParameter(p.getParamName()), builder.getParameter(p1.getParamName()));
		}

		return exp;
	}

	protected Expression processPropertyLike(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notLike(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notLike(builder.getParameter(p.getParamName())));
				}

				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).likeIgnoreCase(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).like(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isIgnoreCase()) {
					exp = builder.anyOf(getProp()).likeIgnoreCase(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).like(builder.getParameter(p.getParamName()));
				}

			}
		} else {
			if (isIgnoreCase()) {
				exp = builder.get(getProp()).likeIgnoreCase(builder.getParameter(p.getParamName()));
			} else {
				exp = builder.get(getProp()).like(builder.getParameter(p.getParamName()));
			}
		}
		return exp;
	}

	protected Expression processPropertyNotLike(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).likeIgnoreCase(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).like(builder.getParameter(p.getParamName())));
				}

				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notLike(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notLike(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isIgnoreCase()) {
					exp = builder.anyOf(getProp()).toUpperCase().notLike(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).notLike(builder.getParameter(p.getParamName()));
				}

			}
		} else {
			if (isIgnoreCase()) {
				exp = builder.get(getProp()).toUpperCase().notLike(builder.getParameter(p.getParamName()));
			} else {
				exp = builder.get(getProp()).notLike(builder.getParameter(p.getParamName()));
			}
		}

		return exp;
	}

	protected Expression processPropertyOneof(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		boolean isString = JpaMetamodelHelper.isString(attr);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notIn(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notIn(builder.getParameter(p.getParamName())));
				}

				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().in(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).in(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isIgnoreCase()) {
					exp = builder.anyOf(getProp()).toUpperCase().in(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).in(builder.getParameter(p.getParamName()));
				}

			}
		} else {
			if (isString && isIgnoreCase()) {
				exp = builder.get(getProp()).toUpperCase().in(builder.getParameter(p.getParamName()));
			} else {
				exp = builder.get(getProp()).in(builder.getParameter(p.getParamName()));
			}
		}
		return exp;
	}

	protected Expression processPropertyNotOneof(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = null;

		p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
		parameters.add(p);
		boolean isString = JpaMetamodelHelper.isString(attr);
		if (attr.isCollection()) {
			if (isAllColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().in(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).in(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else if (isNoneColllectionMode()) {
				ReportQuery subQuery = formReportQueryForDirectCollection(attr, builder);
				if (isIgnoreCase()) {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).toUpperCase().notIn(builder.getParameter(p.getParamName())));
				} else {
					subQuery.setSelectionCriteria(builder.anyOf(getProp()).notIn(builder.getParameter(p.getParamName())));
				}
				exp = builder.notExists(subQuery);
			} else {
				if (isIgnoreCase()) {
					exp = builder.anyOf(getProp()).toUpperCase().notIn(builder.getParameter(p.getParamName()));
				} else {
					exp = builder.anyOf(getProp()).notIn(builder.getParameter(p.getParamName()));
				}

			}
		} else {
			if (isString && isIgnoreCase()) {
				exp = builder.get(getProp()).toUpperCase().notIn(builder.getParameter(p.getParamName()));
			} else {
				exp = builder.get(getProp()).notIn(builder.getParameter(p.getParamName()));
			}
		}

		return exp;

	}

	protected Expression processPropertyTracedownTooneOptimized(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {
		ConditionQuery subQuery = getTraceDownQuery();

		Expression finalExp = null;
		for (PropertyCondition subPc : subQuery.getConditions()) {
			if (subPc.isReturnSimpleExpression()) {
				Expression subExp = subPc.processProperty(builder.get(getProp()), parameters);
				if (finalExp == null) {
					finalExp = subExp;
				} else {
					finalExp = finalExp.and(subExp);
				}
			} else {
				// low-performance query since an extra layer of exists added
				// but this is the only way to make it work.
				// this situation is rare. So we may do not need to optimize it
				Expression subBuilder = new ExpressionBuilder();
				Expression subExp = subPc.processProperty(subBuilder, parameters);
				ReportQuery childQuery = formRelationReportQuery(attr, subBuilder);
				childQuery.retrievePrimaryKeys();
				childQuery.setSelectionCriteria(subBuilder.equal(builder.get(getProp())).and(subExp));
				if (finalExp == null) {
					finalExp = builder.exists(childQuery);
				} else {
					finalExp = finalExp.and(builder.exists(childQuery));
				}

			}
		}
		return finalExp;
	}

	protected Expression processPropertyTracedownToManySomeOptimized(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {
		ConditionQuery subQuery = getTraceDownQuery();
		// subQuery.setPrefix(getQuery().getPrefix()+parameters.size()+"_");
		Expression prefixExp = builder.anyOf(getProp());

		List<Expression> complexExps = new ArrayList<Expression>();

		for (PropertyCondition subPc : subQuery.getConditions()) {
			Expression subExp = subPc.processProperty(prefixExp, parameters);
			complexExps.add(subExp);
		}
		Expression finalExp = null;
		if (complexExps.isEmpty()) {
			throw new InvalidQueryException("No expression from subquery.");
		}
		finalExp = complexExps.get(0);
		for (int i = 1; i < complexExps.size(); i++) {
			finalExp = finalExp.and(complexExps.get(i));
		}
		return finalExp;
	}

	protected Expression processPropertyTracedownToManyAll(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {
		ConditionQuery subQuery = getTraceDownQuery();
		// subQuery.setPrefix(getQuery().getPrefix()+parameters.size()+"_");
		Expression subBuilder = new ExpressionBuilder();
		List<Expression> childExps = new ArrayList<Expression>(3);
		for (PropertyCondition subPc : subQuery.getConditions()) {
			Expression subExp = subPc.processProperty(subBuilder, parameters);
			childExps.add(subExp);
		}
		if (childExps.isEmpty()) {
			throw new InvalidQueryException("No expression from subquery.");
		}
		Expression finalExp = childExps.get(0);

		for (int i = 1; i < childExps.size(); i++) {
			finalExp = finalExp.and(childExps.get(i));
		}

		return builder.allOf(getProp(), finalExp);

	}

	protected Expression processPropertyTracedownToManyNone(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {
		ConditionQuery subQuery = getTraceDownQuery();
		// subQuery.setPrefix(getQuery().getPrefix()+parameters.size()+"_");
		Expression subBuilder = new ExpressionBuilder();
		List<Expression> childExps = new ArrayList<Expression>(3);
		for (PropertyCondition subPc : subQuery.getConditions()) {
			Expression subExp = subPc.processProperty(subBuilder, parameters);
			childExps.add(subExp);
		}
		if (childExps.isEmpty()) {
			throw new InvalidQueryException("No expression from subquery.");
		}
		Expression finalExp = childExps.get(0);

		for (int i = 1; i < childExps.size(); i++) {
			finalExp = finalExp.and(childExps.get(i));
		}

		return builder.noneOf(getProp(), finalExp);
	}

	protected Expression processPropertyEmpty(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		Expression exp = null;
		if (JpaMetamodelHelper.isRelation(attr)) {
			exp = builder.isEmpty(getProp());
		} else {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=396892
			exp = builder.subQuery(this.formSizeReportQuery(attr, builder)).equal(0);
		}
		return exp;

	}

	protected Expression processPropertyNotEmpty(Attribute<?, ?> attr, Expression builder, List<Parameter> parameters) {

		Expression exp = null;
		if (JpaMetamodelHelper.isRelation(attr)) {
			exp = builder.notEmpty(getProp());
		} else {
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=396892
			exp = builder.subQuery(this.formSizeReportQuery(attr, builder)).greaterThan(0);
		}
		return exp;

	}

	/**
	 * 
	 * @param type
	 * @param builder
	 * @param parameters
	 * @return a fully parameterized query. A default value for this query
	 *         should be set to query at a late time.
	 */
	public Expression processProperty(Expression builder, List<Parameter> parameters) {
		Attribute<?, ?> attr = getAttribute();

		Operator op = getOperator();
		ConditionQueryParameter p;
		// convenient expression, only used by simple case
		Expression exp = builder.get(getProp());

		switch (op) {

		// Operator for primtive type first: lt, le, gt, ge, eq, ne, between,
		// notbetween, like(String), notlike(String), checked, unchecked
		// oneof(in(...)), notoneof(notin(...))
		case lt:
			return processPropertyLt(attr, builder, parameters);
		case le:
			return processPropertyLe(attr, builder, parameters);
		case eq:
			return processPropertyEq(attr, builder, parameters);
		case gt:
			return processPropertyGt(attr, builder, parameters);
		case ge:
			return processPropertyGe(attr, builder, parameters);
		case ne:
			return processPropertyNe(attr, builder, parameters);
		case between:
			return processPropertyBetween(attr, builder, parameters);
		case notbetween:
			return processPropertyNotBetween(attr, builder, parameters);
		case like:
			return processPropertyLike(attr, builder, parameters);
		case notlike:
			return processPropertyNotLike(attr, builder, parameters);
		case oneof:
			return processPropertyOneof(attr, builder, parameters);
		case notoneof:
			// for direct follection.
			return processPropertyNotOneof(attr, builder, parameters);
		case checked:
			exp = exp.equal(true);
			return exp;
		case unchecked:
			exp = exp.equal(false);
			return exp;
			/***************** The above is for comparison of primitive value */
			// null, notnull are not primitive attr, only property with single
			// value
		case notnull:
			if (attr.isCollection()) {
				return this.processPropertyNotEmpty(attr, builder, parameters);
			} else {
				exp = exp.notNull();
				return exp;
			}
		case isnull:
			if (attr.isCollection()) {
				return this.processPropertyEmpty(attr, builder, parameters);
			} else {

				if (JpaMetamodelHelper.isRelation(attr) && !JpaMetamodelHelper.isRelationOwner(attr)) {
					ExpressionBuilder main = new ExpressionBuilder();
					ReportQuery subQuery = formRelationReportQuery(attr, main);
					subQuery.setSelectionCriteria(main.equal(builder.get(getProp())));
					subQuery.addAttribute("id");

					exp = builder.notExists(subQuery);

				} else {
					exp = exp.isNull();
				}
				return exp;
			}

			// -----------------size operation for collection: size, sizegt,
			// sizelt, empty, notempty
		case size:
			// do not work for simple collection.
			// need test case for simple collection
			p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
			parameters.add(p);
			if (JpaMetamodelHelper.isRelation(attr)) {
				exp = builder.size(getProp()).equal(builder.getParameter(p.getParamName()));
			} else {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=396892
				exp = builder.subQuery(this.formSizeReportQuery(attr, builder)).equal(builder.getParameter(p.getParamName()));
			}
			return exp;
		case sizeGt:
			p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
			parameters.add(p);
			if (JpaMetamodelHelper.isRelation(attr)) {
				exp = builder.size(getProp()).greaterThan(builder.getParameter(p.getParamName()));
			} else {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=396892
				exp = builder.subQuery(this.formSizeReportQuery(attr, builder)).greaterThan(builder.getParameter(p.getParamName()));
			}
			return exp;
		case sizeLt:
			p = new ConditionQueryParameter(this, getQuery().getPrefix() + parameters.size());
			parameters.add(p);
			if (JpaMetamodelHelper.isRelation(attr)) {
				exp = builder.size(getProp()).lessThan(builder.getParameter(p.getParamName()));
			} else {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=396892
				exp = builder.subQuery(this.formSizeReportQuery(attr, builder)).lessThan(builder.getParameter(p.getParamName()));
			}
			return exp;
		case empty:
			return this.processPropertyEmpty(attr, builder, parameters);
		case notempty:
			return this.processPropertyNotEmpty(attr, builder, parameters);
		case tracedown:
			if (attr.isCollection()) {
				if (isAllColllectionMode()) {
					return this.processPropertyTracedownToManyAll(attr, builder, parameters);
				} else if (isNoneColllectionMode()) {
					return this.processPropertyTracedownToManyNone(attr, builder, parameters);
				} else {
					return this.processPropertyTracedownToManySomeOptimized(attr, builder, parameters);
				}

			} else {
				return this.processPropertyTracedownTooneOptimized(attr, builder, parameters);
			}
		default:
			break;

		}
		return null;
	}

	public FleximsDynamicEntityImpl getEntity() {
		return entity;
	}

}
