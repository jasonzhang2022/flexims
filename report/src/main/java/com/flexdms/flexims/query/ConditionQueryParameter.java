package com.flexdms.flexims.query;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.metamodel.Attribute;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.PrimitiveType;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.util.TimeUnit;
import com.flexdms.flexims.util.Utils;
import com.flexdms.flexims.util.ValueFormatException;
import com.flexdms.flexims.util.ValueUtils;

public class ConditionQueryParameter implements Parameter {

	public static final Logger LOGGER = Logger.getLogger(ConditionQueryParameter.class.getName());
	public static final int EXPECTED_VALUE_SIZE = 5;

	PropertyCondition condition;
	// parameter for first value;
	int index = 1;
	String paraName;

	@Override
	public String getDescription() {
		return index == 1 ? condition.getDescription() : condition.getDescription() + "(second value)";
	}

	public PropertyCondition getCondition() {
		return condition;
	}

	public void setCondition(PropertyCondition condition) {
		this.condition = condition;
	}

	protected ConditionQueryParameter() {

	}

	public ConditionQueryParameter(PropertyCondition condition, String paramName) {
		super();
		this.condition = condition;
		this.paraName = paramName;
	}

	@Override
	public String getName() {
		return paraName;
	}

	public String getParamName() {
		return paraName;
	}

