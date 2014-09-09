package com.flexdms.flexims.it;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenResolvedArtifact;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

public class ArchiveUtil {


	public static WebArchive buildRsWebArchive() {
		return buildRsWebArchive(null);
	}


	public static WebArchive buildRsWebArchive(String name) {
		WebArchive web = null;
		if (name != null) {
			web = ShrinkWrap.create(WebArchive.class, name);
		} else {
			web = ShrinkWrap.create(WebArchive.class);
		}
	
		

		// library
		PomEquippedResolveStage stage = Maven.resolver().loadPomFromFile("pom.xml");
		MavenResolvedArtifact artifact=stage.resolve("com.flexdms.flexims:common").withoutTransitivity().asSingle(MavenResolvedArtifact.class);
		String version=artifact.getResolvedVersion();
		
		WebArchive js= stage.resolve("com.flexdms.flexims:js:war:"+version).withoutTransitivity().asSingle(WebArchive.class);
		web.merge(js);
		
		// internal depedency
		JavaArchive tomcat=stage.resolve("com.flexdms.flexims:tomcat_web:jar:classes:"+version).withoutTransitivity().asSingle(JavaArchive.class);
		tomcat.addAsManifestResource(new File("../tomcat_web/src/main/webapp/WEB-INF/beans.xml"));
		web.addAsLibraries(tomcat);
		
		//users, common, urlctrl
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:auth").withTransitivity().asFile());
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:testorm").withoutTransitivity().asFile());
		
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:dbfiles").withoutTransitivity().asFile());
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:googlefiles").withoutTransitivity().asFile());
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:s3files").withoutTransitivity().asFile());
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:report").withoutTransitivity().asFile());
		web.addAsLibraries(stage.resolve("com.flexdms.flexims:security").withoutTransitivity().asFile());
		
		
		web.addAsLibraries(stage.resolve("org.apache.derby:derbyclient").withoutTransitivity().asFile());
		// basic
		web.addAsLibraries(stage.resolve("javax.validation:validation-api").withTransitivity().asFile());

		// weld, deltaspkie
		web.addAsLibraries(stage.resolve("org.jboss.weld.servlet:weld-servlet").withTransitivity().asFile());

		// JERSEY and eclipselink
		web.addAsLibraries(stage.resolve("org.glassfish.jersey.containers.glassfish:jersey-gf-cdi").withTransitivity().asFile());
		web.addAsLibraries(stage.resolve("org.glassfish:javax.json").withTransitivity().asFile());
		

		// context.xml
		web.addAsWebResource(new File("../servicetest/src/test/resources/rs/context.xml"), "META-INF/context.xml");
		web.addAsWebInfResource(new File("../tomcat_web/src/main/webapp/WEB-INF/web.xml"), "web.xml");
				
		return web;
	}

	/**
	 * Build full archive is length process, we only build it once
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static final String warfile = "target/temp.war";

	public static void main(String args[]) throws Exception {

		// WebArchive webArchive = prepareFullArchive();
		WebArchive webArchive = buildRsWebArchive("flexims");
		System.out.println(webArchive.toString(true));

		String dirString = "target/apache-tomcat-7.0.52/apache-tomcat-7.0.52/webapps";
		FileUtils.deleteQuietly(new File(dirString + "/flexims"));
		new File(dirString).mkdir();

		com.flexdms.flexims.unit.ArchiveUtil.saveWarFileAsExploded(webArchive, dirString);

	}
}
