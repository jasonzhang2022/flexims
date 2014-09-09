package com.flexdms.flexims.unit.jaxb.moxy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContext;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.junit.Test;

import com.flexdms.flexims.jpa.helper.NameValueList;

/**
 * Test the moxy example from eclipselink page can run. Make sure eclipselink
 * library is good.
 * 
 * @author jason.zhang
 * 
 */
public class MoxyExampleTest {

	@Test
	public void TestExample() throws JAXBException {
		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream iStream = myClassLoader.getResourceAsStream("com/flexdms/flexims/unit/moxy/customer.xml");

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
		DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(myClassLoader, properties);
		DynamicEntity newCustomer = jaxbContext.newDynamicEntity("example.Customer");
		newCustomer.set("firstName", "George");
		newCustomer.set("lastName", "Jones");
		DynamicEntity newAddress = jaxbContext.newDynamicEntity("example.Address");
		newAddress.set("street", "227 Main St.");
		newAddress.set("city", "Toronto");
		newAddress.set("province", "Ontario");
		newAddress.set("postalCode", "M5V1E6");
		newCustomer.set("address", newAddress);
		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(newCustomer, writer);

		String string = writer.toString();
		System.out.println(string);
		DynamicEntity dEntity = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(new StringReader(string));
		assertEquals("George", dEntity.get("firstName"));

		// json
		writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.marshal(newCustomer, writer);

		string = writer.toString();
		System.out.println(string);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		DynamicEntity dEntity1 = (DynamicEntity) unmarshaller.unmarshal(new StringReader(string));
		assertEquals("George", dEntity1.get("firstName"));

	}

	@Test
	public void testNameValueList() throws JAXBException {

		Map<String, String> map = new HashMap<>();
		map.put("firstname", "jason");
		map.put("lastname", "zhang");
		JAXBContext jaxbContext = JAXBContext.newInstance(NameValueList.class);
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(NameValueList.fromMap(map), System.out);

	}

	@Test
	public void testNameValueListInDynamic() throws JAXBException {
		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream iStream = myClassLoader.getResourceAsStream("com/flexdms/flexims/unit/moxy/namevaluelist.xml");

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
		DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(myClassLoader, properties);
		DynamicEntity newCustomer = jaxbContext.newDynamicEntity("example.Customer");
		newCustomer.set("firstName", "George");
		newCustomer.set("lastName", "Jones");
		Map<String, String> map = new HashMap<>();
		map.put("firstname", "jason");
		map.put("lastname", "zhang");
		newCustomer.set("map", NameValueList.fromMap(map));

		Map<String, String> map2 = new HashMap<>();
		map2.put("firstname", "jason");
		map2.put("lastname", "zhang");
		newCustomer.set("map2", map2);

		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(newCustomer, writer);

		String string = writer.toString();
		System.out.println(string);
		DynamicEntity dEntity = (DynamicEntity) jaxbContext.createUnmarshaller().unmarshal(new StringReader(string));
		assertEquals("George", dEntity.get("firstName"));

		// json
		writer = new StringWriter();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.marshal(newCustomer, writer);

		string = writer.toString();
		System.out.println(string);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		DynamicEntity dEntity1 = (DynamicEntity) unmarshaller.unmarshal(new StringReader(string));
		assertEquals("George", dEntity1.get("firstName"));

	}

	// wrong xsd is generated
	@Test
	public void testXsdInheritance() throws JAXBException {

		ClassLoader myClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream iStream = myClassLoader.getResourceAsStream("com/flexdms/flexims/unit/moxy/inheritance.xml");

		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, iStream);
		DynamicJAXBContext jaxbContext = DynamicJAXBContextFactory.createContextFromOXM(myClassLoader, properties);
		jaxbContext.generateSchema(new SchemaOutputResolver() {

			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				StreamResult result = new StreamResult(System.out);
				// result.setSystemId(file.toURI().toURL().toString());
				return result;
			}
		});
	}
}
