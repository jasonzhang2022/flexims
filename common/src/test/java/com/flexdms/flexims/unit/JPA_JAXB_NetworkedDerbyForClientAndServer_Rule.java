package com.flexdms.flexims.unit;

import org.junit.rules.ExternalResource;

public class JPA_JAXB_NetworkedDerbyForClientAndServer_Rule extends ExternalResource {
	@Override
	protected void before() throws Throwable {
		System.err.println("-----------set up client ORM for test");
		Util.removeRSDB();
		Util.bootRsNetworkDB();
		// use the network version Derby
		TestOXMSetup.persistenceXml = "persistence_networkderby.xml";
		TestOXMSetup.EntityManagerFactoryProviderInternal.setup();
		// use the mock
		new TestOXMSetup.AppMock(Util.getDerbyRSConnection());
	};

	@Override
	protected void after() {
		Util.removeRSDB();
		Util.shutdownRsNetworkDB();
	};
}
