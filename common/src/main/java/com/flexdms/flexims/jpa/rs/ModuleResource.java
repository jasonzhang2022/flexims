package com.flexdms.flexims.jpa.rs;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.servlet.ServletException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.apache.commons.io.IOUtils;

@ApplicationScoped
public class ModuleResource implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Static cache is fine since we do not modify the resources
	 */
	// javascript extnesion used by instance
	String instJsFile = null;

	// css extension used by instance
	String instCssFile = null;

	// thirdpathy library used by instance
	String thirdPartyJsFile = null;

	// javascript extension used by type application.
	String typeJsFile = null;

	private String lastResrcName(String name) {
		return name.substring(name.lastIndexOf('/') + 1);
	}

	public static class URLComparator implements Comparator<URL>{

		@Override
		public int compare(URL o1, URL o2) {
			return o1.toExternalForm().compareTo(o2.toExternalForm());
		}
		
	}
	@PostConstruct
	public void init() throws ServletException {
		try {
			Writer out =new StringWriter(10000);
			List<URL> resources = CPScanner.scanResources(new ResourceFilter().packageName("META-INF.resources.inst").resourceName("*.js"));
			if (resources != null) {
				Collections.sort(resources, new URLComparator());
				for (URL res : resources) {
					out.write("//------" + lastResrcName(res.toExternalForm()) + "\n");
					IOUtils.copy(res.openStream(), out);
					out.write("//------ end of " + res.toExternalForm() + "\n");

				}
			}
			instJsFile=out.toString();
			out.close();
			resources = null;

			out = new StringWriter(1000);
			resources = CPScanner.scanResources(new ResourceFilter().packageName("META-INF.resources.inst").resourceName("*.css"));
			if (resources != null) {
				Collections.sort(resources, new URLComparator());
				for (URL res : resources) {
					out.write("/*------" + lastResrcName(res.toExternalForm()) + "*/\n");
					IOUtils.copy(res.openStream(), out);
					out.write("/*------ end of " + res.toExternalForm() + "*/\n");

				}
			}
			instCssFile=out.toString();
			out.close();
			resources = null;

			out =new StringWriter(10000);
			resources = CPScanner.scanResources(new ResourceFilter().packageName("META-INF.resources.thirdparty").resourceName("*.js"));
			if (resources != null) {
				Collections.sort(resources, new URLComparator());
				for (URL res : resources) {
					out.write("//------" + lastResrcName(res.toExternalForm()) + "\n");
					IOUtils.copy(res.openStream(), out);
					out.write("//------ end of " + res.toExternalForm() + "\n");

				}
			}
			thirdPartyJsFile=out.toString();
			out.close();
			resources=null;

			out = new StringWriter(10000);
			resources = CPScanner.scanResources(new ResourceFilter().packageName("META-INF.resources.type").resourceName("*.js"));
			if (resources != null) {
				Collections.sort(resources, new URLComparator());
				for (URL res : resources) {
					out.write("//------" + lastResrcName(res.toExternalForm()) + "\n");
					IOUtils.copy(res.openStream(), out);
					out.write("//------ end of " + res.toExternalForm() + "\n");

				}
			}
			typeJsFile=out.toString();
			out.close();
			resources=null;

		} catch (IOException e) {
			throw new ServletException(e);
		}

	}

	@javax.ws.rs.Path("/inst")
	@GET
	@Produces("text/javascript")
	public String getInstJs() throws IOException {
		return instJsFile;
	}

	@javax.ws.rs.Path("/thirdparty")
	@GET
	@Produces("text/javascript")
	public String getThirdPartyJs() throws IOException {
		return thirdPartyJsFile;
	}

	@javax.ws.rs.Path("/type")
	@GET
	@Produces("text/javascript")
	public String getTypeJs() throws IOException {
		return typeJsFile;
	}

	@javax.ws.rs.Path("/instcss")
	@GET
	@Produces("text/css")
	public String getInstCss() throws IOException {
		return instCssFile;
	}


}
