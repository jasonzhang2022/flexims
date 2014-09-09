package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.net.URL;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

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

import com.flexdms.flexims.App;
import com.flexdms.flexims.auth.LoginInfo;
import com.flexdms.flexims.auth.PasswordHasher;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.FxUserSettings;
import com.flexdms.flexims.util.ThreadContext;

@RunWith(Arquillian.class)
@RunAsClient
public class TestAuthService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		WebArchive webArchive = ArchiveUtil.buildRsWebArchive(null, Arrays.asList("com.flexdms.flexims:auth"));
		return webArchive;
	}

	@ArquillianResource
	protected URL baseURL;

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	public TestAuthService() {
		super();
		servicePrefixString = "authrs/auth";
	}

	static FxUser user;

	@BeforeClass
	public static void createTestUser() {
		EntityManager em = App.getEM();
		user = FxUser.createUser(em);
		user.setName("unittest");
		user.setEmail("unittest@test.com");
		user.setPassword("123456");

		em.getTransaction().begin();
		em.persist(user.getEntityImpl());
		em.persist(user.getUserSettings().getEntityImpl());
		em.flush();
		PasswordHasher.hashPasswordStatic(user);
		em.getTransaction().commit();
		em.close();
		ThreadContext.remove(App.EM_KEY);
	}

	@Test
	@InSequence(0)
	public void testStatus() {
		String ret = createBuilder(target.path("status"), MediaType.TEXT_PLAIN).get(String.class);
		assertThat(ret, equalTo("ok"));
	}

	public void login() {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setEmail(user.getEmail());
		loginInfo.setPassword("123456");

		AppMsg msg = postJson(target.path("auth"), loginInfo);
		assertThat(msg.getStatuscode(), equalTo(0l));

	}

	@Test
	@InSequence(1)
	public void testCurrentUser() {

		// authentication is needed.
		try {
			Builder builder = createBuilder(target.path("currentUser"));
			builder.get();
			fail("Should not pass without authentication");
		} catch (Exception e) {

		}

		login();

		FleximsDynamicEntityImpl duser = getJson(target.path("currentUser"));
		FxUser user1 = new FxUser(duser);
		assertThat(user1.getEmail(), equalTo(user.getEmail()));

	}

	@Test
	@InSequence(2)
	public void testUserSetting() {
		login();
		FleximsDynamicEntityImpl ds = getJson(target.path("accountSettings"));
		assertThat(ds.getClass().getSimpleName(), equalTo(FxUserSettings.TYPE_NAME));

	}

	@Test
	@InSequence(3)
	public void testRegister() {
		LoginInfo info = new LoginInfo();
		info.setUsername("regtest");
		info.setPassword("123456");
		info.setEmail("regtest@t.com");
		AppMsg msg = postJson(target.path("register"), info);
		assertThat(msg.getStatuscode(), equalTo(0l));

		// we can login with regsitered user.
		msg = postJson(target.path("auth"), info);
		assertThat(msg.getStatuscode(), equalTo(0l));

	}

	@Test
	@InSequence(5)
	public void testLogout() {
		login();
		AppMsg msg = getJson(target.path("logout"));
		assertThat(msg.getStatuscode(), equalTo(0l));
		try {
			Builder builder = createBuilder(target.path("currentUser"));
			builder.get();
			fail("Should not fail after logout");
		} catch (Exception e) {

		}
	}

	@Test
	@InSequence(6)
	public void changePassword() {
		login();

		LoginInfo info = new LoginInfo();
		info.setPassword("1234567");
		info.setOldPassword("123456");

		AppMsg msg = postJson(target.path("changepassword"), info);
		assertThat(msg.getStatuscode(), equalTo(0l));

		msg = getJson(target.path("logout"));

		info = new LoginInfo();
		info.setEmail(user.getEmail());
		info.setPassword("1234567");

		// login with chnaged password.
		msg = postJson(target.path("auth"), info);
		assertThat(msg.getStatuscode(), equalTo(0l));

	}

}
