package com.flexdms.flexims.auth;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());
	public RSApplication() {
		//packages(RSApplication.class.getPackage().getName());
		register(AuthService.class);
		register(LoginInfo.class);
		RsHelper.configRSApp(this);
		LOGGER.info("auth service is initialized");
	}
}
