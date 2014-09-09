package com.flexdms.flexims.report.rs.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.jpa.helper.NameValuePair;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.query.TypedQuery;
import com.flexdms.flexims.report.rs.QueryParamValue;
import com.flexdms.flexims.report.rs.QueryParamValues;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement(name = "FxReportWrapper")
@XmlSeeAlso({ QueryParamValue.class, QueryParamValues.class })
public class FxReportWrapper {

	public static final Logger LOGGER = Logger.getLogger(ConditionQuery.class.getName());
	public static final String TYPE_NAME = "FxReport";
	public static final String ORDERFIELD_TYPE_NAME = "FxReportOrderField";

	public static final String PROP_NAME_PROP_NAME = "Name";

	public static final String PROP_NAME_PROP_DESCRIPTION = "Description";
	public static final String PROP_NAME_PROP_QUERY = "Query";
	public static final String PROP_NAME_PROP_PROPERTIES = "Properties";
	public static final String PROP_NAME_PROP_ORDERBY = "OrderBy";
	public static final String PROP_NAME_PROP_PARAMVALUES = "ParamValues";

	public String uuid;

	public int count;

	private FleximsDynamicEntityImpl entity;

	@XmlElement
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@XmlElement
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@XmlAnyElement(lax = true)
	public FleximsDynamicEntityImpl getEntity() {
		return (FleximsDynamicEntityImpl) entity;
	}

	public FxReportWrapper setEntity(FleximsDynamicEntityImpl entity) {
		this.entity = entity;
		return this;
	}

	@XmlTransient
	public String getName() {
		return (String) getEntity().get(PROP_NAME_PROP_NAME);
	}

	public FxReportWrapper setName(String value) {
		getEntity().set(PROP_NAME_PROP_NAME, value);
		return this;
	}

	@XmlTransient
	public String getDescription() {
		return (String) getEntity().get(PROP_NAME_PROP_DESCRIPTION);
	}

	public void setDescription(String value) {
		getEntity().set(PROP_NAME_PROP_DESCRIPTION, value);
	}

	TypedQuery query;

	@XmlTransient
	public TypedQuery getQuery() {
		if (query == null) {
			FleximsDynamicEntityImpl queryEntityImpl = getEntity().get(PROP_NAME_PROP_QUERY);
			query = QueryHelper.wrapQueryEntity(queryEntityImpl);
		}
		return query;

	}

	public FxReportWrapper setQuery(TypedQuery query) {
		this.query = query;
		getEntity().set(PROP_NAME_PROP_QUERY, query.getEntity());
		return this;
	}

	public FxReportWrapper setQuery(FleximsDynamicEntityImpl queryEntity) {
		this.query = QueryHelper.wrapQueryEntity(queryEntity);
		getEntity().set(PROP_NAME_PROP_QUERY, queryEntity);
		return this;
	}

	List<String> properties;

	@XmlTransient
	public List<String> getProperties() {
		if (properties == null) {
			NameValueList nvs = (NameValueList) getEntity().get(PROP_NAME_PROP_PROPERTIES);
			if (nvs != null) {
				properties = nvs.toList();
			} else {
				properties = new ArrayList<>(1);
			}
		}

		return properties;
	}

	List<String> itemsproperties;

	@XmlTransient
	public List<String> getItemsProperties() {
		if (itemsproperties == null) {
			List<String> propsList = getProperties();
			itemsproperties = new ArrayList<>(propsList.size());
			for (String prop : propsList) {
				itemsproperties.add("items." + prop);
			}
		}

		return itemsproperties;
	}

	public void setProperties(List<String> value) {
		properties = value;
		getEntity().set(PROP_NAME_PROP_PROPERTIES, NameValueList.fromList(value));
	}

	public FxReportWrapper setProperties(String... value) {
		List<String> properties = new ArrayList<>(value.length);
		for (String v : value) {
			properties.add(v);
		}
		this.properties = properties;
		getEntity().set(PROP_NAME_PROP_PROPERTIES, NameValueList.fromList(properties));
		return this;
	}

	List<OrderField> orderBys = null;

	@XmlTransient
	public List<OrderField> getOrderBy() {
		if (orderBys != null) {
			return orderBys;
		}
		orderBys = new ArrayList<>();
		NameValueList nvs = (NameValueList) getEntity().get(PROP_NAME_PROP_ORDERBY);
		if (nvs != null && nvs.getEntry() != null) {
			for (NameValuePair oe : nvs.getEntry()) {
				OrderField oField = new OrderField(oe.getName(), OrderDirection.valueOf(oe.getValue()));
				orderBys.add(oField);

			}
		}

		return orderBys;
	}

	public FxReportWrapper setOrderBy(List<OrderField> os) {
		orderBys = os;
		NameValueList nvs = new NameValueList();
		ArrayList<NameValuePair> entry = new ArrayList<>(os.size());
		for (OrderField of : os) {
			NameValuePair nValuePair = new NameValuePair(of.getPropname(), of.getDirection().name());
			entry.add(nValuePair);
		}
		nvs.setEntry(entry);
		getEntity().set(PROP_NAME_PROP_ORDERBY, nvs);
		return this;
	}

	@XmlTransient
	public ArrayList<QueryParamValue> getParams() {
		QueryParamValues nvs = (QueryParamValues) getEntity().get(PROP_NAME_PROP_PARAMVALUES);
		return nvs.getParamValues();

	}

	public FxReportWrapper setParams(ArrayList<QueryParamValue> params) {
		QueryParamValues nvs = (QueryParamValues) getEntity().get(PROP_NAME_PROP_PARAMVALUES);
		if (nvs == null) {
			nvs = new QueryParamValues();
			getEntity().set(PROP_NAME_PROP_PARAMVALUES, nvs);
		}
		nvs.setParamValues(params);
		return this;
	}

	public FxReportWrapper addParamValue(String... values) {
		QueryParamValues nvs = (QueryParamValues) getEntity().get(PROP_NAME_PROP_PARAMVALUES);
		if (nvs == null) {
			nvs = new QueryParamValues();
			getEntity().set(PROP_NAME_PROP_PARAMVALUES, nvs);
		}
		nvs.addParamValues(values);
		return this;
	}

}
