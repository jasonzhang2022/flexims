package com.flexdms.flexims.unit.security;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;

import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.Is;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.ACLInitializer;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;

public class TestACLInitializer {

	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule()).around(new JPA_JAXB_EmbeddedDerby_Rule())
			.around(new ExternalResource() {
				protected void before() throws Throwable {
					ACLInitializer.securityUnit = "fxsecuritytest";
					PermissionCheckerTest.removeSecurityDB();
				}
			});

	@Test
	public void testloadPredefinedAcls() throws IOException, SQLException {
		new ACLInitializer().init(null);
		EntityManager securityEntityManager = ACLHelper.emf.createEntityManager();
		securityEntityManager.getTransaction().begin();
		Connection connection = securityEntityManager.unwrap(Connection.class);
		Statement statement = connection.createStatement();
		ResultSet rSet=statement.executeQuery("select * from typepermissions where id=57");
		int count=0;
		while(rSet.next()) {
			count++;
		}
		assertEquals(count, 1);

		securityEntityManager.getTransaction().commit();
		securityEntityManager.close();
	}
}
