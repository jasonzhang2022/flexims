package com.flexdms.flexims.unit;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.junit.rules.ExternalResource;

import com.flexdms.flexims.App;
import com.flexdms.flexims.AppInitializer.AppInitalizeContext;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.util.ThreadContext;

public class AppInitializerRule extends ExternalResource {

	@Override
	protected void before() throws Throwable {
		AppInitalizeContext appInitalizeContext = new AppInitalizeContext();
		EntityManagerFactoryProvider entityManagerFactoryProvider = App.getPersistenceUnit();
		// initialize Persister locader
		entityManagerFactoryProvider.getEMF();

		appInitalizeContext.em = App.getEM();

		try {
			BeanManager beanManager = CDI.current().getBeanManager();
			beanManager.fireEvent(appInitalizeContext);
		} finally {
			ThreadContext.remove(App.EM_KEY);
			appInitalizeContext.em.close();
		}
	}

	@Override
	protected void after() {

	};

}