package com.flexdms.flexims.accesscontrol.action;

import com.flexdms.flexims.accesscontrol.Action;

public class AllAction extends Action {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "All";
	public static final String DESCRIPTION = "All Permissions";

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
		return true;
	}

	public String toString() {
		return NAME;
	}

}
