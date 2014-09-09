package com.flexdms.flexims.unit;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.junit.rules.ExternalResource;

/**
 * Initialize a CDIContext
 * 
 * @author jason.zhang
 * 
 */
public class CDIContainerRule extends ExternalResource {
	public static CdiContainer cdiContainerdiContainer;

	@Override
	protected void before() throws Throwable {
		System.err.println("-----------initialzie weld");
		System.getProperties().setProperty("org.jboss.weld.se.archive.isolation", "false");
		cdiContainerdiContainer = CdiContainerLoader.getCdiContainer();
		cdiContainerdiContainer.boot();
	}

	@Override
	protected void after() {
		cdiContainerdiContainer.shutdown();
	};

}