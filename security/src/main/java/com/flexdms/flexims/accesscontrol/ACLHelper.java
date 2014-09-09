package com.flexdms.flexims.accesscontrol;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.flexdms.flexims.accesscontrol.model.TypeACL;

public final class ACLHelper {
	private ACLHelper() {
	}

	public static final int INITIAL_TYPE_ACL_NUM = 50;
	public static Map<String, TypeACL> typeacls = new HashMap<>(INITIAL_TYPE_ACL_NUM);
	public static List<Action> actions;
	public static EntityManagerFactory emf;

	public static List<Action> getAvailableActions() {
		return actions;
	}

	public static Action getActionByName(String name) {
		for (Action a : getAvailableActions()) {
			if (a.getName().equalsIgnoreCase(name)) {
				return a;
			}
			for (String alias : a.getAliases()) {
				if (alias.equalsIgnoreCase(name)) {
					return a;
				}
			}
		}
		return null;
	}

	// add read lock if needed
	public static TypeACL loadTypeACL(String typeid) {
		TypeACL retAcl = typeacls.get(typeid);
		if (retAcl == null) {
			retAcl = new TypeACL();
			retAcl.setTypeid(typeid);
			typeacls.put(typeid, retAcl);
		}
		return retAcl;
	}

	public static EntityManager getSecurityEM() {
		return emf.createEntityManager();
	}

}
