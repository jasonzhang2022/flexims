package com.flexdms.flexims.accesscontrol.model;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;

@NamedQueries(value = {
		@NamedQuery(name = InstanceACE.ACLQNAME, query = "Select b from  InstanceACE b where b.typename=:typeid and b.instanceid=:instanceid order by b.order"),
		@NamedQuery(name = InstanceACE.ACLDELETE, query = "delete from  InstanceACE b where b.typename=:typeid and b.instanceid=:instanceid"),
		@NamedQuery(name = InstanceACE.TYPEACLDELETE, query = "delete from  InstanceACE b where b.typename=:typeid") })
@Entity
@Table(name = "instanceacl")
@Access(AccessType.FIELD)
@TableGenerator(name = "InstanceACLIDs", table = "seqs", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "instanceACL.seq", allocationSize = MetaSourceBuilder.SEQ_ID_BATCH)
public class InstanceACE implements Serializable {

	public static final String ACLQNAME = "FindInstanceACEs";
	public static final String ACLDELETE = "DeleteInstanceACEs";
	public static final String TYPEACLDELETE = "DeleteTypeACEs";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "InstanceACLIDs")
	long id;

	String typename;
	long instanceid;
	@Column(name = "aceorder")
	long order;

	public long getOrder() {
		return order;
	}

	public void setOrder(long order) {
		this.order = order;
	}

	/**
	 * Type is Permission instead of RolePermission. This is a requirement from
	 * EclipseLink
	 */
	private InstanceRolePermission rolePermission;


	public void setRolePermission(RolePermission rp) {
		if (rp == null) {
			rolePermission = null;
			return;
		}
		if (this.rolePermission != null) {
			rolePermission.fromRolePermission(rp);
		} else {
			this.rolePermission = new InstanceRolePermission(rp);
		}
	}

	// need this to modify value by JSF
	public InstanceRolePermission getInstanceRolePermission() {
		if (rolePermission == null) {
			rolePermission = new InstanceRolePermission();
		}
		return rolePermission;
	}

	public String getTypeid() {
		return typename;
	}

	public void setTypeid(String typeid) {
		this.typename = typeid;
	}

	public long getInstanceid() {
		return instanceid;
	}

	public void setInstanceid(long instanceid) {
		this.instanceid = instanceid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public InstanceACE() {

	}

	public InstanceACE(String typeid, long instanceid) {
		super();
		this.typename = typeid;
		this.instanceid = instanceid;
	}

}
