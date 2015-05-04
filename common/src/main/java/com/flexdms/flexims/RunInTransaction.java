package com.flexdms.flexims;

import java.util.concurrent.Callable;

import javax.enterprise.context.Dependent;
import javax.transaction.Transactional;

@Dependent
public class RunInTransaction {

	@Transactional 
	public <T> T call(Callable<T> call) throws Exception {
		return call.call();
		
	}
	
	@Transactional 
	public void run(Runnable runnable) throws Exception {
		runnable.run();
		
	}
}
