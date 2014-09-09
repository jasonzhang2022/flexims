package com.flexdms.flexims.auth.unit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;

import com.flexdms.flexims.auth.LoginInfo;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;

/**
 * We can marshall and unmarshall javadoc
 * 
 * @author jason.zhang
 * 
 */
public class TestJaxb {

	@Test
	public void testJaxbLoginInfo() throws JAXBException {
		LoginInfo info = new LoginInfo();
		info.setUsername("username");
		info.setPassword("passwr");
		info.setOldPassword("oldpassword");
		info.setRememberMe(true);
		info.setEmail("x@t.com");

		JAXBContext context = JaxbHelper.createMoxyJaxbContext(LoginInfo.class);
		Marshaller marshaller = context.createMarshaller();
		StringWriter stringWriter = new StringWriter();
		marshaller.marshal(info, stringWriter);
		String xmlString = stringWriter.toString();
		System.out.println(xmlString);

		Marshaller jsonMarshaller = JaxbHelper.jsonMarshaller(context);
		stringWriter = new StringWriter();
		jsonMarshaller.marshal(info, stringWriter);
		String jsonString = stringWriter.toString();
		System.out.println(jsonString);

		Unmarshaller unmarshaller = context.createUnmarshaller();
		LoginInfo info1 = (LoginInfo) unmarshaller.unmarshal(new StringReader(xmlString));
		assertThat(info1.getEmail(), equalTo(info.getEmail()));

		unmarshaller = context.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		LoginInfo info2 = (LoginInfo) unmarshaller.unmarshal(new StringReader(jsonString));
		assertThat(info2.getEmail(), equalTo(info.getEmail()));

	}
}
