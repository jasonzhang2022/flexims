package com.flexdms.flexims.query;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityGraph;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class QueryContext {

	EntityGraph<?> graph;
	List<OrderField> orderFields;
	boolean useCache = false;
	boolean cacheResult = false;
	List<String> props;

	FleximsDynamicEntityImpl fxReport;

	public QueryContext() {

	}

	public List<OrderField> getOrderFields() {
		return orderFields;
	}

	public void setOrderFields(List<OrderField> orderFields) {
		this.orderFields = orderFields;
	}

	public EntityGraph<?> getGraph() {
		return graph;
	}

	public void setGraph(EntityGraph<?> graph) {
		this.graph = graph;
	}

	public List<String> getProps() {
		return props;
	}

	public void setProps(List<String> props) {
		this.props = props;
	}

	public void setProps(String... props) {
		this.props = Arrays.asList(props);
	}

	public boolean isCacheResult() {
		return cacheResult;
	}

	public void setCacheResult(boolean cacheResult) {
		this.cacheResult = cacheResult;
	}

	public boolean isUseCache() {
		return useCache;
	}

	public void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

	public FleximsDynamicEntityImpl getFxReport() {
		return fxReport;
	}

	public void setFxReport(FleximsDynamicEntityImpl fxReport) {
		this.fxReport = fxReport;
	}

}
