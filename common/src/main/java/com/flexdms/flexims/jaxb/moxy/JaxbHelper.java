package com.flexdms.flexims.jaxb.moxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import net.sf.corn.cps.CPScanner;
import net.sf.corn.cps.ResourceFilter;

import org.eclipse.persistence.dynamic.DynamicClassLoader;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBHelper;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.ObjectGraph;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.jaxb.dynamic.DynamicJAXBContextFactory;
import org.eclipse.persistence.jaxb.metadata.MetadataSource;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.TypeUpdated;

/**
 * Customized Jaxb used by the system. Besides dynamic entity, static class can
 * be added by giving META-INF/*oxm.xml in jar file.
 * 
 * @author jason TODO update JAXBCONTEXT when type is updated
 */

@ApplicationScoped
public class JaxbHelper {
	public static final Logger LOGGER = Logger.getLogger(JaxbHelper.class.getCanonicalName());

	/**
	 * A JAXBContext that marshals/unmarshals related object to/from ID.
	 */
	protected JAXBContext jc;
	/**
	 * A JAXBContext that marshals/unmarshals related object to/from nested
	 * data.
	 */
	protected JAXBContext jcNotIdRef;

	public static Set<String> classesList = new HashSet<>();

	static List<URL> resources;

	static {
		resources = CPScanner.scanResources(new ResourceFilter().resourceName("META-INF").resourceName("*oxm.xml"));
		classesList.clear();
		for (URL url : resources) {
			LOGGER.info("-----------find oxm file:" + url.toExternalForm());
			try {
				XmlBindings bindings = RelationAsIDMoxyMetaSource.fromOXM(new InputStreamReader(url.openStream()));
				String packagename = bindings.getPackageName();
				for (JavaType javaType : bindings.getJavaTypes().getJavaType()) {
					classesList.add(packagename + "." + javaType.getName());
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}

	}

	public JAXBContext getJcNotIdRef() {
		return jcNotIdRef;
	}

	public static JAXBContext getMoxyContext(Class<?>... classesToBeBound) throws JAXBException {
		return org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(classesToBeBound, null);
	}

	public JaxbHelper() {
	}

	@PostConstruct
	public void init() {
		EntityManagerFactoryProvider pu = App.getPersistenceUnit();
		init(pu.getEMF(), pu.getEMFClassLoader());
	}

	public void reinit(@Observes TypeUpdated typeUpdated) {
		init();
	}

	public void init(EntityManagerFactory emf, DynamicClassLoader dcLoader) {
		try {
			// make sure it is inittialized
			emf.getMetamodel();
			// DynamicClassLoader
			// dcLoader=DynamicClassLoader.lookup(JpaHelper.getEntityManagerFactory(em).getServerSession());
			Map<String, Object> properties;

			List<Object> srcs = new ArrayList<Object>();

			srcs.add(getMetadataSource(emf));
			for (URL url : resources) {
				srcs.add(url.openStream());
			}
			properties = new HashMap<String, Object>(1);
			properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, srcs);
			jc = DynamicJAXBContextFactory.createContextFromOXM(dcLoader, properties);
			srcs.clear();

			srcs.add(new RelationAsDataMoxyMetaSource(emf));
			for (URL url : resources) {
				srcs.add(url.openStream());
			}
			properties = new HashMap<String, Object>(1);
			properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, srcs);
			jcNotIdRef = DynamicJAXBContextFactory.createContextFromOXM(dcLoader, properties);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public MetadataSource getMetadataSource(EntityManagerFactory emf) {
		RelationAsIDMoxyMetaSource src = new RelationAsIDMoxyMetaSource(emf);
		return src;
	}

	public JAXBContext getJAXBContext() {
		// return new CustomJAXBContext(jc);
		return jc;
	}

	public static class TempSchemaOutput extends SchemaOutputResolver {
		public Writer writer = new StringWriter();

		public Result createOutput(String uri, String suggestedFileName) {

			StreamResult result = new StreamResult(writer);
			// result.setSystemId(file.toURI().toURL().toString());
			return result;
		}
	}

	/**
	 * 
	 * @return XSD schema for all classes handled by this JAXB.
	 */
	public String generateSchema() {
		TempSchemaOutput sor = new TempSchemaOutput();
		try {
			this.jcNotIdRef.generateSchema(sor);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sor.writer.toString();

	}

	public void toXml(Object entity, Writer writer) {
		output(entity, writer, true, false);
	}

	public void toJson(Object entity, Writer writer) {
		output(entity, writer, false, false);
	}

	public void output(Object entity, Writer writer, boolean xml, boolean nestedEntity) {
		output(entity, writer, xml, nestedEntity, (ObjectGraph) null);
	}

	public void output(Object entity, Writer writer, boolean xml, boolean nestedEntity, List<String> props) {
		ObjectGraph graph = buildObjectGraph(entity.getClass(), props, jcNotIdRef);
		output(entity, writer, xml, nestedEntity, graph);
	}

	public void output(Object entity, Writer writer, boolean xml, boolean nestedEntity, String... props) {
		ObjectGraph graph = buildObjectGraph(entity.getClass(), Arrays.asList(props), jcNotIdRef);
		output(entity, writer, xml, nestedEntity, graph);
	}

	/**
	 * marshal
	 * 
	 * @param entity
	 *            object to marshal
	 * @param writer
	 *            where to marhsal to
	 * @param xml
	 *            xml or json
	 * @param nestedEntity
	 *            related object should be marshalled as nested data or just id.
	 * @param objectGraph
	 *            select which property to output. Property not in the list will
	 *            be ignored. null value accepted
	 */
	public void output(Object entity, Writer writer, boolean xml, boolean nestedEntity, ObjectGraph objectGraph) {

		try {
			Marshaller marshaller;
			if (nestedEntity) {
				marshaller = jcNotIdRef.createMarshaller();

			} else {
				marshaller = jc.createMarshaller();
			}
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			if (objectGraph != null) {
				marshaller.setProperty(MarshallerProperties.OBJECT_GRAPH, objectGraph);
			}
			if (!xml) {
				marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
				marshaller.setProperty("eclipselink.media-type", "application/json");
			}
			marshaller.marshal(entity, writer);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public Object fromXml(java.io.Reader reader, EntityManager em) {
		return input(reader, em, true, false);
	}

	public Object fromJson(java.io.Reader reader, EntityManager em) {
		return input(reader, em, false, false);
	}

	/**
	 * unmarshal
	 * 
	 * @param reader
	 *            input source
	 * @param em
	 *            entity Manager
	 * @param xml
	 *            xml or json source
	 * @param expectNestedEntity
	 *            expect related object is nested or just an ID.
	 * @return
	 */
	public Object input(java.io.Reader reader, EntityManager em, boolean xml, boolean expectNestedEntity) {
		try {

			Unmarshaller unmarshaller = null;
			if (expectNestedEntity) {
				unmarshaller = jcNotIdRef.createUnmarshaller();
			} else {
				unmarshaller = jc.createUnmarshaller();
			}
			unmarshaller.setProperty(UnmarshallerProperties.ID_RESOLVER, new DynamicIDResolver());
			if (!xml) {
				unmarshaller.setProperty("eclipselink.media-type", "application/json");
			}
			Object object = unmarshaller.unmarshal(reader);

			return object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public JAXBContext getJc() {
		return jc;
	}

	/**
	 * Object graph is not used right now. Filtering is done by JPA
	 */
	public static org.eclipse.persistence.jaxb.Subgraph populateSubgraph(org.eclipse.persistence.jaxb.Subgraph subgraph, List<String> props) {
		subgraph.addAttributeNodes(FleximsDynamicEntityImpl.ID_NAME, FleximsDynamicEntityImpl.VERSION_NAME);
		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				subgraph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}
		for (String key : subkeysMap.keySet()) {
			org.eclipse.persistence.jaxb.Subgraph subgraph1 = subgraph.addSubgraph(key);
			populateSubgraph(subgraph1, subkeysMap.get(key));
		}
		return subgraph;

	}

	public static ObjectGraph buildObjectGraph(Class<?> clz, List<String> props, JAXBContext jcc) {
		ObjectGraph graph = JAXBHelper.getJAXBContext(jcc).createObjectGraph(clz);
		graph.addAttributeNodes(FleximsDynamicEntityImpl.ID_NAME, FleximsDynamicEntityImpl.VERSION_NAME);

		Map<String, List<String>> subkeysMap = new HashMap<String, List<String>>();
		for (String prop : props) {
			int idx = prop.indexOf('.');

			if (idx == -1) {
				graph.addAttributeNodes(prop);
				continue;
			}
			String subkey = prop.substring(0, idx);
			if (!subkeysMap.containsKey(subkey)) {
				subkeysMap.put(subkey, new ArrayList<String>());
			}
			subkeysMap.get(subkey).add(prop.substring(idx + 1));
		}

		for (String key : subkeysMap.keySet()) {
			org.eclipse.persistence.jaxb.Subgraph subgraph = graph.addSubgraph(key);
			populateSubgraph(subgraph, subkeysMap.get(key));
		}
		return graph;
	}

	public static ObjectGraph buildObjectGraph(EntityManager em, String typename, List<String> props, JAXBContext jcc) {
		return buildObjectGraph(JpaHelper.getEntityClass(em, typename), props, jcc);
	}

	public static JAXBContext createMoxyJaxbContext(Class<?>... clzs) throws JAXBException {
		JAXBContext ctx = JAXBContextFactory.createContext(clzs, null);
		return ctx;

	}

	public static Marshaller jsonMarshaller(JAXBContext context) throws JAXBException {
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		return marshaller;

	}
}
