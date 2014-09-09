package com.flexdms.flexims.users.unit;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jaxb.moxy.RelationAsIDMoxyMetaSource;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.TestOXMSetup;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.FxUserSettings;
import com.flexdms.flexims.users.RoleUtils;

public class TestRoleUser {
	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule()).around(new JPA_JAXB_EmbeddedDerby_Rule())
			.around(new AppInitializerRule());

	@Rule
	public EntityManagerRule emRule = new EntityManagerRule();
	JaxbHelper helper;
	EntityManager em;

	@Before
	public void setup() throws JAXBException {
		em = emRule.em;
		helper = App.getInstance(JaxbHelper.class);
		//dumpMappingXml();
	}

	public static void dumpMappingXml() throws JAXBException {

		RelationAsIDMoxyMetaSource src = new RelationAsIDMoxyMetaSource(TestOXMSetup.factory);
		XmlBindings bindings = src.getXmlBindings(null, TestOXMSetup.dcl);
		RelationAsIDMoxyMetaSource.toOXM(bindings, new OutputStreamWriter(System.out));
	}
	/**
	 * Test that we can
	 * <ul>
	 * <li>Persist</li>
	 * <li>marshall and unmarhsall in xml and json</li>
	 * <li>subrole and uncludedby are correct</li>
	 * </ul>
	 */
	@Test
	public void testRolePersistIncludeJaxb() {
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, FxRole.TYPE_NAME);
		FxRole role = new FxRole(de);
		role.setName("p");

		FleximsDynamicEntityImpl dc = JpaHelper.createNewEntity(em, FxRole.TYPE_NAME);
		FxRole rolec = new FxRole(dc);
		rolec.setName("c1");
		role.addSubRoles(dc);

		FleximsDynamicEntityImpl dc2 = JpaHelper.createNewEntity(em, FxRole.TYPE_NAME);
		FxRole rolec2 = new FxRole(dc2);
		rolec2.setName("c2");
		rolec.addSubRoles(dc2);

		em.getTransaction().begin();
		em.persist(dc);
		em.persist(dc2);
		em.persist(de);
		FleximsDynamicEntityImpl df = JpaHelper.createNewEntity(em, FxRole.TYPE_NAME);
		df.set(FxRole.PROP_NAME_PROP_NAME, "cf");
		em.persist(df);
		em.getTransaction().commit();

		helper.output(de, new OutputStreamWriter(System.out), true, false);
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		de = em.find(de.getClass(), de.getId());
		dc = em.find(dc.getClass(), dc.getId());
		dc2 = em.find(dc2.getClass(), dc2.getId());

		role = new FxRole(de);
		rolec = new FxRole(dc);
		rolec2 = new FxRole(dc2);
		FxRole rolef = new FxRole(em.find(dc2.getClass(), df.getId()));

		assertThat(role.getSubRoles(), hasSize(1));
		assertThat(role.getWrappedSubRoles(), hasSize(1));
		assertThat(role.getAllWrappedSubRoles(), hasSize(3));

		assertThat(rolec2.getWrappedIncludeBys(), hasSize(1));

		assertTrue(rolec2.isIncludedBy(rolec));
		assertTrue(rolec2.isIncludedBy(role));
		assertFalse(rolec2.isIncludedBy(rolef));

		FxRole[] roles = { role, rolec, rolec2 };

		for (FxRole rolet : roles) {
			StringWriter stringWriter = new StringWriter();
			helper.toXml(rolet.getEntityImpl(), stringWriter);
			String string = stringWriter.toString();
			System.out.println(string);
			FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(string), em);
			assertThat(dynamicEntityImpl.get(FxRole.PROP_NAME_PROP_NAME).toString(), equalTo(rolet.getName()));
		}
		for (FxRole rolet : roles) {
			StringWriter stringWriter = new StringWriter();
			helper.toJson(rolet.getEntityImpl(), stringWriter);
			String string = stringWriter.toString();
			System.out.println(string);
			FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(string), em);
			assertThat(dynamicEntityImpl.get(FxRole.PROP_NAME_PROP_NAME).toString(), equalTo(rolet.getName()));
		}
		
		StringWriter stringWriter = new StringWriter();
		helper.toXml(role.getEntityImpl(), stringWriter);
		String string = stringWriter.toString();
		System.out.println(string);
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(string), em);
		FxRole unmarshalled=new FxRole(dynamicEntityImpl);
		
		assertThat(unmarshalled.getSubRoles(), hasSize(1));

	}

	/**
	 * Test that we can
	 * <ul>
	 * <li>Persist</li>
	 * <li>marshall and unmarhsall in xml and json</li>
	 * </ul>
	 */
	@Test
	public void testUserAndSetting() {
		FxUser user = FxUser.createUser(em);
		user.setEmail("test@my.com");
		user.setName("test");
		user.setPassword("123456");

		em.getTransaction().begin();
		em.persist(user.getEntityImpl());
		em.persist(user.getUserSettings().getEntityImpl());
		em.getTransaction().commit();

		FxUser user2 = FxUser.loadByEmail(em, user.getEmail());
		assertThat(user.getId(), is(user2.getId()));

		StringWriter stringWriter = new StringWriter();
		helper.toXml(user.getEntityImpl(), stringWriter);
		String string = stringWriter.toString();
		System.out.println(string);
		FleximsDynamicEntityImpl dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(string), em);
		assertThat(dynamicEntityImpl.get(FxRole.PROP_NAME_PROP_NAME).toString(), equalTo(user.getName()));

		stringWriter = new StringWriter();
		helper.toJson(user.getEntityImpl(), stringWriter);
		string = stringWriter.toString();
		System.out.println(string);
		dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(string), em);
		assertThat(dynamicEntityImpl.get(FxRole.PROP_NAME_PROP_NAME).toString(), equalTo(user.getName()));

		stringWriter = new StringWriter();
		helper.toXml(user.getUserSettings().getEntityImpl(), stringWriter);
		string = stringWriter.toString();
		System.out.println(string);
		dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromXml(new StringReader(string), em);
		assertThat(dynamicEntityImpl.get(FxUserSettings.PROP_NAME_PROP_EMAIL).toString(), equalTo(user.getEmail()));

		stringWriter = new StringWriter();
		helper.toJson(user.getUserSettings().getEntityImpl(), stringWriter);
		string = stringWriter.toString();
		System.out.println(string);
		dynamicEntityImpl = (FleximsDynamicEntityImpl) helper.fromJson(new StringReader(string), em);
		assertThat(dynamicEntityImpl.get(FxUserSettings.PROP_NAME_PROP_EMAIL).toString(), equalTo(user.getEmail()));

	}

	@Test
	public void testFxRoleUnmarshall() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><FxRole> <id>901</id><fxversion>2014-05-29T10:46:08.36227-07:00</fxversion> <Name>Test1</Name><SubRole>61</SubRole></FxRole>";
		Object object = helper.fromXml(new StringReader(xml), em);
		FxRole role = new FxRole((FleximsDynamicEntityImpl) object);
		assertTrue(role.getSubRoles().size() == 1);

	}

	@Test
	public void testUserAndRoleAssociation() {
		FxUser user = FxUser.loadByEmail(em, "testadmin@email.com");
		assertTrue(user.isIncludedBy(RoleUtils.getAdminRole()));
	}
}
