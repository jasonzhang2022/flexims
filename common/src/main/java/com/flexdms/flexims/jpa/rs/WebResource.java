package com.flexdms.flexims.jpa.rs;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.common.DeltaSpike;

import com.flexdms.flexims.jpa.textstore.ClobData;
import com.flexdms.flexims.jpa.textstore.TextStoreEMF;

@ApplicationScoped
public class WebResource implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String RESOURCE_PREFIX = "web/";

	@Inject
	TextStoreEMF emf;

	@Inject @DeltaSpike
	ServletContext servletContext;

	String instTplJs = null;

	@PostConstruct
	public void init() throws IOException {
		Writer out = new StringWriter(2000);
		out.write("angular.module('instDirective').run(['$templateCache', function($templateCache) { \n");
		writeResourceDirectory("/template/", out);
		out.write("}]);\n");
		instTplJs = out.toString();
		System.out.print("WebResource is initialized");
	}

	protected void writeResourceDirectory(String name, Writer out) throws IOException {
		for (String res : servletContext.getResourcePaths(name)) {
			if (res.endsWith("/")) {
				writeResourceDirectory(res, out);
			} else {
				writeResourceFile(res, out);
			}
		}
	}

	protected void writeResourceFile(String name, Writer out) throws IOException {
		out.write("$templateCache.put('" + name.substring(1) + "', \n");
		String content = getTemplate(name, servletContext);
		List<String> lines = IOUtils.readLines(new StringReader(content));
		out.write("\"\"");
		for (String line : lines) {
			out.write( "+\"" + line.replace("\"", "\\\"") + "\"\n");
		}
		out.write("\n);\n");
	}

	@Path("/upload")
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public void upload(@FormParam("templatename") String templatename, @FormParam("content") String content) throws IOException {
		ClobData data = new ClobData(RESOURCE_PREFIX + templatename, content);
		emf.save(data);
		init();
	}

	@Path("{templatename: .+}")
	@GET
	@Produces(MediaType.WILDCARD)
	public String getTemplate(@PathParam("templatename") String templateName, @Context ServletContext context) throws IOException {
		ClobData clobData = emf.load(RESOURCE_PREFIX + templateName);
		if (clobData != null) {
			return clobData.getData();
		}

		InputStream inputStream = context.getResourceAsStream(templateName);
		if (inputStream != null) {
			return IOUtils.toString(inputStream);
		}
		return "";
	}

	@Path("/delete/{templatename: .+}")
	@DELETE
	public void deleteTemplate(@PathParam("templatename") String templateName) throws IOException {
		emf.delete(RESOURCE_PREFIX + templateName);
	}

	@Path("/inst.tpl.js")
	@GET
	@Produces("text/javascript")
	public String getInstTemplateModule() throws IOException {
		return instTplJs;
	}
}
