package com.flexdms.flexims.unit.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.NewCookie;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.junit.rules.ExternalResource;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.rsutil.ExceptionWriter;
import com.flexdms.flexims.rsutil.JaxbJsonProvider;
import com.flexdms.flexims.rsutil.RsHelper;
import com.flexdms.flexims.rsutil.XMLEntityMappingsReaderWriter;
import com.flexdms.flexims.unit.TestOXMSetup;

/**
 * Set up Jeryclient for send/receive request/reponse, marshall/unmarshall
 * 
 * @author jason.zhang
 * 
 */
public class JerseyClientRule extends ExternalResource {
	public String baseUrl = null;
	static JaxbHelper jaxHelper;

	public Client client;
	public WebTarget target;

	public JerseyClientRule(String baseUrl) {
		super();
		this.baseUrl = baseUrl;
		if (jaxHelper == null) {
			jaxHelper = new JaxbHelper();
			jaxHelper.init(TestOXMSetup.factory, TestOXMSetup.dcl);
		}
	}

	Map<String, NewCookie> cookies = new HashMap<String, NewCookie>();

	@Override
	protected void before() throws Throwable {
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true)).register(XMLEntityMappingsReaderWriter.class)
				.register(ExceptionWriter.class)

				.register(new ClientResponseFilter() {

					@Override
					public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
						if (responseContext.getCookies() != null && !responseContext.getCookies().isEmpty()) {
							cookies.putAll(responseContext.getCookies());
						}

					}
				}).register(new RsHelper.EntitiesReaderWriter() {
					@Override
					public JaxbHelper getJaxbHelper() {

						return jaxHelper;
					}

				}).register(new JaxbJsonProvider<Object>() {
					@Override
					public JaxbHelper getJaxbHelper() {

						return jaxHelper;
					}
				}, 1000).register(MultiPartFeature.class)
				// when RsMsg is constructed in ExpetionWriter. Default
				// MpxyJsonProvider is used.
				// The configuration below is used by default Json provider.
				.register(new MoxyJsonConfig().setIncludeRoot(true).setFormattedOutput(true).resolver());
		client = ClientBuilder.newClient(clientConfig);
		target = client.target(baseUrl);

		System.out.println(target.getUri().toString());
	};

	@Override
	protected void after() {
		client.close();
	};
}