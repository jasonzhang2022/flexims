package com.flexdms.flexims.unit;

import javax.persistence.EntityManager;

import org.junit.rules.ExternalResource;

import com.flexdms.flexims.App;
import com.flexdms.flexims.util.ThreadContext;

/**
 * Create a thread-local Entity Manager so any code can use App.getEM()
 * 
 * @author jason.zhang
 * 
 */
public class EntityManagerRule extends ExternalResource {
	public EntityManager em;

	@Override
	protected void before() throws Throwable {

		em = App.getEM();

	};

	@Override
	protected void after() {

		em.close();
		ThreadContext.remove(App.EM_KEY);
	};
}