package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import java.io.StringReader;
import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Decision;
import com.flexdms.flexims.accesscontrol.action.AllAction;
import com.flexdms.flexims.accesscontrol.action.CreateAction;
import com.flexdms.flexims.accesscontrol.action.DeleteAction;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.GrantAction;
import com.flexdms.flexims.accesscontrol.action.QueryAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.accesscontrol.action.WatcherAction;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.accesscontrol.rs.InstanceACES;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.unit.security.JaxbTest;
import com.flexdms.flexims.users.FxUser;

@RunWith(Arquillian.class)
@RunAsClient
public class TestAclService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		WebArchive webArchive = ArchiveUtil.buildRsWebArchive(null, Arrays.asList("com.flexdms.flexims:security"));
		return webArchive;
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	public TestAclService() {
		super();
		servicePrefixString = "aclrs/acl";
	}

	static FxUser user;

	static JAXBContext ctx;

	@BeforeClass
	public static void createTestUser() throws JAXBException {
		ctx = JAXBContext.newInstance(NameValueList.class, TypeACL.class, InstanceACES.class);
		ACLHelper.actions = Arrays.asList(new EditAction(), new WatcherAction(), new ReadAction(), new QueryAction(), new AllAction(),
				new CreateAction(), new DeleteAction(), new GrantAction());

	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T unmarshaller(String ret) {
		if (ret.length() < 10) {
			return null;
		}
		javax.xml.bind.Unmarshaller unmarshaller;

		try {
			unmarshaller = ctx.createUnmarshaller();
			unmarshaller.setProperty("eclipselink.media-type", "application/json");
			return (T) unmarshaller.unmarshal(new StringReader(ret));
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

	@Test
	@InSequence(0)
	public void testStatus() {
		String ret = createBuilder(target.path("status"), MediaType.TEXT_PLAIN).get(String.class);
		assertThat(ret, equalTo("ok"));
	}

	@Test
	@InSequence(0)
	public void testAllAction() {
		NameValueList allActions = getJson(target.path("allactions"));
		assertThat(allActions.getEntry(), hasSize(ACLHelper.getAvailableActions().size()));
	}

	/* This test could be dectructive. */
	@Test
	@InSequence(100)
	public void testTypeACL() {

		// read
		TypeACL typeACL = getJson(target.path("typeacl").path("FxRole"));
		assertThat(typeACL.getAclParentTypes(), hasSize(1));
		assertThat(typeACL.getAclParentTypes(), hasItem("IncludedBy"));
		assertThat(typeACL.getRolePermissions(), hasSize(2));

		typeACL = getJson(target.path("typeacl").path("FxUserSettings"));
		assertThat(typeACL.getPropPermissions(), hasSize(1));

		// save
		TypeACL oldFxuserAcl= getJson(target.path("typeacl").path("FxUser"));
		typeACL = JaxbTest.createTypeACL();
		postJson(target.path("typeacl"), typeACL);
		typeACL = getJson(target.path("typeacl").path("FxUser"));
		//restore
		postJson(target.path("typeacl"), oldFxuserAcl);
		assertThat(typeACL.getAclParentTypes(), hasSize(2));
		assertThat(typeACL.getRolePermissions(), hasSize(2));
		assertThat(typeACL.getPropPermissions(), hasSize(1));
	}

	@Test
	@InSequence(10)
	public void testInstanceACL() {
		String type="Test";
		FleximsDynamicEntityImpl entityImpl=JpaHelper.createNewEntity(em, type);
		entityImpl.set("fname", "acltest");
		entityImpl.setId(52l);
		
		em.getTransaction().begin();
		FleximsDynamicEntityImpl dEntityImpl=(FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), 52l);
		if (dEntityImpl!=null) {
			em.remove(dEntityImpl);
		}
		em.getTransaction().commit();
		
		em.getTransaction().begin();
		em.persist(entityImpl);
		em.getTransaction().commit();
		
		InstanceACES aces = JaxbTest.createInstACL();
		postJson(target.path("instacl").path("Test").path("52"), aces);

		// get
		aces = getJson(target.path("instacl").path("Test").path("52"));
		assertThat(aces.getAces(), hasSize(2));

		// update
		aces.getAces().get(0).getInstanceRolePermission().setDecision(Decision.Deny);
		aces.getAces().get(1).getInstanceRolePermission().setDecision(Decision.Deny);
		postJson(target.path("instacl").path("Test").path("52"), aces);

		aces = getJson(target.path("instacl").path("Test").path("52"));
		assertThat(aces.getAces(), hasSize(2));
		assertThat(aces.getAces().get(0).getInstanceRolePermission().getDecision(), equalTo(Decision.Deny));
		assertThat(aces.getAces().get(1).getInstanceRolePermission().getDecision(), equalTo(Decision.Deny));

		// delete one
		aces.getAces().remove(1);
		postJson(target.path("instacl").path("Test").path("52"), aces);
		assertThat(aces.getAces(), hasSize(1));

		// delete all
		aces.getAces().clear();
		postJson(target.path("instacl").path("Test").path("52"), aces);
		assertThat(aces.getAces(), hasSize(0));
	}

}
