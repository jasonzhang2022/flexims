package com.flexdms.flexims.accesscontrol.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name = "typeacl")
@Access(AccessType.PROPERTY)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TypeACL implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement
	String typename;

	/**
	 * Logic parent of this type, used for permission check purpose. For
	 * example, Order is a logic parent of orderItem.
	 */
	@XmlElement
	List<String> aclParentTypes = new ArrayList<String>(1);

	/**
	 * Permission associated with specific Role.
	 */
	@XmlElement
	List<RolePermission> rolePermissions = new ArrayList<RolePermission>(2);

	/**
	 * Permission associated with property value of Instance
	 */
	@XmlElement
	List<PropertyPermission> propPermissions = new ArrayList<PropertyPermission>(3);

	@Transient
	public List<String> getAclParentTypes() {
		return aclParentTypes;
	}

	@Transient
	public List<RolePermission> getRolePermissions() {
		return rolePermissions;
	}

	@Transient
	public List<PropertyPermission> getPropPermissions() {
		return propPermissions;
	}

	public void setAclParentTypes(List<String> aclParentTypes) {
		this.aclParentTypes = aclParentTypes;
	}

	public void setRolePermissions(List<RolePermission> rolePermissions) {
		this.rolePermissions = rolePermissions;
	}

	public void setPropPermissions(List<PropertyPermission> propPermissions) {
		this.propPermissions = propPermissions;
	}

	@Id
	public String getTypeid() {
		return typename;
	}

	public void setTypeid(String typeid) {
		this.typename = typeid;
	}

	// ------------------------methods below used by JPA
	// unique directon does not work.
	// we use bidirectional relatioship
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "typeACL")
	@OrderBy(value = "order ASC")
	private List<Permission> getPermissions() {
		List<Permission> perms = new ArrayList<Permission>(5);
		if (rolePermissions != null && !rolePermissions.isEmpty()) {
			int count = 0;
			for (Permission perm : rolePermissions) {
				perm.setOrder(count++);
				perm.setTypeACL(this);
				perms.add(perm);
			}
		}
		if (propPermissions != null && !propPermissions.isEmpty()) {
			int count = 0;
			for (Permission perm : propPermissions) {
				perm.setOrder(count++);
				perm.setTypeACL(this);
				perms.add(perm);
			}
		}
		return perms;
	}

	@SuppressWarnings("unused")
	private void setPermissions(List<Permission> perms) {
		rolePermissions.clear();
		propPermissions.clear();
		if (perms == null || perms.isEmpty()) {
			return;
		}
		for (Permission perm : perms) {

			if (perm instanceof RolePermission) {
				perm.setTypeACL(this);
				rolePermissions.add((RolePermission) perm);
			} else if (perm instanceof PropertyPermission) {
				propPermissions.add((PropertyPermission) perm);
			} else {
				assert false;
			}
		}

	}

	@SuppressWarnings("unused")
	private String getPTypes() {
		if (aclParentTypes == null || aclParentTypes.isEmpty()) {
			return null;
		}
		return StringUtils.join(aclParentTypes, ",");
	}

	@SuppressWarnings("unused")
	private void setPTypes(String ids) {
		if (ids == null) {
			this.aclParentTypes = new ArrayList<String>(1);
			return;
		}
		this.aclParentTypes = new ArrayList<String>(3);
		for (String id : ids.split(",")) {
			this.aclParentTypes.add(id);
		}
	}

}
