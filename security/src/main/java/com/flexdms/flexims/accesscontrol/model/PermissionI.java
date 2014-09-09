package com.flexdms.flexims.accesscontrol.model;

import java.util.List;

import com.flexdms.flexims.accesscontrol.Action;
import com.flexdms.flexims.accesscontrol.Decision;

public interface PermissionI {
	int INITIAL_PERMISSION_NUM = 3;

	List<Action> getActions();

	Decision getDecision();
}
