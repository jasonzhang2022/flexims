package com.flexdms.flexims.report.rs;

import java.util.logging.Logger;

import org.glassfish.jersey.server.ResourceConfig;

import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.rsutil.RsHelper;

public class RSApplication extends ResourceConfig {
	public static final Logger LOGGER = Logger.getLogger(RSApplication.class.getCanonicalName());

	public RSApplication() {
		// packages(RSApplication.class.getPackage().getName());
		register(ReportService.class);
		RsHelper.configRSApp(this);
		register(FxReportWrapper.class);
		LOGGER.info("report service is initialized");
	}
}
