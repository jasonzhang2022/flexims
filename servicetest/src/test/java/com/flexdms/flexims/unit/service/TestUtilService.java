package com.flexdms.flexims.unit.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.util.List;

import javax.ws.rs.core.MediaType;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.util.TimeUnit;

/**
 * TODO: test case for create type with inheritance, Test case for check
 * property name
 * 
 * @author jason.zhang
 * 
 */
@RunWith(Arquillian.class)
@RunAsClient
public class TestUtilService extends TestRSbase {
	@Deployment(testable = false)
	@OverProtocol("Servlet 3.0")
	public static Archive<?> createDeployment() throws Exception {
		return ArchiveUtil.buildRsWebArchive();
	}

	@ArquillianResource
	protected URL baseURL;

	public TestUtilService() {
		super();
		servicePrefixString = "rs/util";
	}

	@Override
	protected String getBaseUrl() {
		baseUrl = baseURL.toExternalForm();
		return baseUrl;
	}

	static String testName = "Basictype";
	static long testid = 0;

	@Test
	public void testGetenum() throws Exception {
		// test
		NameValueList nvs = target.path("getenum").path(TimeUnit.class.getName()).request(MediaType.APPLICATION_JSON).get(NameValueList.class);
		assertThat(nvs.getEntry().size(), equalTo(TimeUnit.values().length));
	}

	@Test
	public void temp() {
		List<URL> resources = CPScanner.scanResources(new ResourceFilter().packageName("META-INF.resources.thirdparty").resourceName("*.js"));
		// .resourceName("META-INF").resourceName("resources").resourceName("thirdparty").resourceName("*.js"));
		// .resourceName("META-INF/resources/thirdparty/*.js"));
		// .resourceName("resources").resourceName("thirdparty").resourceName("*.js"));
		for (URL res : resources) {
			System.out.println(res.toExternalForm());
		}
	}

}
