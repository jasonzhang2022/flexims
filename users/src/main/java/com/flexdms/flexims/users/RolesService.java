package com.flexdms.flexims.users;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;

import org.eclipse.persistence.dynamic.DynamicEntity;

import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.rsutil.AppMsg;
import com.flexdms.flexims.rsutil.Entities;
import com.flexdms.flexims.util.SessionCtx;

@Path("/role")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class RolesService {

	@Context
	HttpServletRequest request;

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

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@SuppressWarnings("rawtypes")
	@Path("/allroles")
	@GET
	public Entities getAllRoles() {
		EntityGraph<? extends DynamicEntity> grapp = em.createEntityGraph(JpaHelper.getEntityClass(em, "FxRole"));
		grapp.addAttributeNodes("Name");
		grapp.addAttributeNodes("id");
		Query query = em.createQuery("select r from FxRole r");
		query.setHint("javax.persistence.fetchgraph", grapp);
		query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
		query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS);

		List list = query.getResultList();

		Entities entities = new Entities();
		entities.setItems(list);
		return entities;
	}

	@SuppressWarnings("rawtypes")
	@Path("/allusers")
	@GET
	public Entities getAllUsers() {
		EntityGraph<? extends DynamicEntity> grapp = em.createEntityGraph(JpaHelper.getEntityClass(em, "FxRole"));
		grapp.addAttributeNodes("Name");
		grapp.addAttributeNodes("id");
		Query query = em.createQuery("select u from FxUser u");
		query.setHint("javax.persistence.fetchgraph", grapp);
		query.setHint("javax.persistence.cache.retrieveMode", CacheRetrieveMode.BYPASS);
		query.setHint("javax.persistence.cache.storeMode", CacheStoreMode.BYPASS);

		List list = query.getResultList();

		Entities entities = new Entities();
		entities.setItems(list);
		return entities;
	}


	@Path("/inrole/{username}/{role}")
	@GET
	public AppMsg isInRole(@PathParam("username") String username, @PathParam("role") String role) {
		FxRole user = FxRole.loadByName(username, em);
		AppMsg msg = new AppMsg();
		msg.setStatuscode(1); // not included.
		if (user.isIncludedBy(FxRole.loadByName(role, em))) {
			msg.setStatuscode(0);
		}
		return msg;
	}

}