	public void setParamName(String paraName) {
		this.paraName = paraName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	Object value;

	@Override
	public Object getValue() {
		if (value != null) {
			return value;
		}
		return getDefaultValue();
	}

	@Override
	public String getDefaultValue() {
		PrimitiveType pt = JpaMetamodelHelper.getPrimitiveType(condition.getAttribute());

		if (index == 1) {
			String dv = condition.getFirstValue();

			if (pt == null && condition.getOperator() == Operator.tracedown) {
				if (dv == null) {
					return dv;
				}
				if (dv.equals("0")) {
					return null;
				}
				return String.valueOf(condition.getTraceDown());
			}

			if (pt != null && (pt == PrimitiveType.DATE || pt == PrimitiveType.TIMESTAMP)) {
				if (dv != null) {
					return dv;
				}
				TimeUnit tu = condition.getStartUnit();
				long number = condition.getStartTime();
				if (tu == null) {
					tu = TimeUnit.DAY;
				}
				long t = ValueUtils.getValue(tu, number, condition.isAdjustToWholeTime());
				if (pt == PrimitiveType.DATE) {

					return Utils.dateToString(new Date(t));
				} else {
					return Utils.dateToString(new Date(t));
				}
			}
			return dv;
		} else {
			String dv = condition.getSecondValue();
			if (pt != null && (pt == PrimitiveType.DATE || pt == PrimitiveType.TIMESTAMP)) {
				if (dv != null) {
					return dv;
				}
				TimeUnit tu = condition.getEndUnit();
				long number = condition.getEndTime();
				if (tu == null) {
					tu = TimeUnit.DAY;
				}
				long t = ValueUtils.getValue(tu, number, condition.isAdjustToWholeTime());
				if (pt == PrimitiveType.DATE) {

					return Utils.dateToString(new Date(t));
				} else {
					return Utils.dateToString(new Date(t));
				}
			}
			return dv;
		}
	}

	public void setValue(Object v) {
		this.value = v;
	}

	@Override
	public String getPropertyName() {
		return condition.getProp();
	}

	@Override
	public Object getValueForQuery() {
		Object v = getValue();

		if (v == null) {
			return v;
		}
		// Does not depends on property type
		if (condition.getOperator() == Operator.size) {
			try {
				return Integer.parseInt(v.toString());
			} catch (NumberFormatException e) {
				// throw new
				// InvalidQueryParameter("An integer value is expected for '{0}' opreator, but got a {1}",
				// new Object[] {Operator.size.getSymbol(), v.toString()});
				throw new InvalidQueryParameter("An integer value is expected for '{0}' opreator, but got a {1}");
			}
		}
		if (condition.getOperator() == Operator.tracedown) {
			try {
				// tracedown query can not be static
				// long id= Long.parseLong(v.toString());
				TypedQuery query = condition.getTraceDownQuery();
				if (query == null) {
					// throw new
					// InvalidQueryParameter("Can not find Query instance with id {0} for tracedown operator",
					// new Object[] {id});
					throw new InvalidQueryParameter("Can not find Query instance with id {0} for tracedown operator");
				}
			} catch (NumberFormatException e) {
				// throw new
				// InvalidQueryParameter("An long value is expected for tracedown opreator, but got a {0}",
				// new Object[] { v.toString()});
				throw new InvalidQueryParameter("An long value is expected for tracedown opreator, but got a {0}");
			}
		}

		if (index == 2) {
			// always single value
			return getValueAsObjectSingle(v, condition);
		}

		int number = Operator.requiredValues(condition.getOperator());
		if (number == 1 || number == 2) {
			// for number=2, the second number is in second value
			return getValueAsObjectSingle(v, condition);
		} else {
			return getValueAsObjectMultiple(v, condition);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object getValueAsObjectMultiple(Object value, PropertyCondition condition) {
		List ret = new ArrayList(EXPECTED_VALUE_SIZE);
		// multiple value is needed such as in operator
		if (value instanceof List) {
			List vs = (List) value;

			for (Object v : vs) {
				ret.add(getValueAsObjectSingle(v, condition));
			}
		} else if (value instanceof String) {
			String[] ps = ((String) value).split(",");
			for (String p : ps) {
				ret.add(getValueAsObjectSingle(p, condition));
			}
		} else {
			ret.add(getValueAsObjectSingle(value, condition));
		}
		return ret;

	}

	public static Object getValueAsObjectSingle(Object value, PropertyCondition condition) {

		PrimitiveType pt = JpaMetamodelHelper.getPrimitiveType(condition.getAttribute());

		if (pt != null) {
			if (value instanceof String) {
				try {
					String strValue = (String) value;
					if (condition.isIgnoreCase()) {
						strValue = strValue.toUpperCase();
					}
					return ValueUtils.toSingleObjectValue(strValue, pt);
				} catch (ValueFormatException e) {
					throw new InvalidQueryParameterValue(e.getMessage());
				}
			} else {
				// already an object. most likely set from code.
				return value;
			}
		} else {
			Attribute<?, ?> attribute = condition.getAttribute();
			Class<?> referenceClass = JpaMetamodelHelper.getAttributeType(attribute);
			if (attribute == null) {
				throw new InvalidQueryException("Invalid query");
			}
			if (JpaMetamodelHelper.isEmedded(attribute) && !attribute.isCollection()) {
				throw new InvalidQueryParameter("Can not query on single embedded instance");
			}
			if (value instanceof FleximsDynamicEntityImpl) {
				FleximsDynamicEntityImpl i = (FleximsDynamicEntityImpl) value;
				if (!referenceClass.isAssignableFrom(value.getClass())) {
					// "For query parameter '"+pc.getDisplayName()
					// +"' "+", Expect type "+ctype.getName()+", but get an instance of "+i.getType().getName()
					InvalidQueryParameter exception = new InvalidQueryParameter(
							"For query parameter '{0}', Expect type {1}, but get an instance of {2}", new Object[] { condition.getProp(),
									referenceClass.getSimpleName(), value.getClass().getSimpleName() });
					throw exception;
				}
				return i;
			} else if (value instanceof Number || value instanceof String) {
				long id = Long.parseLong(value.toString());
				EntityDAO dao = App.getInstance(EntityDAO.class);
				FleximsDynamicEntityImpl i = dao.loadEntity(referenceClass, id);
				if (i == null) {
					InvalidQueryParameter exception = new InvalidQueryParameter(
							"For query parameter '{0}',  can not find instance with id {1} of type {2} as input parameter", new Object[] {
									condition.getProp(), id, referenceClass.getSimpleName() });
					throw exception;
				}
				return i;
			} else {
				InvalidQueryParameter exception = new InvalidQueryParameter("For query parameter '{0}', do not know how to convert {1} to instance",
						new Object[] { condition.getProp(), value.getClass() });
				throw exception;
			}
		}
	}

}
