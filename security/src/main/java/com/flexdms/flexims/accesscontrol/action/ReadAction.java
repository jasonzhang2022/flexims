package com.flexdms.flexims.accesscontrol.action;

import com.flexdms.flexims.accesscontrol.Action;

public class ReadAction extends Action {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Read";
	public static final String DESCRIPTION = "Access(Read)";
	public static final String[] ALIASES = { "Access", "View" };

	@Override
	public String[] getAliases() {
		return ALIASES;
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public boolean contain(Action action) {
		return action.getName().equals(QueryAction.NAME);
	}

	public String toString() {
		return NAME;
	}
}
