package com.flexdms.flexims.rsutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

@Provider
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class JaxbJsonProvider<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

	@Inject
	JaxbHelper jaxbHelper;

	public JaxbHelper getJaxbHelper() {
		return jaxbHelper;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (JaxbHelper.classesList.contains(type.getName())) {
			return true;
		}
		if (FleximsDynamicEntityImpl.class.isAssignableFrom(type)) {
			return true;
		}

		return false;
	}

	@Override
	public long getSize(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(T t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {

		if (RsHelper.isJsonType(mediaType)) {
			getJaxbHelper().toJson(t, new OutputStreamWriter(entityStream));
		} else {
			getJaxbHelper().toXml(t, new OutputStreamWriter(entityStream));
		}
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (JaxbHelper.classesList.contains(type.getName())) {
			return true;
		}
		if (FleximsDynamicEntityImpl.class.isAssignableFrom(type)) {
			return true;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException {
		if (RsHelper.isJsonType(mediaType)) {
			return (T) getJaxbHelper().fromJson(new InputStreamReader(entityStream), App.getEM());
		} else {
			return (T) getJaxbHelper().fromXml(new InputStreamReader(entityStream), App.getEM());
		}

	}

}
