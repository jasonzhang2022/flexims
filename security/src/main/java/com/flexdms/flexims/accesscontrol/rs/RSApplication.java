package com.flexdms.flexims.accesscontrol.rs;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());

	public RSApplication() {
		// packages(RSApplication.class.getPackage().getName());
		register(ACLService.class);
		register(InstanceACES.class);
		RsHelper.configRSApp(this);
		LOGGER.info("security(acl) service is initialized");
	}
}
