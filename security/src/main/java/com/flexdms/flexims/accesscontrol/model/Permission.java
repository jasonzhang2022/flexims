package com.flexdms.flexims.accesscontrol.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.Decision;
import com.flexdms.flexims.accesscontrol.PermissionResult;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;
import com.flexdms.flexims.users.FxRole;

@Access(AccessType.PROPERTY)
@Entity
@Table(name = "typepermissions")
@TableGenerator(name = "typepermsid", table = "seqs", pkColumnName = "name", valueColumnName = "seq", pkColumnValue = "accesscontrol.Permission.seq", allocationSize = MetaSourceBuilder.SEQ_ID_BATCH)
@DiscriminatorColumn(name = "ptype")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Permission implements Serializable, PermissionI {

	
	private static final long serialVersionUID = 1L;

	private long id;
	private int order;
	List<Action> actions;
	Decision decision = Decision.Allow;

	@Transient
	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	@Column(name = "actions")
	private String getActionString() {
		if (this.actions == null || this.actions.isEmpty()) {
			return null;
		}
		return StringUtils.join(actions, ',');
	}

	@SuppressWarnings("unused")
	private void setActionString(String action) {
		if (actions == null) {
			actions = new ArrayList<Action>(INITIAL_PERMISSION_NUM);
		}
		actions.clear();
		if (action == null) {
			return;
		}
		String[] as = action.split(",");
		for (String name : as) {
			actions.add(ACLHelper.getActionByName(name));
		}
	}

	@Enumerated(EnumType.STRING)
	public Decision getDecision() {
		return decision;
	}

	public void setDecision(Decision decision) {
		this.decision = decision;
	}

	// --------------Change tracking does not work properly
	// So we make permission as an entity and give it an id
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "typepermsid")
	private long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(long id) {
		this.id = id;
	}

	@Column(name = "aceorder")
	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public PermissionResult checkPermission(Action action, FxRole role, FleximsDynamicEntityImpl i) {

		if (!action.inAction(getActions())) {
			return PermissionResult.Undecided;
		}
		if (!isApplyForUser(role, i)) {
			return PermissionResult.Undecided;
		}
		if (decision == Decision.Allow) {
			return PermissionResult.Allow;
		} else {
			return PermissionResult.Deny;
		}
	}

	/**
	 * Whether this permission apply for this user
	 * 
	 * @return
	 */
	@Transient
	public abstract boolean isApplyForUser(FxRole role, FleximsDynamicEntityImpl i);

	// ------------------------unique direction one-toMany does not work. this
	// is
	// why we have this. //to which type this permission is attached
	TypeACL typeACL;

	@ManyToOne
	@JoinColumn(name = "typeid")
	public TypeACL getTypeACL() {
		return typeACL;
	}

	public void setTypeACL(TypeACL typeACL) {
		this.typeACL = typeACL;
	}

}
