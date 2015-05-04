package com.flexdms.flexims.accesscontrol.action;

import javax.enterprise.context.ApplicationScoped;

import com.flexdms.flexims.accesscontrol.Action;
@ApplicationScoped
public class QueryAction extends Action {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "Query";
	public static final String DESCRIPTION = "Run Query(For Query)";

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
		return false;
	}

	public String toString() {
		return NAME;
	}

}
