package com.flexdms.flexims.query;

import java.util.Map;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

/**
 * A query that return a list of object of a particular type.
 * 
 * @author jason
 * 
 */
public abstract class TypedQuery implements TypedQueryI {

	public FleximsDynamicEntityImpl entity;
	public static final String TYPE_NAME = "TypedQuery";
	public static final String PROP_NAME_TYPE = "TargetedType";
	public static final String PROP_NAME_REPORTS = "Reports";
	public static final String PROP_NAME_NAME = "Name";

	public TypedQuery(FleximsDynamicEntityImpl entityImpl) {
		super();
		this.entity = entityImpl;
	}

	public String getQueryType() {
		return (String) entity.get(PROP_NAME_TYPE);
	}

	public void setQueryType(String typeName) {

		entity.set(PROP_NAME_TYPE, typeName);
	}

	public String getQueryName() {
		return (String) entity.get(PROP_NAME_NAME);
	}

	public void setQueryName(String qname) {
		entity.set(PROP_NAME_NAME, qname);
	}

	public Parameter getParameter(String name) {
		for (Parameter param : getParameters()) {
			if (param.getName().equals(name)) {
				return param;
			}
		}
		return null;
	}

	/**
	 * A parameter has value.
	 * 
	 * @return whether the query is executable or not.
	 */
	public boolean isExecutable() {
		for (Parameter param : getParameters()) {
			if (param.getValue() == null) {
				return false;
			}
		}
		return true;
	}

	public boolean isValid() {
		return true;
	}

	public Map<String, Object> getAttributes() {
		return entity.getMetaAttributes();
	}

	public FleximsDynamicEntityImpl getEntity() {
		return entity;
	}

	public void setEntity(FleximsDynamicEntityImpl entity) {
		this.entity = entity;
	}

	QueryContext queryContext;

	@Override
	public void setQueryContext(QueryContext qContext) {
		queryContext = qContext;
	}

	@Override
	public QueryContext getQueryContext() {
		return queryContext;
	}

}
