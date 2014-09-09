package com.flexdms.flexims.module;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());
	public RSApplication() {
		//packages(RSApplication.class.getPackage().getName());
		register(ModuleService.class);
		register(ModuleDefinition.class);
		RsHelper.configRSApp(this);
		LOGGER.info("module service is initialized");
	}
}
