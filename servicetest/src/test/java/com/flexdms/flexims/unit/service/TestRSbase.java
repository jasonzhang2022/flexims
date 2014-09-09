package com.flexdms.flexims.unit.service;

import java.io.StringReader;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_NetworkedDerbyForClientAndServer_Rule;

public class TestRSbase {
	protected String baseUrl = "http://localhost:8080/flexims/";
	protected String servicePrefixString;
	protected JaxbHelper helper;

	protected Client client;
	protected WebTarget target;
	protected EntityManager em;

	// set up derby database, start database, set up EntityManagerFactory
	@ClassRule
	public static JPA_JAXB_NetworkedDerbyForClientAndServer_Rule clientSetupRule = new JPA_JAXB_NetworkedDerbyForClientAndServer_Rule();
	@Rule
	public EntityManagerRule entityManagerRule = new EntityManagerRule();

	public JerseyClientRule jerseyClientRule;

	@Before
	public void setup() throws Throwable {

		em = entityManagerRule.em;
		jerseyClientRule = new JerseyClientRule(getBaseUrl());
		jerseyClientRule.before();
		client = jerseyClientRule.client;
		target = jerseyClientRule.target.path(servicePrefixString);
		helper = JerseyClientRule.jaxHelper;
	}

	@After
	public void teardown() {
		jerseyClientRule.after();
		 try {
		        Class rtClass = Thread.currentThread().getContextClassLoader().getParent().loadClass("org.jacoco.agent.rt.RT");
		        Object jacocoAgent = rtClass.getMethod("getAgent", null).invoke(null);
		        Method dumpMethod = jacocoAgent.getClass().getMethod("dump", boolean.class);
		        dumpMethod.invoke(jacocoAgent, false);
		    } catch(ClassNotFoundException e) {
		        System.out.println("no jacoco agent attached to this jvm");
		    } catch (Exception e) {
		    	 System.out.println("while trying to dump jacoco data");
		    }
	}

	protected String getBaseUrl() {
		return baseUrl;
	}

	protected javax.ws.rs.client.Invocation.Builder createBuilder(WebTarget t) {
		return createBuilder(t, MediaType.APPLICATION_JSON);
	}

	protected javax.ws.rs.client.Invocation.Builder createBuilder(WebTarget t, String mediaType) {
		javax.ws.rs.client.Invocation.Builder builder = t.request(mediaType);
		if (!jerseyClientRule.cookies.isEmpty()) {
			for (NewCookie c : jerseyClientRule.cookies.values()) {
				builder = builder.cookie(c);
			}
		}
		return builder;
	}

	@SuppressWarnings("unchecked")
	protected <T> T unmarshaller(String ret) {
		if (ret.length() < 10) {
			return null;
		}
		return (T) JerseyClientRule.jaxHelper.fromJson(new StringReader(ret), em);
	}

	public <T> T postJson(WebTarget t, Object entity) {

		String ret = createBuilder(t).post(Entity.entity(entity, MediaType.APPLICATION_JSON_TYPE), String.class);
		return unmarshaller(ret);

	}

	public <T> T getJson(WebTarget t) {
		String ret = createBuilder(t).get(String.class);
		return unmarshaller(ret);
	}

	public <T> T deleteJson(WebTarget t) {
		String ret = createBuilder(t).delete(String.class);
		return unmarshaller(ret);
	}
}
