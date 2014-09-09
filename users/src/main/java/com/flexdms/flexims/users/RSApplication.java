package com.flexdms.flexims.users;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public RSApplication() {
		// packages(RSApplication.class.getPackage().getName());
		register(RolesService.class);
		RsHelper.configRSApp(this);
		LOGGER.info("role/users service is initialized");
	}

	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());
}
