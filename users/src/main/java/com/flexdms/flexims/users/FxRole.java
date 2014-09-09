package com.flexdms.flexims.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

public class FxRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	FleximsDynamicEntityImpl entityImpl;

	public static final String TYPE_NAME = "FxRole";
	public static final String PROP_NAME_PROP_NAME = "Name";

	public static final String PROP_NAME_PROP_SUBROLE = "SubRole";
	public static final String PROP_NAME_PROP_INCLUEDEBY = "IncludedBy";

	public FxRole() {

	}

	public FxRole(FleximsDynamicEntityImpl entityImpl) {
		super();
		this.entityImpl = entityImpl;
	}

	public FleximsDynamicEntityImpl getEntityImpl() {
		return entityImpl;
	}

	public void setEntityImpl(FleximsDynamicEntityImpl entityImpl) {
		this.entityImpl = entityImpl;
	}

	public String getName() {
		return entityImpl.get(PROP_NAME_PROP_NAME);
	}

	public FxRole setName(String name) {
		entityImpl.set(PROP_NAME_PROP_NAME, name);
		return this;
	}

	public List<FleximsDynamicEntityImpl> getSubRoles() {
		return entityImpl.get(PROP_NAME_PROP_SUBROLE);
	}

	List<FxRole> subroles = null;

	public List<FxRole> getWrappedSubRoles() {
		if (subroles != null) {
			return subroles;
		}
		List<FleximsDynamicEntityImpl> rs = getSubRoles();
		subroles = new ArrayList<>(rs.size());
		for (FleximsDynamicEntityImpl r : rs) {
			if (r.getClass().getSimpleName().equals(TYPE_NAME)) {
				subroles.add(new FxRole(r));
			} else {
				subroles.add(new FxUser(r));
			}
		}
		return subroles;
	}

	public long getId() {
		return entityImpl.getId();
	}

	List<FxRole> includedBys = null;

	public List<FxRole> getWrappedIncludeBys() {
		if (includedBys != null) {
			return includedBys;
		}
		List<FleximsDynamicEntityImpl> rs = entityImpl.get(PROP_NAME_PROP_INCLUEDEBY);
		if (rs == null) {
			includedBys = Collections.EMPTY_LIST;
			return includedBys;
		}
		includedBys = new ArrayList<>(rs.size());
		for (FleximsDynamicEntityImpl r : rs) {
			if (r.getClass().getSimpleName().equals(TYPE_NAME)) {
				includedBys.add(new FxRole(r));
			} else {
				includedBys.add(new FxUser(r));
			}
		}
		return includedBys;
	}

	public FxRole addSubRoles(FleximsDynamicEntityImpl... roles) {
		List<FleximsDynamicEntityImpl> list = getSubRoles();
		if (list == null) {
			list = new ArrayList<>(roles.length);
			entityImpl.set(PROP_NAME_PROP_SUBROLE, list);
		}
		for (FleximsDynamicEntityImpl role : roles) {
			list.add(role);
		}

		roles = null;
		return this;
	}

	public boolean isAllRole() {
		return getId() == RoleUtils.ALL_ROLE_ID;
	}

	public boolean isAdminRole() {
		return this.getId() == RoleUtils.ADMIN_ROLE_ID;
	}

	public boolean isDeveloperRole() {
		return this.getId() == RoleUtils.DEVELOPER_ROLE_ID;
	}

	public boolean isAnonymousRole() {
		return this.getId() == RoleUtils.ANONYMOUS_ID;
	}

	/**
	 * whether current role is 'role' or s subrole of 'role'
	 * 
	 * @param role
	 * @return
	 */
	public boolean isIncludedBy(FxRole role) {
		if (role.isAllRole()) {
			return true;
		}
		if (role.isAnonymousRole()) {
			return false;
		}
		LinkedList<FxRole> notprocessed = new LinkedList<FxRole>();
		notprocessed.add(this);
		FxRole current = null;
		while ((current = notprocessed.poll()) != null) {
			if (current.getId() == role.getId()) {
				return true;
			}
			notprocessed.addAll(current.getWrappedIncludeBys());
		}
		return false;

	}

	LinkedList<FxRole> allSubroles = null;

	public List<FxRole> getAllWrappedSubRoles() {
		if (allSubroles != null) {
			return allSubroles;
		}
		allSubroles = new LinkedList<FxRole>();
		getSubRoles();
		LinkedList<FxRole> notprocessed = new LinkedList<FxRole>();
		notprocessed.add(this);

		FxRole current = null;
		while ((current = notprocessed.poll()) != null) {
			allSubroles.add(current);
			notprocessed.addAll(current.getWrappedSubRoles());
		}
		return allSubroles;
	}

	public static FxRole wrap(FleximsDynamicEntityImpl de) {
		if (de.getClass().getSimpleName().endsWith(TYPE_NAME)) {
			return new FxRole(de);
		}
		return new FxUser(de);
	}

	public static FxRole loadByName(String name, EntityManager em) {
		javax.persistence.Query query = em.createQuery("select r from " + FxRole.TYPE_NAME + " r where r.Name = :name");
		query.setParameter("name", name);
		FleximsDynamicEntityImpl rDynamicEntityImpl = (FleximsDynamicEntityImpl) query.getSingleResult();
		return wrap(rDynamicEntityImpl);
	}

	public static boolean isACLType(String name) {
		if (name.equals(FxUser.TYPE_NAME) || name.equals(FxRole.TYPE_NAME)) {
			return true;
		}
		return false;
	}

}
