package com.flexdms.flexims.unit.query;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.report.rs.QueryParamValue;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.unit.TestOXMSetup;

public class TestFxReportWrapper extends QueryTestBase {

	@Test
	public void testJaxb() {
		FleximsDynamicEntityImpl query = QueryHelper.createAllQuery("FxReport");

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

		JaxbHelper jaxbHelper = new JaxbHelper();
		jaxbHelper.init(TestOXMSetup.factory, TestOXMSetup.dcl);
		// round-trip for xml
		StringWriter stringWriter = new StringWriter();
		jaxbHelper.toXml(report, stringWriter);
		String xmlString = stringWriter.toString();
		System.out.print(xmlString);
		jaxbHelper.fromXml(new StringReader(xmlString), em);

		// round-trip for json
		stringWriter = new StringWriter();
		jaxbHelper.toJson(report, stringWriter);
		String jsonString = stringWriter.toString();
		System.out.print(jsonString);
		jaxbHelper.fromJson(new StringReader(jsonString), em);

	}
}
