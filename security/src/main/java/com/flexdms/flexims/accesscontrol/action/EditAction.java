package com.flexdms.flexims.accesscontrol.action;

import javax.enterprise.context.ApplicationScoped;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Action;
@ApplicationScoped
public class EditAction extends Action {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Edit";
	public static final String DESCRIPTION = "Edit";
	public static final String[] ALIASES = { "Modify", "Update" };

	@Override
	public String[] getAliases() {
		return ALIASES;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean contain(Action action) {
		return action.getName().equals(ReadAction.NAME) || ACLHelper.getActionByName(ReadAction.NAME).contain(action);
	}

	public String toString() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

}