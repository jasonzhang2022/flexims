package com.flexdms.flexims.urlctrl.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.urlctrl.UrlControlFilter;
import com.flexdms.flexims.users.RoleUtils;

public class TestUrlCtrlFilter {

	@ClassRule
	public static TestRule rule = RuleChain.outerRule(new CDIContainerRule()).around(new JPA_JAXB_EmbeddedDerby_Rule()).around(new AppInitializerRule());

	@Rule
	public EntityManagerRule emRule = new EntityManagerRule();
	JaxbHelper helper;
	EntityManager em;

	@Before
	public void setup() throws JAXBException {
		em = emRule.em;
		helper = App.getInstance(JaxbHelper.class);
	}

	@Test
	public  void testUrlCtrl() {
		assertFalse(UrlControlFilter.forbidden("/rs/type/schema", RoleUtils.getAdminRole()));
		assertTrue(UrlControlFilter.forbidden("/rs/type/schema", RoleUtils.getDeveloperRole()));
		
		assertFalse(UrlControlFilter.forbidden("not controlled", RoleUtils.getAdminRole()));
		assertFalse(UrlControlFilter.forbidden("not controlled", RoleUtils.getDeveloperRole()));
	}
}
