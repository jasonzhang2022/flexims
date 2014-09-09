package com.flexdms.flexims.jpa.rs;

import java.util.logging.Logger;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.file.FileIDs;
import com.flexdms.flexims.file.FileInfos;
import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public RSApplication() {
		// packages(RSApplication.class.getPackage().getName());
		// registe class explicitly, in some situation, jersey can not find
		// classes from package
		register(FileService.class);
		register(TypeService.class);
		register(UtilService.class);
		register(InstService.class);
		register(ResourceService.class);
		register(FileIDs.class);
		register(FileInfos.class);
		RsHelper.configRSApp(this);
		// https://java.net/jira/browse/GLASSFISH-21033
		register(MultiPartFeature.class);
		LOGGER.info("basic service is initialized");
	}

	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());

}
