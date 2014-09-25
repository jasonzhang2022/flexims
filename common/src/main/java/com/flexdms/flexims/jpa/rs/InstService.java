package com.flexdms.flexims.jpa.rs;

import static com.flexdms.flexims.rsutil.RsHelper.isXml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;

import com.flexdms.flexims.App;
import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.EntityManagerFactoryProvider;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jaxb.moxy.RelationAsIDMoxyMetaSource;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.Entities;

/**
 * Manage instance view/edit/delete
 * 
 * @author jason.zhang
 * 
 */
@Path("/inst")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class InstService {

	// this does not work under weld
	// @Context Request rs;

	@Inject
	EntityDAO dao;

	@Inject
	EntityManager em;

	@Inject
	JaxbHelper jaxbHelper;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/get/{type}/{id}")
	@GET
	@Transactional
	public FleximsDynamicEntityImpl getSingle(@PathParam("type") String type, @PathParam("id") long id,
			@QueryParam("refresh") @DefaultValue("true") boolean refresh) throws Exception {
		// always refresh
		if (refresh) {
			em.clear();
			em.getEntityManagerFactory().getCache().evict(JpaHelper.getEntityClass(em, type), id);
		}
		FleximsDynamicEntityImpl entityImpl = dao.loadEntity(type, id);
		return entityImpl;
	}

	@Path("/jaxbmap")
	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public Response getJaxbMap() {
		EntityManagerFactoryProvider pu = App.getPersistenceUnit();
		RelationAsIDMoxyMetaSource src = new RelationAsIDMoxyMetaSource(pu.getEMF());
		final XmlBindings bindings = src.getXmlBindings(null, pu.getEMFClassLoader());
		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				try {
					RelationAsIDMoxyMetaSource.toOXM(bindings, new OutputStreamWriter(output));
				} catch (JAXBException e) {
					throw new WebApplicationException(e);
				}

			}
		};
		return Response.ok(stream).build();
	}

	@Path("/jaxbxsd")
	@GET
	@Produces({ MediaType.APPLICATION_XML })
	public Response getJaxbXsd() {
		StreamingOutput stream = new StreamingOutput() {

			@Override
			public void write(OutputStream output) throws IOException {
				try {
					JaxbHelper.TempSchemaOutput resolver = new JaxbHelper.TempSchemaOutput();
					resolver.writer = new OutputStreamWriter(output);
					jaxbHelper.getJc().generateSchema(resolver);
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}

			}
		};
		return Response.ok(stream).build();
	}

	// save or update
	@Path("/save")
	@POST
	@Transactional
	public FleximsDynamicEntityImpl saveSingle(Reader reader, @Context Request rs) throws Exception {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) jaxbHelper.input(reader, em, isXml(rs), false);
		// EntityDAO dao = getEntityDAO();
		return dao.saveEntity(inst);

	}

	@Path("/savebatch")
	@POST
	@Transactional
	public Entities saveBatch(Reader reader, @Context Request rs) throws Exception {
		Entities entities = (Entities) jaxbHelper.input(reader, em, isXml(rs), false);
		for (Object obj : entities.getItems()) {
			FleximsDynamicEntityImpl entity = (FleximsDynamicEntityImpl) obj;
			em.persist(entity);
			em.flush(); // very important so EntityListener can add more
						// modification to this transaction
		}
		return entities;
	}

	@Path("/savenested")
	@POST
	@Transactional
	public Response saveNested(Reader reader, @Context Request rs) throws Exception {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) jaxbHelper.input(reader, em, isXml(rs), true);
		// EntityDAO dao = getEntityDAO();
		FleximsDynamicEntityImpl ret = dao.saveEntity(inst);
		StringWriter stringWriter = new StringWriter();
		jaxbHelper.output(ret, stringWriter, isXml(rs), true);
		return Response.ok(stringWriter.toString()).build();
	}

	@Path("/update")
	@POST
	public FleximsDynamicEntityImpl updateSingle(Reader reader, @Context Request rs) throws Exception {
		return saveSingle(reader, rs);
	}

	@Path("/updatebatch")
	@POST
	@Transactional
	public Entities updateBatch(Reader reader, @Context Request rs) throws Exception {
		List<FleximsDynamicEntityImpl> itemsList = new LinkedList<FleximsDynamicEntityImpl>();
		Entities entities = (Entities) jaxbHelper.input(reader, em, isXml(rs), false);
		for (Object obj : entities.getItems()) {
			FleximsDynamicEntityImpl entity = (FleximsDynamicEntityImpl) obj;
			itemsList.add(em.merge(entity));
			em.flush(); // very important so EntityListener can add more
						// modification to this transaction
		}
		entities.setItems(itemsList);
		return entities;
	}

	@Path("/delete/{type}/{id}")
	@DELETE
	public FleximsDynamicEntityImpl deleteSingle(@PathParam("type") String type, @PathParam("id") long id) {
		// return getEntityDAO().deleteEntity(type, id);
		return dao.deleteEntity(type, id);
	}

	@Path("/delete")
	@POST
	@Transactional
	public FleximsDynamicEntityImpl deleteEntity(Reader reader, @Context Request rs) {
		FleximsDynamicEntityImpl inst = (FleximsDynamicEntityImpl) jaxbHelper.input(reader, em, isXml(rs), false);
		return deleteSingle(inst.getClass().getSimpleName(), inst.getId());
	}

}
