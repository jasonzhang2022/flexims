package com.flexdms.flexims.jpa.rs;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;

import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * Manage client files (files uploaded from client) and server files
 * 
 * @author jason.zhang
 * 
 */
@Path("/res")
@ApplicationScoped
public class ResourceService {

	
	@Path("/web")
	public WebResource getWebResource() {
		return BeanProvider.getContextualReference(WebResource.class);
	}
	
	@Path("/module")
	public ModuleResource getModuleResource() {
		return BeanProvider.getContextualReference(ModuleResource.class);
	}
	
	@Path("/context")
	public JSContextResource getJSContextResource() {
		return BeanProvider.getContextualReference(JSContextResource.class);
	}
	
	

}
