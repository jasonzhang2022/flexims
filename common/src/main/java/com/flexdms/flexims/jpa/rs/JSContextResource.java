package com.flexdms.flexims.jpa.rs;

import java.util.Enumeration;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.deltaspike.core.api.common.DeltaSpike;

import com.flexdms.flexims.config.ConfigItem;
import com.flexdms.flexims.config.Configs;

@ApplicationScoped
public class JSContextResource {

	public static final String JSON_KEY = "jsContextResource";

	@Inject
	@DeltaSpike
	ServletContext servletContext;

	@Path("/js")
	@GET
	@Produces("text/javascript")
	public String doGet() {
		JsonObjectBuilder builder = Json.createObjectBuilder().add("modelerprefix", servletContext.getContextPath());
		Enumeration<String> paranames = servletContext.getInitParameterNames();
		boolean hasformprefix = false;
		while (paranames.hasMoreElements()) {
			String name = paranames.nextElement();
			if (name.startsWith("web.")) {
				builder.add(name.substring(4), servletContext.getInitParameter(name));
			}
			if (name.equals("web.formprefix")) {
				hasformprefix = true;
			}
		}
		if (!hasformprefix) {
			builder.add("formprefix", servletContext.getContextPath());
		}
		JsonObjectBuilder configObject = Json.createObjectBuilder();

		for (ConfigItem config : Configs.getItems().values()) {
			if (config.isForClient() && !config.isForAdmin()) {
				configObject.add(config.getName(), config.getValue());
			}
		}
		builder.add("config", configObject);
		String jsonString = "var appctx=" + builder.build().toString() + ";";
		return jsonString;
	}

}
