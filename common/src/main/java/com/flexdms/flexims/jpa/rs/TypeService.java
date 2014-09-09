package com.flexdms.flexims.jpa.rs;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.naming.NamingException;
import javax.persistence.metamodel.Attribute;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.MapPersister;
import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.HttpCodeException;
import com.flexdms.flexims.type.TypeUtil;
import com.flexdms.flexims.util.CodedExceptionImpl;
import com.flexdms.flexims.util.Utils;

/**
 * Manage type retrieve and modification
 * 
 * @author jason.zhang
 * 
 */
@Path("/type")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class TypeService {

	public static final int TYPE_REFERED = 600;
	public static final int PROP_REFERED = 601;

	@Inject
	MapPersister mapPersister;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/meta")
	@GET
	public XMLEntityMappings getMeta() throws NamingException {
		return DynamicMetaSource.getEntityMaps();

	}

	@Path("/metajs/{jsonp}")
	@GET
	@Produces("application/javascript")
	public String getMetaJson(@PathParam("jsonp") String jsonp) throws NamingException {
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toJson(DynamicMetaSource.getEntityMaps(), writer);
		return jsonp + "(" + writer.toString() + ")";
	}

	@Path("/schema")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getSchema() throws Exception {
		return App.getPersistenceUnit().getSchema();
	}

	@Path("/checkname/{typename}")
	@GET
	public AppMsg checkTypeName(@PathParam("typename") String typeName) throws Exception {
		Connection con = App.getConnection();
		String msg = TypeUtil.validateTypeName(typeName, con, false);
		con.close();

		AppMsg ret = new AppMsg();
		if (msg != null) {
			ret.setStatuscode(1);
			ret.setMsg(msg);

		}
		return ret;
	}

	@Path("/checkname/{typename}/{propname}")
	@GET
	public AppMsg checkPropName(@PathParam("typename") String typeName, @PathParam("propname") String propName) throws Exception {
		Connection con = App.getConnection();
		String msg = TypeUtil.validatePropName(typeName, propName, con, false);
		con.close();

		AppMsg ret = new AppMsg();
		if (msg != null) {
			ret.setStatuscode(1);
			ret.setMsg(msg);
		}
		return ret;
	}

	@GET
	@Path("/getsingle/{typename}")
	public XMLEntityMappings getSingleType(@PathParam("typename") String typename) throws Exception {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		ClassAccessor accessor = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), typename);
		if (accessor == null) {
			throw new HttpCodeException(Status.NO_CONTENT.getStatusCode(), "type " + typename + " is not found");
		}

		JpaMapHelper.addClassAccessor(entityMaps, accessor);
		TableGeneratorMetadata meta = JpaMapHelper.retrieveTableGenerator(DynamicMetaSource.getEntityMaps(), accessor);
		if (meta != null) {
			entityMaps.getTableGenerators().add(meta);
		}

		return entityMaps;
	}

	@GET
	@Path("/getmultiple/{types}")
	public XMLEntityMappings getMutlipleType(@PathParam("types") String types) throws Exception {

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
			TableGeneratorMetadata meta = JpaMapHelper.retrieveTableGenerator(DynamicMetaSource.getEntityMaps(), accessor);
			if (meta != null) {
				entityMaps.getTableGenerators().add(meta);
			}
		}
		return entityMaps;
	}

	@POST
	@Path("savetype")
	public XMLEntityMappings save(XMLEntityMappings entityMappings) throws Exception {
		String name = null;
		if (!entityMappings.getEntities().isEmpty()) {

			EntityAccessor entityAccessor = entityMappings.getEntities().get(0);
			name = entityAccessor.getName();
			name = Utils.upperFirstLetter(name);
			entityAccessor.setName(name);
			entityAccessor.setClassName(name);

			DynamicMetaSource.getEntityMaps().getEntities().add(entityAccessor);
			if (entityAccessor.getParentClassName().equals(FleximsDynamicEntityImpl.class.getName())) {
				MetaSourceBuilder.addID(entityAccessor, DynamicMetaSource.getEntityMaps());
				MetaSourceBuilder.addVersion(entityAccessor);
			} else {
				boolean foundParent = false;
				EntityAccessor parent = null;
				for (EntityAccessor entityAccessor1 : DynamicMetaSource.getEntityMaps().getEntities()) {
					if (entityAccessor1.getClassName().equalsIgnoreCase(entityAccessor.getParentClassName())) {
						parent = entityAccessor1;
						break;
					}
				}

				if (parent != null) {
					foundParent = true;
					MetaSourceBuilder.establishInheritance(entityAccessor, parent);
				} else {
					for (MappedSuperclassAccessor entityAccessor2 : DynamicMetaSource.getEntityMaps().getMappedSuperclasses()) {
						if (entityAccessor2.getClassName().equalsIgnoreCase(entityAccessor.getParentClassName())) {
							foundParent = true;
							break;
						}
					}
					MetaSourceBuilder.addID(entityAccessor, DynamicMetaSource.getEntityMaps());
					MetaSourceBuilder.addVersion(entityAccessor);
				}

				if (!foundParent) {
					// ignore parent from client.
					entityAccessor.setParentClassName(FleximsDynamicEntityImpl.class.getName());
					MetaSourceBuilder.addID(entityAccessor, DynamicMetaSource.getEntityMaps());
					MetaSourceBuilder.addVersion(entityAccessor);
				}
			}

		} else if (!entityMappings.getEmbeddables().isEmpty()) {

			EmbeddableAccessor embeddableAccessor = entityMappings.getEmbeddables().get(0);
			name = embeddableAccessor.getName();
			name = Utils.upperFirstLetter(name);
			embeddableAccessor.setName(name);
			embeddableAccessor.setClassName(name);
			DynamicMetaSource.getEntityMaps().getEmbeddables().add(embeddableAccessor);
		}

		DynamicMetaSource.normalize();
		App.getPersistenceUnit().refresh(false);
		mapPersister.save(DynamicMetaSource.getEntityMaps());
		return getSingleType(name);
	}

	@Path("saveprop")
	@POST
	public XMLEntityMappings saveProp(XMLEntityMappings entityMappings) throws Exception {
		return saveProp(entityMappings, true);
	}

	@Path("minorsaveprop")
	@POST
	public XMLEntityMappings minorSaveProp(XMLEntityMappings entityMappings) throws Exception {
		return saveProp(entityMappings, false);
	}

	public XMLEntityMappings saveProp(XMLEntityMappings entityMappings, boolean refreshPu) throws Exception {
		String name = null;
		if (!entityMappings.getEntities().isEmpty()) {
			EntityAccessor entityAccessor = entityMappings.getEntities().get(0);
			name = entityAccessor.getName();
			int index = 0;
			for (EntityAccessor entityAccessor2 : DynamicMetaSource.getEntityMaps().getEntities()) {
				if (entityAccessor2.getName().equalsIgnoreCase(name)) {
					break;
				}
				index++;
			}
			DynamicMetaSource.getEntityMaps().getEntities().remove(index);

			DynamicMetaSource.getEntityMaps().getEntities().add(entityAccessor);
		} else if (!entityMappings.getEmbeddables().isEmpty()) {
			EmbeddableAccessor embeddableAccessor = entityMappings.getEmbeddables().get(0);
			name = embeddableAccessor.getName();

			int index = 0;
			for (EmbeddableAccessor embeddableAccessor2 : DynamicMetaSource.getEntityMaps().getEmbeddables()) {
				if (embeddableAccessor2.getName().equalsIgnoreCase(name)) {
					break;
				}
				index++;
			}
			DynamicMetaSource.getEntityMaps().getEmbeddables().remove(index);
			DynamicMetaSource.getEntityMaps().getEmbeddables().add(embeddableAccessor);
		}
		DynamicMetaSource.normalize();
		if (refreshPu) {
			App.getPersistenceUnit().refresh(false);
		}
		mapPersister.save(DynamicMetaSource.getEntityMaps());
		return getSingleType(name);
	}

	@Path("deleteprop/{typename}/{propname}")
	@GET
	public XMLEntityMappings deleteProp(@PathParam("typename") String typeName, @PathParam("propname") String propName) throws Exception {
		ClassAccessor entity = JpaMapHelper.findClassAccessor(DynamicMetaSource.getEntityMaps(), typeName);
		Attribute<?, ?> attr = JpaMetamodelHelper.getAttribute(typeName, propName);
		if (JpaMapHelper.findPropertyBbyMappedBy(JpaMetamodelHelper.getAttributeType(attr).getSimpleName(), propName) != null) {
			throw new CodedExceptionImpl(PROP_REFERED, "Deletion can not be performed. Target Property is referred by other types");
		}

		JpaMapHelper.deleteProp(entity, propName);
		App.getPersistenceUnit().refresh(false);
		mapPersister.save(DynamicMetaSource.getEntityMaps());
		return DynamicMetaSource.getEntityMaps();
	}

	@Path("deletetype/{typename}")
	@GET
	public XMLEntityMappings deleteType(@PathParam("typename") String typeName) throws Exception {
		List<Attribute<?, ?>> attrs = JpaMetamodelHelper.findReferringProps(typeName, true);
		if (!attrs.isEmpty()) {
			throw new CodedExceptionImpl(TYPE_REFERED, "Deletion can not be performed. Target type is referred by other types");
		}
		JpaMapHelper.deleteClassAccessor(DynamicMetaSource.getEntityMaps(), typeName);
		App.getPersistenceUnit().refresh(false);
		mapPersister.save(DynamicMetaSource.getEntityMaps());
		return DynamicMetaSource.getEntityMaps();
	}

	@Path("/updatetype")
	@POST
	public XMLEntityMappings updateType(XMLEntityMappings entityMappings) throws Exception {
		String name = null;
		if (!entityMappings.getEntities().isEmpty()) {

			EntityAccessor entityAccessor = entityMappings.getEntities().get(0);
			name = entityAccessor.getName();
			name = Utils.upperFirstLetter(name);
			entityAccessor.setName(name);
			entityAccessor.setClassName(name);
			JpaMapHelper.replaceClassAccessor(DynamicMetaSource.getEntityMaps(), entityAccessor);
		} else if (!entityMappings.getEmbeddables().isEmpty()) {

			EmbeddableAccessor embeddableAccessor = entityMappings.getEmbeddables().get(0);
			name = embeddableAccessor.getName();
			name = Utils.upperFirstLetter(name);
			embeddableAccessor.setName(name);
			embeddableAccessor.setClassName(name);
			JpaMapHelper.replaceClassAccessor(DynamicMetaSource.getEntityMaps(), embeddableAccessor);
		}
		DynamicMetaSource.normalize();
		mapPersister.save(DynamicMetaSource.getEntityMaps());
		return getSingleType(name);
	}

}
