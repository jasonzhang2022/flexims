package com.flexdms.flexims.jpa.helper;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.converters.Converter;
import org.eclipse.persistence.sessions.Session;

public class JAXBConverter implements Converter {

	public static final int OBJECT_JAXB_SIZE = 2048;
	private static final long serialVersionUID = 1L;
	String rootClass;
	JAXBContext jc;

	public JAXBConverter() {

	}

	public void initJAXBContext() {
		try {
			Class<?>[] clsz = { Class.forName(rootClass) };
			jc = JAXBContext.newInstance(clsz);
		} catch (Exception e1) {
			throw new RuntimeException(e1);
		}
	}

	@Override
	public Object convertObjectValueToDataValue(Object objectValue, Session session) {
		if (objectValue == null) {
			return null;
		}
		try {
			Marshaller marshaller = jc.createMarshaller();
			// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter(OBJECT_JAXB_SIZE);
			marshaller.marshal(objectValue, sw);
			return sw.toString();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public Object convertDataValueToObjectValue(Object dataValue, Session session) {
		if (dataValue == null) {
			return null;
		}
		Unmarshaller unmarshaller;
		try {
			unmarshaller = jc.createUnmarshaller();
			String s = (String) dataValue;
			StringReader reader = new StringReader(s);
			Object t1 = unmarshaller.unmarshal(reader);
			return t1;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public void initialize(DatabaseMapping mapping, Session session) {
		rootClass = (String) mapping.getProperty("rootClass");
		initJAXBContext();

	}

}
