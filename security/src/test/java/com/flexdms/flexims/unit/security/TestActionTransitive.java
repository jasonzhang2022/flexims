package com.flexdms.flexims.unit.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.action.AllAction;
import com.flexdms.flexims.accesscontrol.action.CreateAction;
import com.flexdms.flexims.accesscontrol.action.DeleteAction;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.GrantAction;
import com.flexdms.flexims.accesscontrol.action.QueryAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.accesscontrol.action.WatcherAction;

public class TestActionTransitive {

	@BeforeClass
	public static void setupActions() {
		ACLHelper.actions = Arrays.asList(new EditAction(), new WatcherAction(), new ReadAction(), new QueryAction(), new AllAction(),
				new CreateAction(), new DeleteAction(), new GrantAction());
	}
	@Test
	public void testActionTransitive() {

		AllAction allAction = new AllAction();
		assertTrue(allAction.contain(new CreateAction()));
		assertTrue(allAction.contain(new DeleteAction()));
		assertTrue(allAction.contain(new EditAction()));
		assertTrue(allAction.contain(new GrantAction()));
		assertTrue(allAction.contain(new QueryAction()));
		assertTrue(allAction.contain(new ReadAction()));
		assertTrue(allAction.contain(new WatcherAction()));

		CreateAction createAction = new CreateAction();
		assertFalse(createAction.contain(allAction));

		EditAction editAction = new EditAction();
		assertTrue(editAction.contain(new ReadAction()));
		assertTrue(editAction.contain(new QueryAction()));
		assertFalse(editAction.contain(new CreateAction()));

		ReadAction readAction = new ReadAction();
		assertTrue(readAction.contain(new QueryAction()));

	}

}
