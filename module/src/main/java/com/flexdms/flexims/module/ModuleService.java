package com.flexdms.flexims.module;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.RunAsAdmin;
import com.flexdms.flexims.RunInTransaction;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;
import com.flexdms.flexims.query.QueryHelper;
import com.flexdms.flexims.report.rs.helper.FxReportWrapper;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.util.SessionCtx;

/**
 * Service manage user authentication.
 * 
 * @author jason.zhang
 * 
 */
@Path("/module")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class ModuleService {

	@Context
	Request rs;

	@Inject
	EntityDAO dao;

	@Inject
	EntityManager em;

	@Inject
	SessionCtx sessionCtx;

	@Inject
	JaxbHelper jaxbHelper;

	@Inject
	RunInTransaction runInTransaction;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/upload")
	@POST
	@RunAsAdmin
	public AppMsg upload() {

		AppMsg msg = new AppMsg();
		try {
			msg.setMsg("Ok");
		} catch (Exception e) {
			msg.setStatuscode(1);
			msg.setMsg("Invalid Email or Password");
		}
		return msg;
	}

	@Path("/download")
	@POST
	public Response download(final ModuleDefinition moduleDefinition) {
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException {
				try {
					ZipOutputStream zip = new JarOutputStream(output);
					buildModule(zip, moduleDefinition);

					zip.close();
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}

			}
		};
		ResponseBuilder builder = Response.ok(stream).type("application/java-archive").header("Content-Disposition", "attachment;filename=batch.zip");
		return builder.build();

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void buildModule(final ZipOutputStream zip, final ModuleDefinition moduleDefinition) throws Exception {

		ZipEntry zipEntry = null;
		StringWriter writer = null;

		// ----------------------------report and query
		final String[] names = moduleDefinition.types.split(",");
		final Entities entities = new Entities();
		runInTransaction.run(new Runnable() {

			@Override
			public void run() {
				if (moduleDefinition.reporttype.equalsIgnoreCase("all")) {
					for (String type : names) {
						entities.getItems().addAll(QueryHelper.loadQueriesForType(type));
						entities.getItems().addAll(QueryHelper.loadReportForType(type));
					}
				} else {
					Set etSet = new HashSet();
					for (String type : names) {
						ClassAccessor accessor = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), type);
						List<Long> reportIds = collectRelationUI(accessor);
						for (Long reportid : reportIds) {
							FleximsDynamicEntityImpl report = dao.loadEntity(FxReportWrapper.TYPE_NAME, reportid);
							if (report != null) {
								FleximsDynamicEntityImpl query = report.get(FxReportWrapper.PROP_NAME_PROP_QUERY);
								etSet.add(report);
								etSet.add(query);
							}
						}
					}
					entities.getItems().addAll(etSet);
				}

			}
		});

		Map<Long, Long> idMap = new HashMap<>();
		if (!entities.getItems().isEmpty()) {
			// assign ID.
			long currentQueryId = moduleDefinition.queryStartid;
			long currentReportId = moduleDefinition.reportStartid;
			for (Object m : entities.getItems()) {
				em.detach(m);
				FleximsDynamicEntityImpl currentEntity = (FleximsDynamicEntityImpl) m;
				if (!currentEntity.getClass().getSimpleName().equals(FxReportWrapper.TYPE_NAME)) {
					currentEntity.setId(currentQueryId++);
				} else {
					idMap.put(currentEntity.getId(), currentReportId);
					currentEntity.setId(currentReportId++);
				}
			}

			zipEntry = new ZipEntry("META-INF/query_" + moduleDefinition.module + "_data.fxext.xml");
			writer = new StringWriter();
			jaxbHelper.toXml(entities, writer);
			String queryReportString = writer.toString();
			zipEntry.setSize(queryReportString.length());
			zip.putNextEntry(zipEntry);
			zip.write(queryReportString.getBytes());
			zip.closeEntry();
		}

		// -------------------------model xml
		zipEntry = new ZipEntry("META-INF/" + moduleDefinition.module + "_model_orm.xml");
		writer = new StringWriter();
		XMLEntityMappings mappings = getMappingFile(moduleDefinition.types, moduleDefinition.module, idMap);
		MetaSourceBuilder.toXml(mappings, writer);
		String mapString = writer.toString();
		zipEntry.setSize(mapString.length());
		zip.putNextEntry(zipEntry);
		zip.write(mapString.getBytes());
		zip.closeEntry();

		// ------------------------------data.
		if (moduleDefinition.withdata) {
			Entities entities1 = new Entities();
			List instancess = runInTransaction.call(new Callable<List>() {

				@Override
				public List call() throws Exception {
					List iss = new ArrayList(names.length);
					for (String type : names) {
						List instances = QueryHelper.loadAll(type);
						iss.add(instances);
					}
					return iss;
				}
			});
			for (Object obj : instancess) {
				long currentId = moduleDefinition.startid;
				List instances = (List) obj;
				for (Object e : instances) {
					em.detach(e);
					((FleximsDynamicEntityImpl) e).setId(currentId++);
				}
				entities1.getItems().addAll(instances);
			}

			zipEntry = new ZipEntry("META-INF/data_" + moduleDefinition.module + "_data.fxext.xml");
			writer = new StringWriter();
			jaxbHelper.toXml(entities1, writer);
			String instancesString = writer.toString();
			zipEntry.setSize(instancesString.length());
			zip.putNextEntry(zipEntry);
			zip.write(instancesString.getBytes());
			zip.closeEntry();
		}

	}

	protected XMLEntityMappings getMappingFile(String types, String module, Map<Long, Long> idMap) {
		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		Set<String> processed = new HashSet<>();
		String[] names = types.split(",");
		for (String typename : names) {
			if (processed.contains(typename)) {
				continue;
			}
			ClassAccessor accessor = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), typename);
			if (accessor == null) {
				continue;
			}
			JpaMapHelper.addClassAccessor(entityMaps, accessor);

			// -------------------------table key generator
			TableGeneratorMetadata meta = JpaMapHelper.retrieveTableGenerator(DynamicMetaSource.getEntityMaps(), accessor);
			if (meta != null) {
				entityMaps.getTableGenerators().add(meta);
			}
		}

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		for (ClassAccessor accessor : entityMaps.getEntities()) {
			// ------------------------module name.
			adjustModule(accessor, module);

			// relation.ui
			adjustRelationUI(accessor, idMap);
		}
		for (ClassAccessor accessor : entityMaps.getEmbeddables()) {
			// ------------------------module name.
			adjustModule(accessor, module);

			// relation.ui
			adjustRelationUI(accessor, idMap);
		}

		return entityMaps;
	}

	protected void adjustModule(ClassAccessor accessor, String module) {
		boolean found = false;
		boolean adjustModule = false;
		for (PropertyMetadata propertyMetadata : accessor.getProperties()) {
			if (propertyMetadata.getName().equalsIgnoreCase("module")) {
				found = true;
				if (!propertyMetadata.getValue().equalsIgnoreCase(module)) {
					adjustModule = true;
				}
				break;
			}
		}
		if (!found) {
			PropertyMetadata propertyMetadata = new PropertyMetadata();
			propertyMetadata.setName("module");
			propertyMetadata.setValue(module);
			accessor.getProperties().add(propertyMetadata);
			adjustModule = true;
		}

		if (adjustModule) {
			// TODO adjust table schema, and JPA Entity Name
		}
	}

	protected void adjustRelationUI(ClassAccessor accessor, Map<Long, Long> idMap) {
		String propname = "relationui.report";
		if (accessor.getAttributes()==null) {
			return;
		}
		for (OneToOneAccessor prop : accessor.getAttributes().getOneToOnes()) {
			for (PropertyMetadata propmeta : prop.getProperties()) {
				if (propmeta.getName().equalsIgnoreCase(propname)) {
					propmeta.setValue(idMap.get(Long.parseLong(propmeta.getValue())).toString());
				}
			}
		}
		for (OneToManyAccessor prop : accessor.getAttributes().getOneToManys()) {
			for (PropertyMetadata propmeta : prop.getProperties()) {
				if (propmeta.getName().equalsIgnoreCase(propname)) {
					propmeta.setValue(idMap.get(Long.parseLong(propmeta.getValue())).toString());
				}
			}
		}
		for (ManyToManyAccessor prop : accessor.getAttributes().getManyToManys()) {
			for (PropertyMetadata propmeta : prop.getProperties()) {
				if (propmeta.getName().equalsIgnoreCase(propname)) {
					propmeta.setValue(idMap.get(Long.parseLong(propmeta.getValue())).toString());
				}
			}
		}
		for (ManyToOneAccessor prop : accessor.getAttributes().getManyToOnes()) {
			for (PropertyMetadata propmeta : prop.getProperties()) {
				if (propmeta.getName().equalsIgnoreCase(propname)) {
					propmeta.setValue(idMap.get(Long.parseLong(propmeta.getValue())).toString());
				}
			}
		}
	}

	protected List<Long> collectRelationUI(ClassAccessor accessor) {
		String propname = "relationui.report";
		List<Long> reportIds = new ArrayList<Long>(4);
		if (accessor.getAttributes()==null) {
			return reportIds;
		}
		if (accessor.getAttributes().getOneToOnes() != null) {
			for (OneToOneAccessor prop : accessor.getAttributes().getOneToOnes()) {
				for (PropertyMetadata propmeta : prop.getProperties()) {
					if (propmeta.getName().equalsIgnoreCase(propname)) {
						reportIds.add(Long.parseLong(propmeta.getValue()));
					}
				}
			}
		}
		if (accessor.getAttributes().getOneToManys() != null) {
			for (OneToManyAccessor prop : accessor.getAttributes().getOneToManys()) {
				for (PropertyMetadata propmeta : prop.getProperties()) {
					if (propmeta.getName().equalsIgnoreCase(propname)) {
						reportIds.add(Long.parseLong(propmeta.getValue()));
					}
				}
			}
		}
		if (accessor.getAttributes().getManyToManys() != null) {
			for (ManyToManyAccessor prop : accessor.getAttributes().getManyToManys()) {
				for (PropertyMetadata propmeta : prop.getProperties()) {
					if (propmeta.getName().equalsIgnoreCase(propname)) {
						reportIds.add(Long.parseLong(propmeta.getValue()));
					}
				}
			}
		}
		if (accessor.getAttributes().getManyToOnes() != null) {
			for (ManyToOneAccessor prop : accessor.getAttributes().getManyToOnes()) {
				for (PropertyMetadata propmeta : prop.getProperties()) {
					if (propmeta.getName().equalsIgnoreCase(propname)) {
						reportIds.add(Long.parseLong(propmeta.getValue()));
					}
				}
			}
		}
		return reportIds;
	}

}
