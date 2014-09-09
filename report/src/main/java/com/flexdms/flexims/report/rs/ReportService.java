package com.flexdms.flexims.report.rs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.persistence.dynamic.DynamicEntity;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.ConditionQueryParameter;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.Parameter;
import com.flexdms.flexims.query.PropertyCondition;
import com.flexdms.flexims.query.QueryContext;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.query.TypedQuery;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.rsutil.RsHelper;
import com.flexdms.flexims.util.SessionCtx;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Path("/query")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class ReportService {

	@Context
	HttpServletRequest request;

	@Context
	Request rs;

	@Inject
	EntityDAO dao;
	@Inject
	EntityManager em;

	@Inject
	SessionCtx sessionCtx;
	@Inject
	JaxbHelper jaxbHelper;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/querybytype/{typename}")
	@GET
	public Entities getQueriesByType(@PathParam("typename") String typename) {
		Entities entities = new Entities();
		List list = QueryHelper.loadQueriesForType(typename);
		if (list != null) {
			entities.setItems(list);
		}
		return entities;
	}

	@Path("/querybyname/{queryname}")
	@GET
	public FleximsDynamicEntityImpl getQueryByName(@PathParam("queryname") String queryName) {
		FleximsDynamicEntityImpl entity = QueryHelper.loadQuery(queryName).getEntity();
		em.refresh(entity);
		return entity;
	}

	
	@Path("/reportbytype/{typename}")
	@GET
	public Entities getReportsByType(@PathParam("typename") String typename) {
		Entities entities = new Entities();
		List list = QueryHelper.loadReportForType(typename);
		if (list != null) {
			entities.getItems().addAll(list);
		}
		for (Object r : list) {
			FleximsDynamicEntityImpl reportDynamicEntityImpl = (FleximsDynamicEntityImpl) r;
			FleximsDynamicEntityImpl queryDynamicEntityImpl = reportDynamicEntityImpl.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
			entities.getItems().addAll(getAllTraceDown(queryDynamicEntityImpl.getId()).getItems());
		}
		return entities;
	}

	@Path("/reportbyname/{reportname}")
	@GET
	public Entities getReportByName(@PathParam("reportname") String reportName) {
		Entities entities = new Entities();
		Map<String, Object> params = new HashMap<>(2);
		params.put(FxReportWrapper.PROP_NAME_PROP_NAME, reportName);
		TypedQuery query = QueryHelper.createSimpleEqualQuery(FxReportWrapper.TYPE_NAME, params);
		query.buildQuery(em);
		List list = query.fetchAllResult(em);
		if (list == null || list.isEmpty()) {
			return entities;
		}
		FleximsDynamicEntityImpl entity = (FleximsDynamicEntityImpl) list.get(0);
		em.refresh(entity);
		entities.getItems().add(entity);

		FleximsDynamicEntityImpl queryDynamicEntityImpl = entity.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
		entities.getItems().addAll(getAllTraceDown(queryDynamicEntityImpl.getId()).getItems());
		return entities;
	}

	@Path("/reportbyid/{id}")
	@GET
	public Entities getReportById(@PathParam("id") long reportId) {
		Entities entities = new Entities();

		DynamicEntity dEntityImpl = em.find(JpaHelper.getEntityClass(em, FxReportWrapper.TYPE_NAME), reportId);
		if (dEntityImpl == null) {
			return entities;
		}
		em.refresh(dEntityImpl);
		entities.addItem(dEntityImpl);
		FleximsDynamicEntityImpl queryDynamicEntityImpl = dEntityImpl.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
		entities.getItems().addAll(getAllTraceDown(queryDynamicEntityImpl.getId()).getItems());
		return entities;
	}

	@Path("/allreportbytype/{typename}")
	@GET
	@Transactional
	public Entities getAllReport(@PathParam("typename") String typename) {

		Entities entities = new Entities();
		// find the best report.
		List reports = QueryHelper.loadReportForType(typename);
		FleximsDynamicEntityImpl bestFitReport = null;
		FleximsDynamicEntityImpl reportret = null;
		for (Object robj : reports) {
			FleximsDynamicEntityImpl report = (FleximsDynamicEntityImpl) robj;
			FleximsDynamicEntityImpl query1 = report.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
			String name = query1.get(TypedQuery.PROP_NAME_NAME);
			if (name.equalsIgnoreCase("all " + typename)) {
				bestFitReport = report;
			}
			if (query1.getClass().getSimpleName().equals(ConditionQuery.TYPE_NAME)) {
				ConditionQuery cQuery = new ConditionQuery(query1);
				if (cQuery.getConditions().isEmpty()) {
					reportret = report;
				}
			}
		}
		if (bestFitReport != null) {
			reportret = bestFitReport;
		}
		if (reportret != null) {
			return entities.addItem(reportret, reportret.get(FxReportWrapper.PROP_NAME_PROP_QUERY));
		}

		List queries = QueryHelper.loadQueriesForType(typename);
		FleximsDynamicEntityImpl bestFitQuery = null;
		FleximsDynamicEntityImpl query = null;
		for (Object robj : queries) {
			FleximsDynamicEntityImpl query1 = (FleximsDynamicEntityImpl) robj;
			String name = query1.get(TypedQuery.PROP_NAME_NAME);
			if (name.equalsIgnoreCase("all " + typename)) {
				bestFitQuery = query1;
				break;
			}
			if (query1.getClass().getSimpleName().equals(ConditionQuery.TYPE_NAME)) {
				ConditionQuery cQuery = new ConditionQuery(query1);
				if (cQuery.getConditions().isEmpty()) {
					query = query1;
				}
			}
		}
		if (bestFitQuery != null) {
			query = bestFitQuery;
		}
		if (query == null) {
			query = QueryHelper.createAllQuery(typename);
		} else {
			em.refresh(query);
		}
		List list2 = query.get(TypedQuery.PROP_NAME_REPORTS);
		if (list2 != null && !list2.isEmpty()) {
			reportret = (FleximsDynamicEntityImpl) list2.get(0);
			return entities.addItem(reportret, reportret.get(FxReportWrapper.PROP_NAME_PROP_QUERY));
		}
		reportret = QueryHelper.createAllReport(query);
		return entities.addItem(reportret, reportret.get(FxReportWrapper.PROP_NAME_PROP_QUERY));
	}

	@Path("/alltracedown/{queryid}")
	@GET
	public Entities getAllTraceDown(@PathParam("queryid") long id) {
		Entities entities = new Entities();
		FleximsDynamicEntityImpl entityImpl = dao.loadEntity(JpaHelper.getEntityClass(em, ConditionQuery.TYPE_NAME), id);
		if (entityImpl == null) {
			return entities;
		}
		em.refresh(entityImpl);
		LinkedList<FleximsDynamicEntityImpl> queries = new LinkedList<>();
		queries.push(entityImpl);
		FleximsDynamicEntityImpl query = null;
		while ((query = queries.pop()) != null) {
			entities.getItems().add(query);
			List<FleximsDynamicEntityImpl> conditions = query.get(ConditionQuery.PROP_NAME_PROP_CONDITION);
			if (conditions == null) {
				continue;
			}
			for (FleximsDynamicEntityImpl propCond : conditions) {
				FleximsDynamicEntityImpl subquery = propCond.get(PropertyCondition.PROP_NAME_TRACEDOWN);
				if (subquery != null) {
					queries.push(subquery);
				}
			}
			if (queries.isEmpty()) {
				break;
			}
		}
		return entities;
	}

	@Path("/prepare")
	@POST
	@Transactional
	public FxReportWrapper prepareQuery(FxReportWrapper report) {
		if (report.getUuid() != null) {
			destory(report.getUuid());
		}

		report.setUuid(UUID.randomUUID().toString());
		ConditionQuery query = (ConditionQuery) report.getQuery();

		// TODO build entityObject based on property to display

		// order by
		QueryContext queryContext = new QueryContext();
		queryContext.setOrderFields(report.getOrderBy());
		queryContext.setProps(report.getProperties());
		queryContext.setFxReport(report.getEntity());
		query.setQueryContext(queryContext);
		ReportActionContext actionContext = new ReportActionContext();
		actionContext.setFxReport(report.getEntity());
		App.fireEvent(actionContext);
		query.buildQuery(em);
		List<Parameter> paramsList = query.getParameters();
		int i = 0;
		// assume that all parameters has value, parameters are ordered in the
		// same order.
		// This is client responsibility
		for (Parameter param1 : paramsList) {
			ConditionQueryParameter param = (ConditionQueryParameter) param1;
			switch (Operator.requiredValues(param.getCondition().getOperator())) {
			case 1:
			case 2:
				param.setValue(report.getParams().get(i).getValue());
				break;
			case 3:
				param.setValue(report.getParams().get(i).getValues());
				break;
			default:
				break;
			}
			i++;
		}

		report.setCount(query.getResultCount(em));
		sessionCtx.putAttr("report:" + report.getUuid(), report);

		return report;
	}

	@Path("/fetch/{uuid}{offset: (/\\d+)?}{len: (/\\d+)?}")
	@GET
	@Transactional
	public Response fetchResult(@PathParam("uuid") String uuid, @PathParam("offset") String offsetStr, @PathParam("len") String lenStr,
			@Context Request rs) {

		if (offsetStr.length() == 0) {
			offsetStr = "/0";
		}
		if (lenStr.length() == 0) {
			lenStr = "/100";
		}
		final FxReportWrapper report = (FxReportWrapper) sessionCtx.getAttr("report:" + uuid);
		ConditionQuery query = (ConditionQuery) report.getQuery();
		final boolean xml = RsHelper.isXml(rs);

		final Entities entities = new Entities();
		entities.getItems().addAll(query.fetchPartialResult(Integer.parseInt(offsetStr.substring(1)), Integer.parseInt(lenStr.substring(1)), em));
		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				jaxbHelper.output(entities, new OutputStreamWriter(output), xml, true);
			}
		};
		if (xml) {
			return Response.ok(stream).type(MediaType.APPLICATION_XML).build();
		}
		return Response.ok(stream).type(MediaType.APPLICATION_JSON).build();
	}

	@Path("/fetchall/{uuid}")
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
			"text/csv", "application/pdf" })
	public Response fetchResultAll(@PathParam("uuid") String uuid, 
			@QueryParam("format") @DefaultValue("json") final String format,
			@QueryParam("nested") @DefaultValue("true") final boolean nested,
			@Context Request rs) {

		FxReportWrapper report = (FxReportWrapper) sessionCtx.getAttr("report:" + uuid);
		ConditionQuery query = (ConditionQuery) report.getQuery();

		final Entities entities = new Entities();
		entities.getItems().addAll(query.fetchAllResult(em));
		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				if (format.equalsIgnoreCase("xml")) {
					jaxbHelper.output(entities, new OutputStreamWriter(output), true, nested);
				} else {
					jaxbHelper.output(entities, new OutputStreamWriter(output), false, nested);
				}
			}
		};

		if (format.equalsIgnoreCase("xml")) {
			return Response.ok(stream).type(MediaType.APPLICATION_XML).build();
		}
		if (format.equalsIgnoreCase("json")) {
			return Response.ok(stream).type(MediaType.APPLICATION_JSON).build();
		}
		// do not support other type right now
		return Response.noContent().build();

	}

	@Path("/destory/{uuid}")
	@DELETE
	public void destory(@PathParam("uuid") String uuid) {

		sessionCtx.getAttrs().remove("report:" + uuid);
	}

}
