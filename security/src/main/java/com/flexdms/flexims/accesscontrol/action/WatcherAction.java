package com.flexdms.flexims.accesscontrol.action;

import javax.enterprise.context.ApplicationScoped;

import com.flexdms.flexims.accesscontrol.Action;
@ApplicationScoped
public class WatcherAction extends Action {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "Watch";
	public static final String DESCRIPTION = "Watch";
	public static final String[] ALIASES = {};

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

	public String toString() {
		return NAME;
	}
}
