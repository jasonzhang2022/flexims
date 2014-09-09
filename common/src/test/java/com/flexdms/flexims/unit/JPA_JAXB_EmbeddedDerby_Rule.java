package com.flexdms.flexims.unit;

import org.junit.rules.ExternalResource;

public class JPA_JAXB_EmbeddedDerby_Rule extends ExternalResource {
	@Override
	protected void before() throws Throwable {
		System.err.println("-----------set up client ORM for test");
		Util.removeUnitDB();
		TestOXMSetup.EntityManagerFactoryProviderInternal.setup();
		// use the mock
		new TestOXMSetup.AppMock(Util.getConnection());

	};

}
