package com.flexdms.flexims.file;

import javax.inject.Inject;

import org.apache.deltaspike.scheduler.api.Scheduled;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Schedule a background job to clean temporary file. Add one level indirection
 * so that the actual work instance comes from CDI and its method is
 * intercepted.
 */
// fire every five minutes
@Scheduled(cronExpression = "0 */5 * * * ?")
public class TempFileCleanupQuartzHook implements Job {

	@Inject
	TempFileCleanup actualWork;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		actualWork.run();

	}

}