package com.flexdms.flexims.unit.security;

import static mockit.Deencapsulation.setField;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.annotation.Annotation;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import javax.enterprise.event.Event;
import javax.enterprise.util.TypeLiteral;
import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import mockit.Mock;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.App;
import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.ACLInitializer;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.ActionContext;
import com.flexdms.flexims.accesscontrol.AuthorizedException;
import com.flexdms.flexims.accesscontrol.Decision;
import com.flexdms.flexims.accesscontrol.InstanceACLCache;
import com.flexdms.flexims.accesscontrol.PermissionChecker;
import com.flexdms.flexims.accesscontrol.PermissionResult;
import com.flexdms.flexims.accesscontrol.QueryAuthorizer;
import com.flexdms.flexims.accesscontrol.RoleContext;
import com.flexdms.flexims.accesscontrol.action.AllAction;
import com.flexdms.flexims.accesscontrol.action.CreateAction;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.QueryAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.accesscontrol.action.WatcherAction;
import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.InstanceACL;
import com.flexdms.flexims.accesscontrol.model.PropertyPermission;
import com.flexdms.flexims.accesscontrol.model.RolePermission;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.query.TypedQuery;
import com.flexdms.flexims.report.rs.ReportActionContext;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.RequestScopeRule;
import com.flexdms.flexims.unit.TestEntityUtil;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.RoleUtils;
import com.flexdms.flexims.users.unit.DataUtil;

public class PermissionCheckerTest {

	public static void removeSecurityDB() {
		try {
			// shut down embedded database connection if there is one.
			DriverManager.getConnection("jdbc:derby:memoryfxsecurity;shutdown=true", "flexims", "123456");
		} catch (SQLException e) {
			// ignore
		}
		System.out.println("---------------removed RS test database");
	}

	EntityManager securityEm;
	PermissionChecker checker;
	InstanceACLCache aclCache;
	EntityManager em;

