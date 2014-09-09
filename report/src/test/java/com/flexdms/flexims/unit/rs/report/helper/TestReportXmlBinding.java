package com.flexdms.flexims.unit.rs.report.helper;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.RequestScopeRule;
import com.flexdms.flexims.unit.TestOXMSetup;
import com.flexdms.flexims.util.Utils;

@RunWith(JUnit4.class)
public class TestReportXmlBinding {
	@ClassRule
	public static CDIContainerRule cdiContainerRule = new CDIContainerRule();
	@ClassRule
	public static JPA_JAXB_EmbeddedDerby_Rule clientSetupRule = new JPA_JAXB_EmbeddedDerby_Rule();

	@Rule
	public EntityManagerRule eManagerRule = new EntityManagerRule();
	@Rule
	public RequestScopeRule requestScopeRule = new RequestScopeRule();

	EntityManager em;
	JaxbHelper helper;
	@Before
	public void emsetup() throws JAXBException, SQLException {
		em = eManagerRule.em;
		helper = new JaxbHelper();
		helper.init(TestOXMSetup.factory, TestOXMSetup.dcl);

	}

	@Test
	public void testReportJaxb() {
	

		ConditionQuery query = QueryUtil.createSimpleQuery("Basictype", "propint", Operator.eq, em);
		QueryUtil.saveQuery(query, em);

		FleximsDynamicEntityImpl entityImpl = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		FxReportWrapper report = new FxReportWrapper();
		report.setUuid("uuid");
		report.setCount(5);
		report.setEntity(entityImpl);
		report.setQuery(query);
		List<String> properties = new ArrayList<>(2);
		properties.add("shortstring");
		properties.add("propint");
		properties.add("propdate");
		report.setProperties(properties);

		// order by
		List<OrderField> os = new ArrayList<>(1);
		os.add(new OrderField("propint", OrderDirection.ASC));
		report.setOrderBy(os);

		report.addParamValue("3");
		report.addParamValue("test1", "test2");
		report.addParamValue(Utils.dateToString(new Date()), Utils.dateToString(new Date()));

		StringWriter sWriter = new StringWriter();
		helper.toXml(report, sWriter);
		System.out.println(sWriter.toString());

		sWriter = new StringWriter();
		helper.toJson(report, sWriter);
		String jsonString = sWriter.toString();
		System.out.println(sWriter.toString());

		FxReportWrapper report1 = (FxReportWrapper) helper.fromJson(new StringReader(jsonString), em);

		// params
		assertEquals(3, report1.getParams().size());
		assertThat(report1.getParams().get(0).getValue(), equalTo("3"));
		assertEquals(2, report1.getParams().get(1).getValuesAsArray().length);
		assertThat(report1.getParams().get(1).getValuesAsArray()[0], equalTo("test1"));
		assertThat(report1.getParams().get(1).getValuesAsArray()[1], equalTo("test2"));

		// order by
		assertEquals(1, report1.getOrderBy().size());
		assertThat(report.getOrderBy().get(0).getPropname(), equalTo("propint"));

		// query is loaded when unmarshall
		FleximsDynamicEntityImpl query1 = report.getEntity().get(FxReportWrapper.PROP_NAME_PROP_QUERY);
		assertNotNull(query1);
		assertEquals(query.getEntity().getId(), query1.getId());

	}

	@Test
	public void testGenerateSchemaJaxb() {
		helper.generateSchema();

	}

	/**
	 * If the jaxb mapping file is incorrect, the traceDown property can not
	 * refer to DefaultTypedQuery , the subtype of TypedQuery
	 */
	@Test
	public void testUnmarshall() {
		String jsonString = "{\"DefaultTypedQuery\":{\"id\":null,\"Name\":\"tracedown\",\"Description\":null,\"TargetedType\":\"Mdoomroom\",\"fxversion\":null,\"Reports\":null,\"Conditions\":[{\"Property\":\"student\",\"Operator\":\"tracedown\",\"RelativeStartDate\":null,\"RelativeStartUnit\":null,\"FirstValue\":null,\"RelativeEndDate\":null,\"RelativeEndUnit\":null,\"SecondValue\":null,\"Description\":null,\"IgnoreCase\":false,\"WholeTime\":true,\"CollectionMode\":\"all\",\"TraceDown\":10055}]}}";
		
		helper.input(new StringReader(jsonString), em, false, false);

	}

}
