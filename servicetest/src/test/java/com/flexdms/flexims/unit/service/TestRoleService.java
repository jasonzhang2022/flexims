package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.App;
import com.flexdms.flexims.auth.PasswordHasher;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.util.ThreadContext;

@RunWith(Arquillian.class)
@RunAsClient
public class TestRoleService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		WebArchive webArchive = ArchiveUtil.buildRsWebArchive(null, Arrays.asList("com.flexdms.flexims:users"));
		return webArchive;
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	public TestRoleService() {
		super();
		servicePrefixString = "rolers/role";
	}

	@Test
	@InSequence(0)
	public void testStatus() {
		String ret = createBuilder(target.path("status"), MediaType.TEXT_PLAIN).get(String.class);
		assertThat(ret, equalTo("ok"));
	}

	@Test
	public void testAllRoles() {
		Entities entities = getJson(target.path("allroles"));
		assertTrue(entities.getItems().size() > 3);
	}

	public void createTestUser() {
		FxUser user = FxUser.createUser(em);
		user.setName("unittest");
		user.setEmail("unittest@test.com");
		user.setPassword("123456");

		em.getTransaction().begin();
		em.persist(user.getEntityImpl());
		em.persist(user.getUserSettings().getEntityImpl());
		em.flush();
		PasswordHasher.hashPasswordStatic(user);
		em.getTransaction().commit();
	}

	@Test
	public void testAllUsers() {
	
		createTestUser();
		Entities entities = getJson(target.path("allusers"));
		assertTrue(entities.getItems().size() >1);

	}
}
