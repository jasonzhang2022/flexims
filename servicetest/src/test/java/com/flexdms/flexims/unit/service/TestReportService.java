package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.query.PropertyCondition;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.query.TypedQuery;
import com.flexdms.flexims.report.rs.QueryParamValue;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.rsutil.RSMsg;
import com.flexdms.flexims.unit.Util;

@RunWith(Arquillian.class)
@RunAsClient
public class TestReportService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		return ArchiveUtil.buildRsWebArchive();
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	boolean sameDB = true;

	public TestReportService() {
		super();
		servicePrefixString = "reportrs/query";
	}

	@Before
	public void deleteTestQuery() throws SQLException {
		Connection connection = null;
		if (sameDB) {
			connection = Util.getDerbyRSConnection();
		} else {
			connection = Util.getPostgresConnection();
		}
		connection.setAutoCommit(true);
		connection
				.createStatement()
				.execute(
						"DELETE FROM defaulttypedquery_conditions as d  where exists( select t.id from typedquery t where d.DefaultTypedQuery_ID=t.id and t.name like  'TestGenerated %')");
		connection.createStatement().execute(
				"DELETE FROM fxreport as d  where exists( select t.id from typedquery t where d.query_id=t.id and t.name like  'TestGenerated %')");
		connection.createStatement().execute("DELETE FROM TYPEDQUERY WHERE name like 'TestGenerated %'");
	}

	public FleximsDynamicEntityImpl createEntityImpl(String name) {
		return JpaHelper.createNewEntity(em, name);
	}

	static String testName = "Basictype";
	static long testid = 0;

	@Test
	public void testStatus() {
		String ret = target.path("status").request(MediaType.TEXT_PLAIN).get(String.class);
		assertThat(ret, equalTo("ok"));
	}

	public ConditionQuery createSimpleQuery(String type, String prop, Operator op) {
		ConditionQuery studentname = QueryHelper.createSimpleQuery(type, prop, op, em);
		studentname.setQueryName("TestGenerated " + index++);
		return studentname;
	}

	public ConditionQuery saveQuery(ConditionQuery query) {
		List<FleximsDynamicEntityImpl> conds = new ArrayList<>(3);
		for (PropertyCondition pc : query.getConditions()) {
			conds.add(pc.getEntity());
		}
		query.getEntity().set(ConditionQuery.PROP_NAME_PROP_CONDITION, conds);

		FleximsDynamicEntityImpl retEntity = postJson(jerseyClientRule.target.path("rs/inst/save"), query.getEntity());
		return new ConditionQuery(retEntity);

	}

	public static int index = 0;
	public String subname = "";

	/**
	 * Query a build with all its students's name like
	 * 
	 * @return
	 */
	protected ConditionQuery createTraceDown() {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		ConditionQuery query = null;
		PropertyCondition pc = null;

		ConditionQuery studentname = createSimpleQuery(relatedTypeString, "Name", Operator.like);

		studentname = saveQuery(studentname);
		subname = studentname.getQueryName();

		query = createSimpleQuery(qType, propName, Operator.tracedown);

		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentname);
		pc.setAllColllectionMode(); // A build with all its students's name end
									// with 2. room 603, students 6, 7,
		query = saveQuery(query);
		return query;

	}

	@Test
	public void testAllTraceDown() {
		ConditionQuery query = createTraceDown();
		Entities ret = getJson(target.path("alltracedown").path(String.valueOf(query.getEntity().getId())));
		assertEquals(ret.getItems().size(), 2);
		FleximsDynamicEntityImpl subquery = (FleximsDynamicEntityImpl) ret.getItems().get(1);
		assertThat((String) subquery.get("Name"), equalTo(subname));
	}

	public void deleteInst(FleximsDynamicEntityImpl entityImpl) {
		deleteJson(jerseyClientRule.target.path("rs/inst/delete").path(entityImpl.getClass().getSimpleName())
				.path(String.valueOf(entityImpl.getId())));
	}

	@Test
	public void testAllReport() {
		Entities ret = getJson(target.path("allreportbytype").path("Basictype"));
		assertEquals(ret.getItems().size(), 2);

		FleximsDynamicEntityImpl reportDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(0);
		FleximsDynamicEntityImpl queryDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(1);

		deleteInst(reportDynamicEntityImpl);
		deleteInst(queryDynamicEntityImpl);
		FleximsDynamicEntityImpl entityImpl = getJson(jerseyClientRule.target.path("rs/inst/get")
				.path(reportDynamicEntityImpl.getClass().getSimpleName()).path(String.valueOf(reportDynamicEntityImpl.getId())));
		assertNull(entityImpl);

		// we should sitll have report
		ret = getJson(target.path("allreportbytype").path("Basictype"));
		assertEquals(ret.getItems().size(), 2);

	}

	@Test
	public void testQueryReportByName() {
		// make sure we have query/report
		Entities ret = getJson(target.path("allreportbytype").path("Basictype"));
		FleximsDynamicEntityImpl reportDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(0);
		FleximsDynamicEntityImpl queryDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(1);

		FleximsDynamicEntityImpl queryDynamicEntityImpl1 = getJson(target.path("querybyname").path(
				(String) queryDynamicEntityImpl.get(TypedQuery.PROP_NAME_NAME)));
		assertEquals(queryDynamicEntityImpl.getId(), queryDynamicEntityImpl1.getId());
		Entities ret1 = getJson(target.path("reportbyname").path((String) reportDynamicEntityImpl.get(FxReportWrapper.PROP_NAME_PROP_NAME)));
		FleximsDynamicEntityImpl reportDynamicEntityImpl1 = (FleximsDynamicEntityImpl) ret1.getItems().get(0);
		assertEquals(reportDynamicEntityImpl.getId(), reportDynamicEntityImpl1.getId());
	}

	@Test
	public void testQueryReportByType() {
		// make sure we have query/report
		Entities ret = getJson(target.path("allreportbytype").path("Basictype"));

		Entities queries = getJson(target.path("querybytype").path("Basictype"));
		assertTrue(queries.getItems().size() > 0);

		Entities reports = getJson(target.path("reportbytype").path("Basictype"));
		assertTrue(reports.getItems().size() > 0);
	}

	@Test
	public void testReportById() {
		// make sure we have query/report
		Entities ret = getJson(target.path("allreportbytype").path("Basictype"));
		FleximsDynamicEntityImpl reportDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(0);
		FleximsDynamicEntityImpl queryDynamicEntityImpl = (FleximsDynamicEntityImpl) ret.getItems().get(1);

		Entities ret1 = getJson(target.path("reportbyid").path(String.valueOf(reportDynamicEntityImpl.getId())));
		assertTrue(ret1.getItems().size() > 0);

		FleximsDynamicEntityImpl reportDynamicEntityImpl1 = (FleximsDynamicEntityImpl) ret1.getItems().get(0);
		FleximsDynamicEntityImpl queryDynamicEntityImpl1 = (FleximsDynamicEntityImpl) ret1.getItems().get(1);
		assertEquals(reportDynamicEntityImpl.getId(), reportDynamicEntityImpl1.getId());
		assertEquals(queryDynamicEntityImpl.getId(), queryDynamicEntityImpl1.getId());
	}

	@Test
	public void testQueryFlow() {
		ConditionQuery query = createTraceDown();
		FleximsDynamicEntityImpl entityImpl = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		FxReportWrapper report = new FxReportWrapper();
		report.setEntity(entityImpl);
		report.setQuery(query);

		// display properties
		report.setProperties("name");

		// order field
		List<OrderField> os = new ArrayList<>(1);
		os.add(new OrderField("name", OrderDirection.ASC));
		report.setOrderBy(os);

		// param value
		ArrayList<QueryParamValue> params = new ArrayList<>();
		QueryParamValue paramValue = new QueryParamValue();
		paramValue.setIndex(0);
		paramValue.setValue("ja%");
		params.add(paramValue);
		report.setParams(params);

		// repare
		report = postJson(target.path("prepare"), report);
		System.out.println("UUID:" + report.getUuid());
		System.out.println("Count:" + report.count);
		assertNotNull(report.uuid);

		// without offset, len
		getJson(target.path("fetch").path(report.getUuid()));
		// without len
		getJson(target.path("fetch").path(report.getUuid()).path("0"));

		// fetch result: full url
		Entities result = getJson(target.path("fetch").path(report.getUuid()).path("0").path("5"));

		Entities allResult = getJson(target.path("fetchall").path(report.getUuid()));
		try {
			RSMsg msg = getJson(target.path("fetchall1").path(report.getUuid()));
			// web application code is catched properly.
			fail("should have a 404 exception");
		} catch (NotFoundException e) {
			// if reached here, it is handld correctly.
		}

		deleteJson(target.path("destory").path(report.getUuid()));
		try {
			RSMsg msg = getJson(target.path("fetchall").path(report.getUuid()));
			fail("Can not request after destory");
		} catch (InternalServerErrorException e) {

		}

	}
}