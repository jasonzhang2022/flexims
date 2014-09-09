package com.flexdms.flexims.unit;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.AcceptScopesStrategy;

/**
 * A set utilities to create/inspect shrinkwrap[ archive. All methods are
 * static. Caller can use import static to import all methods
 * 
 * @author jason.zhang
 * 
 */
public final class ArchiveUtil {
	private ArchiveUtil() {
	}

	public static WebArchive addMavenDependencies(WebArchive web) {
		File[] files = Maven.resolver().loadPomFromFile("pom.xml").importRuntimeDependencies().resolve().withTransitivity().asFile();
		web.addAsLibraries(files);
		return web;
	}

	public static WebArchive buildFullArchive(String warfile) {
		if (new File(warfile).exists()) {
			WebArchive web = ShrinkWrap.create(ZipImporter.class, warfile).importFrom(new File(warfile)).as(WebArchive.class);
			return web;
		} else {
			return prepareFullArchive(warfile);
		}
	}

	public static WebArchive prepareFullArchive(String warfile) {
		WebArchive web = ShrinkWrap.create(MavenImporter.class, warfile).loadPomFromFile("pom.xml")
				.importBuildOutput(new AcceptScopesStrategy(ScopeType.COMPILE)).as(WebArchive.class);

		return web;
	}

	public static WebArchive prepareFullArchiveFromPom(String pomfile) {
		WebArchive web = ShrinkWrap.create(MavenImporter.class).loadPomFromFile(new File(pomfile))
				.importBuildOutput(new AcceptScopesStrategy(ScopeType.COMPILE)).as(WebArchive.class);

		return web;
	}

	public static void saveWarFileAsZip(Archive<?> archive, String file) {
		archive.as(ZipExporter.class).exportTo(new File(file));
	}

	public static void saveWarFileAsExploded(Archive<?> archive, String file) {
		archive.as(ExplodedExporter.class).exportExploded(new File(file));
	}

	public static WebArchive importWarFile(String file) {
		return ShrinkWrap.create(WebArchive.class).as(ExplodedImporter.class).importDirectory(new File(file)).as(WebArchive.class);
	}

	public static void listWarFile(Archive<?> archive) {
		System.out.println(archive.toString(true));
	}

	public static PomEquippedResolveStage getPomLibraryResolver() {
		PomEquippedResolveStage stage = Maven.resolver().loadPomFromFile("pom.xml");
		return stage;
	}

	public static File[] resolvePomLibrary(PomEquippedResolveStage stage, String group, String artifact) {
		return stage.resolve(group + ":" + artifact).withTransitivity().asFile();
	}
}