	/**
	 * Bean in session scope such as PermssionChecker, RoleContext, QueryAuthorizer ar enot lookedup from 
	 * Session Scope, Instead, they are wired by by Decapsulation method from mockit.
	 */
	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule())
	.around(new JPA_JAXB_EmbeddedDerby_Rule())
	.around(new ExternalResource() {
		protected void before() throws Throwable {
			ACLInitializer.securityUnit = "fxsecuritytest";
			removeSecurityDB();
			
			new  mockit.MockUp<PermissionChecker>() {
				//do not let configuration affect our testing
				@Mock
				public PermissionResult getConfigedPermissionResult(Action action) {
					return PermissionResult.Undecided;
				}
			};
		}
	})
	.around(new AppInitializerRule())
	.around(new ExternalResource() {
		protected void before() throws Throwable {
			EntityManager eManager = App.getPersistenceUnit().getEMF().createEntityManager();
			DataUtil.createTestUsers(eManager);
			eManager.close();
		}
	});

	@Rule
	public EntityManagerRule eManagerRule = new EntityManagerRule();
	@Rule
	public RequestScopeRule requestScopeRule = new RequestScopeRule();





	@Before
	public void emsetup() throws JAXBException, SQLException {
		em = eManagerRule.em;

		securityEm = ACLHelper.getSecurityEM();
		checker = new PermissionChecker();
		aclCache = new InstanceACLCache();
		setField(aclCache, securityEm);
		setField(checker, aclCache);
		setField(checker, "actionEvent", new Event<ActionContext>() {

			@Override
			public void fire(ActionContext event) {
			}

			@Override
			public Event<ActionContext> select(Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
				return null;
			}

		});
		ACLHelper.typeacls.clear();

		em.clear();
		// remove all cache to that it can reloaded
		em.getEntityManagerFactory().getCache().evictAll();
		aclCache.clear();
	}

	@After
	public void teardown() {
		securityEm.close();

	}

	// permission defined on user
	public void constructChildTypeACL() {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxUser");

		// user 1 access
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(1l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 2 deny
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(2l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		typeACL.setRolePermissions(Arrays.asList(permission, permission1));

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
	}

	public void addUserTypeACLByRole() {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxUser");

		// user 1 access
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(101l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 2 deny
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(102l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		typeACL.setRolePermissions(Arrays.asList(permission, permission1));

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
	}

	// permission define in role. should be inherited by user
	public void addSuperTypeRule() {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxRole");

		// user 11 access
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(11l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 12 deny
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(12l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		typeACL.setRolePermissions(Arrays.asList(permission, permission1));

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);

	}

	// permission define in role. should be inherited by user
	public void addUserPropRuleAllow(boolean b) {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxUser");

		PropertyPermission permission = new PropertyPermission();
		permission.setPropName("IncludedBy");
		if (b) {
			permission.setDecision(Decision.Allow);
		} else {
			permission.setDecision(Decision.Deny);
		}

		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));
		typeACL.setPropPermissions(Arrays.asList(permission));
		permission.setTypeACL(typeACL);

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
	}

	// permission define in role. should be inherited by user
	public void addRolePropRuleAllow(boolean b) {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxRole");

		PropertyPermission permission = new PropertyPermission();
		permission.setPropName("IncludedBy");
		if (b) {
			permission.setDecision(Decision.Allow);
		} else {
			permission.setDecision(Decision.Deny);
		}

		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));
		typeACL.setPropPermissions(Arrays.asList(permission));
		permission.setTypeACL(typeACL);

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
	}

	@Test
	public void testActionAssociation() {
		Action editAction = ACLHelper.getActionByName(EditAction.NAME);
		Action readAction = ACLHelper.getActionByName(ReadAction.NAME);
		Action queryAction = ACLHelper.getActionByName(QueryAction.NAME);
		assertTrue(editAction.contain(readAction));
		assertTrue(editAction.contain(queryAction));

		Action allAction = ACLHelper.getActionByName(AllAction.NAME);
		assertTrue(allAction.contain(readAction));
		assertTrue(allAction.contain(editAction));
		assertTrue(allAction.contain(queryAction));

	}

	@Test
	public void testRoleInclusion() {
		FxUser admin = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 61l));
		assertTrue(admin.isIncludedBy(RoleUtils.getAdminRole()));

		FxUser user31 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 31l));
		FxRole grp3 = new FxRole((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxRole"), 103l));
		FxRole grp4 = new FxRole((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxRole"), 104l));
		FxRole grp5 = new FxRole((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxRole"), 105l));
		assertTrue(user31.isIncludedBy(grp3));
		assertFalse(user31.isIncludedBy(grp4));
		assertTrue(user31.isIncludedBy(grp5));

	}

	/**
	 * Rule 0: admin has all permission
	 */
	@Test
	public void testInstance_AdminShortChut() {
		FxUser Admin = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 61l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));
		PermissionResult result = null;
		// -----------------------allow case for instance
		for (Action action : ACLHelper.actions) {
			result = checker.checkPermission(action, Arrays.asList(Admin), "FxUser", user12.getEntityImpl(), false);
			assertEquals(result, PermissionResult.Allow);
		}

		// -----------------------allow case for type
		for (Action action : ACLHelper.actions) {
			result = checker.checkPermission(action, Arrays.asList(Admin), "FxUser", user12.getEntityImpl(), false);
			assertEquals(result, PermissionResult.Allow);
		}
	}

	/**
	 * rule 1 Check static role associated with instance
	 */
	@Test
	public void testInstance_StaticRole() {
		FxUser userTarget = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 61l));
		InstanceACL instanceACL = new InstanceACL(userTarget.getEntityImpl());

		// user 1 access UserTarget
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(11l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 2 denied UserTarget
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(12l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		InstanceACE ace1 = new InstanceACE("FxUser", userTarget.getId());
		ace1.setRolePermission(permission);

		InstanceACE ace2 = new InstanceACE("FxUser", userTarget.getId());
		ace2.setRolePermission(permission1);

		instanceACL.setAces(Arrays.asList(ace1, ace2));

		aclCache.addCache(instanceACL);

		FxUser user11 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user11), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user11), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user11), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

	}

	/**
	 * rule 2: Check propertyRule associated with Instance
	 */
	@Test
	public void testInstance_PropertyRuleSelf() {
		// anyone is included in IncludedBy group(in the same group) will be
		// allowed to access
		addUserPropRuleAllow(true);

		FxUser userTarget = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));
		FxUser user21 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 21l));

		PermissionResult result = null;
		// -----------------------allow case : user 12 is the same same group as
		// user11.
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------not the same group. Undecided.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// anyone is included in IncludedBy group(in the same group) will be
		// denied to access
		addUserPropRuleAllow(false);

		// -----------------------allow case : user 12 is the same same group as
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------not the same group. Undecided.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);
	}

	/**
	 * rule 3: Check Property Permission defined in super type.
	 */
	@Test
	public void testInstance_PropertyRuleFromSuperType() {
		// anyone is included in IncludedBy group(in the same group) will be
		// allowed to access
		addRolePropRuleAllow(true);

		FxUser userTarget = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));
		FxUser user21 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 21l));

		PermissionResult result = null;
		// -----------------------allow case : user 12 is the same same group as
		// user11.
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Allow);

		// undecided for creation
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------not the same group. Undecided.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// anyone is included in IncludedBy group(in the same group) will be
		// denied to access
		addUserPropRuleAllow(false);

		// -----------------------allow case : user 12 is the same same group as
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------not the same group. Undecided.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", userTarget.getEntityImpl(), false);
		assertEquals(result, PermissionResult.Undecided);
	}

	/**
	 * rule 4: Instance Inheritance Rule from its containing parent. room
	 * follows build containg it.
	 */
	@Test
	public void testInstance_ContainerRuleSelf() {
		FleximsDynamicEntityImpl student = TestEntityUtil.createAndPersisteDemoRelationObject(em);
		FleximsDynamicEntityImpl doomroom = student.get("doomroom");
		FleximsDynamicEntityImpl doombuild = student.get("doombuild");

		/*
		 * If we defined permission in build, room should follow it.
		 */
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("Mdoomroom");
		typeACL.setAclParentTypes(Arrays.asList("doombuild"));

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
		//
		InstanceACL instanceACL = new InstanceACL(doombuild);

		// user 1 access build
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(11l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 2 denied build
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(12l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		InstanceACE ace1 = new InstanceACE("FxUser", doombuild.getId());
		ace1.setRolePermission(permission);

		InstanceACE ace2 = new InstanceACE("FxUser", doombuild.getId());
		ace2.setRolePermission(permission1);

		instanceACL.setAces(Arrays.asList(ace1, ace2));
		aclCache.addCache(instanceACL);

		FxUser user11 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user11), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user11), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user11), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", doomroom, false);
		assertEquals(result, PermissionResult.Undecided);

	}

	/**
	 * rule 5: Instance Inheritance Rule from its containing parent. FxUser
	 * Follow the Rule in FxRole it belongs to.
	 */
	@Test
	public void testInstance_ContainerRuleFromSuperType() {

		/*
		 * To judge access for User 11. No access is defined for FxUser. But
		 * inheried rule is defined for FxRole(super type of FxUser). FxRole is
		 * included by IncludedBy relations(accidentally, it is fxRole, it can
		 * not different type). If we define permission in Related instance,
		 * that permission is checked.
		 */
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxRole");
		typeACL.setAclParentTypes(Arrays.asList("IncludedBy"));

		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);

		FxUser user31 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 31l));
		FxUser user21 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 21l));
		FxRole grp1 = new FxRole((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxRole"), 101l));

		/**
		 * 
		 */
		InstanceACL instanceACL = new InstanceACL(grp1.getEntityImpl());

		// user 31 can access grp1
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(31l);
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		// user 21 can not access grp1
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(21l);
		permission1.setActions(Arrays.asList(ACLHelper.getActionByName(EditAction.NAME), ACLHelper.getActionByName(WatcherAction.NAME)));

		InstanceACE ace1 = new InstanceACE("FxRole", grp1.getId());
		ace1.setRolePermission(permission);

		InstanceACE ace2 = new InstanceACE("FxRole", grp1.getId());
		ace2.setRolePermission(permission1);

		instanceACL.setAces(Arrays.asList(ace1, ace2));
		aclCache.addCache(instanceACL);

		FleximsDynamicEntityImpl user11 = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l);
		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user31), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user31), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user31), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user31), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user21), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user21), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user21), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Undecided);

		// there is no rule for User11 to access the instance 'User11'
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(new FxUser(user11)), "FxUser", user11, false);
		assertEquals(result, PermissionResult.Undecided);

	}

	/**
	 * Rule 6: Static permission defined in current type
	 */
	@Test
	public void testTypeRule_typeitself() {
		constructChildTypeACL();

		FleximsDynamicEntityImpl de1 = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 1l);
		FxUser user1 = new FxUser(de1);

		FleximsDynamicEntityImpl de2 = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 2l);
		FxUser user2 = new FxUser(de2);

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user1), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user1), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user1), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user1), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

	}

	/**
	 * rule 7: Check rule defined in super type: Should check FxRole for FxUser.
	 */
	@Test
	public void testTypeRule_supertype() {
		constructChildTypeACL();
		FxUser user11 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user12 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 12l));

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		// Add permission to role type
		addSuperTypeRule();
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user12), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user12), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user12), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user12), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);
	}

	/**
	 * check does not care whether it is user or role
	 */
	@Test
	public void testRole() {
		addUserTypeACLByRole();
		FxUser user11 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user21 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 21l));

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Allow);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		// ---------------------deby case
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user21), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(user21), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(user21), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(user21), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);

		FxRole grp2 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxRole"), 102l));
		// deny case by group
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(grp2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(WatcherAction.NAME), Arrays.asList(grp2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// implied by Edit
		result = checker.checkPermission(ACLHelper.getActionByName(ReadAction.NAME), Arrays.asList(grp2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);

		// can watcher
		result = checker.checkPermission(ACLHelper.getActionByName(CreateAction.NAME), Arrays.asList(grp2), "FxUser", null, false);
		assertEquals(result, PermissionResult.Undecided);
	}

	/**
	 * check rule defined in containg type
	 * 
	 */
	@Test
	public void testTypeRule_customHook() {
		setField(checker, "actionEvent", new Event<ActionContext>() {

			@Override
			public void fire(ActionContext event) {
				// deny access
				event.setPermissionResult(PermissionResult.Deny);
			}

			@Override
			public Event<ActionContext> select(Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
				return null;
			}

		});
		addUserTypeACLByRole();
		FxUser user11 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 11l));
		FxUser user21 = new FxUser((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, "FxUser"), 21l));

		PermissionResult result = null;
		// -----------------------allow case
		// user 1 can edit usr.
		result = checker.checkPermission(ACLHelper.getActionByName(EditAction.NAME), Arrays.asList(user11), "FxUser", null, false);
		assertEquals(result, PermissionResult.Deny);
		// set this back to original
		setField(checker, "actionEvent", new Event<ActionContext>() {

			@Override
			public void fire(ActionContext event) {
			}

			@Override
			public Event<ActionContext> select(Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
				return null;
			}

			@Override
			public <U extends ActionContext> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
				return null;
			}

		});

	}
	
	@Test
	public void testQueryAuthorizer() {
		String typeid = "Basictype";

		// simulate that current user is use01.
		RoleContext roleContext = new RoleContext();
		FleximsDynamicEntityImpl user01de = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, FxUser.TYPE_NAME), 1l);
		FxUser user01 = new FxUser(user01de);
		roleContext.getRoles().push(user01);

		TypedQuery query = QueryHelper.createSimpleEqualQuery(typeid, "propint", "50");
		QueryUtil.saveQuery((ConditionQuery) query, em);

		// Give user 01 read action.
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid(typeid);
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(user01de.getId());
		permission.setActions(Arrays.asList(ACLHelper.getActionByName(ReadAction.NAME)));
		typeACL.setRolePermissions(Arrays.asList(permission));

		ACLHelper.typeacls.put(typeid, typeACL);

		// Can we judge permission by query.
		ReportActionContext reportActionContext = new ReportActionContext();
		reportActionContext.setFxReport(query.getEntity());

		// case one:
		// check whether the end user has READ permission in target type.
		QueryAuthorizer authorizer = new QueryAuthorizer();
		setField(authorizer, checker);
		setField(authorizer, roleContext);
		// postive case,
		try {
			authorizer.assertQueryExecution(reportActionContext);
		} catch (AuthorizedException e) {
			fail("Read permission in targeted type should allow query");
		}

		// negative case
		typeACL.getRolePermissions().get(0).setDecision(Decision.Deny);
		try {
			authorizer.assertQueryExecution(reportActionContext);
			fail("Read permission in targeted type should allow query");
		} catch (AuthorizedException e) {

		}

		// case 2: if query permission is defined in Query instance, it should
		// be used.
		// Query permission is given to query instance. Although it is denied in
		// Basictype type.
		InstanceACE ace = new InstanceACE(ConditionQuery.TYPE_NAME, query.getEntity().getId());
		// role permission
		RolePermission instPermission = new RolePermission();
		instPermission.setDecision(Decision.Allow);
		instPermission.setRoleid(user01.getId());
		instPermission.setActions(Arrays.asList(ACLHelper.getActionByName(QueryAction.NAME)));
		ace.setRolePermission(instPermission);
		InstanceACL acl = new InstanceACL(query.getEntity());
		acl.setAces(Arrays.asList(ace));
		aclCache.addCache(acl);
		try {
			authorizer.assertQueryExecution(reportActionContext);
		} catch (AuthorizedException e) {
			fail("Query permission in Query Instance should allow query");
		}

		// create a new temporary report
		FleximsDynamicEntityImpl report = JpaHelper.createNewEntity(em, FxReportWrapper.TYPE_NAME);
		report.set(FxReportWrapper.PROP_NAME_PROP_QUERY, query.getEntity());

		// Temporary report should follow the same flow as query associated
		try {
			reportActionContext.setFxReport(report);
			authorizer.assertQueryExecution(reportActionContext);
		} catch (AuthorizedException e) {
			fail("Query permission in Query Instance should allow query for temporary report");
		}

		// negative case for query permission in instance
		acl.getAces().get(0).getInstanceRolePermission().setDecision(Decision.Deny);
		try {
			reportActionContext.setFxReport(query.getEntity());
			authorizer.assertQueryExecution(reportActionContext);
			fail("Query permission in Query Instance should allow query");
		} catch (AuthorizedException e) {

		}
		try {
			reportActionContext.setFxReport(report);
			authorizer.assertQueryExecution(reportActionContext);
			fail("Query permission in Query Instance should temporary report");
		} catch (AuthorizedException e) {

		}

		// persistence the report.
		FxReportWrapper reportWrapper = new FxReportWrapper();
		reportWrapper.setEntity(report);
		reportWrapper.setName("queryauthorizertest");
		reportWrapper.setProperties("propint");
		em.getTransaction().begin();
		em.persist(report);
		em.getTransaction().commit();

		// No a permission at instance level. QueryInstance should be checked.
		try {
			reportActionContext.setFxReport(report);
			authorizer.assertQueryExecution(reportActionContext);
			fail("Query permission in Query Instance should allow persisteed report");
		} catch (AuthorizedException e) {

		}

		// let us add permission for report instance
		ace = new InstanceACE(FxReportWrapper.TYPE_NAME, report.getId());
		// role permission
		instPermission = new RolePermission();
		instPermission.setDecision(Decision.Allow);
		instPermission.setRoleid(user01.getId());
		instPermission.setActions(Arrays.asList(ACLHelper.getActionByName(QueryAction.NAME)));
		ace.setRolePermission(instPermission);
		acl = new InstanceACL(report);
		acl.setAces(Arrays.asList(ace));
		aclCache.addCache(acl);
		// No a permission at instance level. QueryInstance should be checked.
		try {
			reportActionContext.setFxReport(report);
			authorizer.assertQueryExecution(reportActionContext);

		} catch (AuthorizedException e) {
			fail("Query permission in report Instance should allow persisteed report");
		}
	}
}
