package com.flexdms.flexims.accesscontrol.rs;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.metamodel.ManagedType;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.flexdms.flexims.EntityDAO;
import com.flexdms.flexims.RunAsAdmin;
import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.AuthorizedException;
import com.flexdms.flexims.accesscontrol.FxsecurityEM;
import com.flexdms.flexims.accesscontrol.PermissionChecker;
import com.flexdms.flexims.accesscontrol.RoleContext;
import com.flexdms.flexims.accesscontrol.action.GrantAction;
import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.PropertyPermission;
import com.flexdms.flexims.accesscontrol.model.RolePermission;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.jpa.JpaMetamodelHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.event.InstanceAction.InstanceActionType;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.users.FxRole;

@Path("/acl")
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RequestScoped
public class ACLService {

	@Inject
	EntityDAO dao;

	@Inject
	EntityManager em;

	@Inject
	@FxsecurityEM
	EntityManager securityEM;

	@Inject
	PermissionChecker permissionChecker;

	@Inject
	RoleContext roleContext;

	@Path("/status")
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String status() {
		return "ok";
	}

	@Path("/allactions")
	@GET
	public NameValueList allActions() {
		NameValueList mList = new NameValueList();
		for (Action action : ACLHelper.getAvailableActions()) {
			mList.addPair(action.getName(), action.getDescription());
		}
		return mList;
	}

	/**
	 * Anyone can read typeacl.
	 * 
	 * @param typename
	 * @return
	 */
	@Path("/typeacl/{typename}")
	@GET
	public TypeACL getTypeACL(@PathParam("typename") String typename) {
		TypeACL typeACL = securityEM.find(TypeACL.class, typename);
		if (typeACL == null) {
			typeACL = new TypeACL();
			typeACL.setTypeid(typename);
		}
		for (RolePermission p : typeACL.getRolePermissions()) {
			p.setTypeACL(null);
		}
		for (PropertyPermission p : typeACL.getPropPermissions()) {
			p.setTypeACL(null);
		}
		return typeACL;
	}

	@Path("/typeacl")
	@POST
	public void saveTypeACL(TypeACL typeACL) {
		if (permissionChecker.hasPermission(ACLHelper.getActionByName(GrantAction.NAME), roleContext.getRoles(), typeACL.getTypeid(), null)) {
			throw new AuthorizedException(InstanceActionType.GRANT, null);
		}
		for (RolePermission p : typeACL.getRolePermissions()) {
			p.setTypeACL(typeACL);
		}
		for (PropertyPermission p : typeACL.getPropPermissions()) {
			p.setTypeACL(typeACL);
		}
		securityEM.getTransaction().begin();
		securityEM.merge(typeACL);
		securityEM.getTransaction().commit();
		ACLHelper.typeacls.put(typeACL.getTypeid(), typeACL);
	}

	@SuppressWarnings("unchecked")
	@Path("/instacl/{typename}/{id}")
	@GET
	public InstanceACES getInstACL(@PathParam("typename") String typename, @PathParam("id") long id) {
		// if we can load, then we have read permission.
		dao.loadEntity(typename, id);

		Query query = securityEM.createNamedQuery(InstanceACE.ACLQNAME);
		query.setParameter("typeid", typename);
		query.setParameter("instanceid", id);

		List<InstanceACE> aces = (List<InstanceACE>) query.getResultList();
		InstanceACES aces1 = new InstanceACES();
		aces1.setAces(aces);

		return aces1;
	}

	@SuppressWarnings("unchecked")
	@Path("/instacl/{typename}/{id}")
	@POST
	public void saveInstACL(@PathParam("typename") String typename, @PathParam("id") long id, InstanceACES aces) {

		FleximsDynamicEntityImpl entity = dao.loadEntity(typename, id);
		if (entity == null) {
			return;
		}

		if (permissionChecker.hasPermission(ACLHelper.getActionByName(GrantAction.NAME), roleContext.getRoles(), entity.getClass().getSimpleName(),
				entity)) {
			throw new AuthorizedException(InstanceActionType.GRANT, entity);
		}

		securityEM.getTransaction().begin();
		Query query = securityEM.createNamedQuery(InstanceACE.ACLQNAME);
		query.setParameter("typeid", typename);
		query.setParameter("instanceid", id);

		List<InstanceACE> acesOld = (List<InstanceACE>) query.getResultList();
		for (InstanceACE ace : aces.getAces()) {
			if (ace.getId() == 0) {
				securityEM.persist(ace);
			} else {
				for (InstanceACE oldAce : acesOld) {
					if (oldAce.getId() == ace.getId()) {
						securityEM.merge(ace);
						acesOld.remove(oldAce);
						break;
					}
				}
			}
		}
		for (InstanceACE oldAce : acesOld) {
			securityEM.remove(oldAce);
		}
		securityEM.getTransaction().commit();
	}

	@Path("/typepermissions/{username}/{action}")
	@GET
	@RunAsAdmin
	public NameValueList hasTypePermission(@PathParam("username") String username, @PathParam("action") String actionName) {
		List<FxRole> roles = new ArrayList<>(1);
		FxRole user = FxRole.loadByName(username, em);
		roles.add(user);

		NameValueList mList = new NameValueList();
		Action action = ACLHelper.getActionByName(actionName);
		for (ManagedType<?> t : JpaMetamodelHelper.getMetamodel().getManagedTypes()) {
			if (permissionChecker.hasPermission(action, roles, t.getJavaType().getSimpleName(), null)) {
				mList.addPair(t.getJavaType().getSimpleName(), "true");
			} else {
				mList.addPair(t.getJavaType().getSimpleName(), "false");
			}
		}
		return mList;
	}

	@Path("/permissions/{username}/{typename}")
	@GET
	@RunAsAdmin
	public NameValueList hasPermission(@PathParam("username") String username, @PathParam("typename") String typename) {
		return hasPermission(username, typename, 0);
	}

	@Path("/permissions/{username}/{typename}/{id}")
	@GET
	@RunAsAdmin
	public NameValueList hasPermission(@PathParam("username") String username, @PathParam("typename") String typename, @PathParam("id") long id) {

		List<FxRole> roles = new ArrayList<>(1);
		FxRole user = FxRole.loadByName(username, em);
		roles.add(user);

		NameValueList mList = new NameValueList();

		FleximsDynamicEntityImpl entity = null;
		if (id != 0) {
			entity = dao.loadEntity(typename, id);
		}

		for (Action action : ACLHelper.getAvailableActions()) {
			if (permissionChecker.hasPermission(action, roles, typename, entity)) {
				mList.addPair(action.getName(), "true");
			} else {
				mList.addPair(action.getName(), "false");
			}
		}
		return mList;
	}

}
