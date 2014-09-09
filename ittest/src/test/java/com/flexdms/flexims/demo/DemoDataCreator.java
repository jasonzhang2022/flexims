package com.flexdms.flexims.demo;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

import mockit.Mock;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.accesscontrol.ACLInitializer;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.PermissionChecker;
import com.flexdms.flexims.accesscontrol.PermissionResult;
import com.flexdms.flexims.it.DataUtil;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.RequestScopeRule;
import com.flexdms.flexims.unit.TestEntityUtil;
import com.flexdms.flexims.unit.TestOXMSetup;
import com.flexdms.flexims.unit.Util;
import com.flexdms.flexims.users.FxUserSettings;

public class DemoDataCreator {
	public static class PostgresqlClientSetup extends ExternalResource {
		@Override
		protected void before() throws Throwable {
			System.err.println("-----------set up client ORM for test");
			// use the network version Derby
			TestOXMSetup.persistenceXml = "persistence_postgres.xml";
			TestOXMSetup.EntityManagerFactoryProviderInternal.setup();
			// use the mock
			new TestOXMSetup.AppMock(Util.getPostgresConnection());
		};

		@Override
		protected void after() {

		};
	}

	/**
	 * Bean in session scope such as PermssionChecker, RoleContext,
	 * QueryAuthorizer ar enot lookedup from Session Scope, Instead, they are
	 * wired by by Decapsulation method from mockit.
	 */
	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule()).around(new PostgresqlClientSetup()).around(new ExternalResource() {
		protected void before() throws Throwable {
			ACLInitializer.securityUnit = "fxsecuritytest";
		}
	}).around(new AppInitializerRule());

	@Rule
	public EntityManagerRule emRule = new EntityManagerRule();
	@Rule
	public RequestScopeRule requestScopeRule = new RequestScopeRule();

	EntityManager em;

	@Before
	public void deleteTestQuery() throws SQLException {
		em = emRule.em;

	}

	protected void deleteData(String type) {
		String qlString = "delete from " + type;
		javax.persistence.Query query = em.createQuery(qlString);
		query.executeUpdate();

	}

	@Test
	public void addTestUsers() {		
		com.flexdms.flexims.users.unit.DataUtil.createTestUsers(em);
	}

	@Test
	public void addBasicType() {
		String typename = "Basictype";
		// delete all to void constraint error
		em.getTransaction().begin();
		deleteData(typename);
		em.getTransaction().commit();

		DataUtil.prepareBasictype(em);
		;
	}

	/**
	 * Build Porter with 200 rooms Build Kmart with 200 room
	 * 
	 * College Arts with 200 Courses College Engineer witj 200 courses
	 * 
	 * 200 OneManys
	 * 
	 * @throws SQLException
	 */
	@Test
	public void addStudentsRelated() throws SQLException {
		DataUtil.studentForRelationEditor(em);
	}
}
