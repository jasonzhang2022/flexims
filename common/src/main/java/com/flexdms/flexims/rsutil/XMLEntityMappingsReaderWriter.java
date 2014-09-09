package com.flexdms.flexims.rsutil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;

@Provider
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class XMLEntityMappingsReaderWriter implements MessageBodyReader<XMLEntityMappings>, MessageBodyWriter<XMLEntityMappings> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (XMLEntityMappings.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	@Override
	public long getSize(XMLEntityMappings t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		if (XMLEntityMappings.class.isAssignableFrom(type)) {
			return true;
		}
		return false;
	}

	@Override
	public XMLEntityMappings readFrom(Class<XMLEntityMappings> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {

		if (RsHelper.isJsonType(mediaType)) {

			return MetaSourceBuilder.fromJson(new InputStreamReader(entityStream));

		} else {
			return MetaSourceBuilder.fromXml(new InputStreamReader(entityStream));
		}

	}

	@Override
	public void writeTo(XMLEntityMappings t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		if (RsHelper.isJsonType(mediaType)) {
			MetaSourceBuilder.toJson(t, new OutputStreamWriter(entityStream));
		} else {
			MetaSourceBuilder.toXml(t, new OutputStreamWriter(entityStream));
		}

	}

}
