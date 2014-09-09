package com.flexdms.flexims.unit.security;

import java.sql.SQLException;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import mockit.Mock;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.App;
import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.ACLInitializer;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.Decision;
import com.flexdms.flexims.accesscontrol.PermissionChecker;
import com.flexdms.flexims.accesscontrol.PermissionResult;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.WatcherAction;
import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.PropertyPermission;
import com.flexdms.flexims.accesscontrol.model.RolePermission;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.Util;

public class JPATest {

	EntityManager securityEm;
	
	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule())
	.around(new JPA_JAXB_EmbeddedDerby_Rule())
	.around(new ExternalResource() {
		protected void before() throws Throwable {
			ACLInitializer.securityUnit = "fxsecuritytest";
		}
	})
	.around(new AppInitializerRule());


	@Before
	public void emsetup() throws JAXBException, SQLException {
		securityEm = ACLHelper.getSecurityEM();
	}

	@After
	public void teardown() {
		securityEm.close();
	}

	@Test
	public void testInstanceACE() {
		InstanceACE ace = new InstanceACE("test", 50l);
		ace.setOrder(0);

		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(999l);
		permission.setActions(Arrays.asList(new EditAction(), new WatcherAction()));

		ace.setRolePermission(permission);
		securityEm.getTransaction().begin();
		securityEm.persist(ace);
		securityEm.getTransaction().commit();

	}

	@Test
	public void testTypeACL() {
		securityEm.getTransaction().begin();
		TypeACL typeACL = securityEm.find(TypeACL.class, "test");
		if (typeACL != null) {
			securityEm.remove(typeACL);
		}
		securityEm.getTransaction().commit();

		typeACL = null;
		typeACL = new TypeACL();
		typeACL.setTypeid("test");
		typeACL.setAclParentTypes(Arrays.asList("p1", "p2"));

		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(999l);
		permission.setActions(Arrays.asList(new EditAction(), new WatcherAction()));

		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(998l);
		permission1.setActions(Arrays.asList(new EditAction(), new WatcherAction()));

		typeACL.setRolePermissions(Arrays.asList(permission, permission1));

		PropertyPermission pp = new PropertyPermission();
		pp.setDecision(Decision.Allow);
		pp.setPropName("prop1");
		pp.setActions(Arrays.asList(new EditAction(), new WatcherAction()));

		PropertyPermission pp1 = new PropertyPermission();
		pp1.setDecision(Decision.Allow);
		pp1.setPropName("prop2");
		pp1.setActions(Arrays.asList(new EditAction(), new WatcherAction()));

		typeACL.setPropPermissions(Arrays.asList(pp, pp1));
		securityEm.getTransaction().begin();
		securityEm.persist(typeACL);
		securityEm.getTransaction().commit();
	}
}
