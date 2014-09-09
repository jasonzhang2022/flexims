package com.flexdms.flexims.unit.rs.report.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.report.rs.QueryParamValues;

public class TestQueryParamValues {

	@Test
	public void test() throws JAXBException {
		QueryParamValues paramValues = new QueryParamValues();

		paramValues.addParamValues("test", "test1");
		paramValues.addParamValues();
		paramValues.addParamValues("test3", "test4");

		JAXBContext ctxContext = JaxbHelper.createMoxyJaxbContext(QueryParamValues.class);
		Marshaller marshaller = ctxContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(paramValues, System.out);

		ctxContext = org.eclipse.persistence.jaxb.JAXBContext.newInstance(QueryParamValues.class);
		marshaller = ctxContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.marshal(paramValues, System.out);
	}
}
