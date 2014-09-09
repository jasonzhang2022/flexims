package com.flexdms.flexims.rsutil;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Variant;
import javax.ws.rs.ext.Provider;

import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;

public final class RsHelper {
	private RsHelper() {
	}
	public static final List<Variant> VS;
	static {
		VS = Variant.mediaTypes(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_XML_TYPE).languages(Locale.getDefault()).build();
	}

	public static boolean isJsonType(MediaType mediaType) {
		if (MediaType.APPLICATION_JSON_TYPE.getType().equalsIgnoreCase(mediaType.getType())
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype().equalsIgnoreCase(mediaType.getSubtype())) {
			return true;
		}
		return false;
	}

	public static boolean isXml(Request request) {
		Variant variant = request.selectVariant(VS);
		return !isJsonType(variant.getMediaType());
	}

	@Provider
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public static class EntitiesReaderWriter extends JaxbJsonProvider<Entities> {

	}

	public static void configRSApp(Configurable<ResourceConfig> resourceConfig) {
		resourceConfig.register(XMLEntityMappingsReaderWriter.class).register(ExceptionWriter.class).register(EntitiesReaderWriter.class)
				.register(JaxbJsonProvider.class)
				// when RsMsg is constructed in ExpetionWriter. Default
				// MpxyJsonProvider is used.
				// The configuration below is used by default Json provider.
				.register(new MoxyJsonConfig().setIncludeRoot(true).setFormattedOutput(true).resolver());
	}
}
