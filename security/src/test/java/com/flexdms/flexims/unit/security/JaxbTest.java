package com.flexdms.flexims.unit.security;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.BeforeClass;
import org.junit.Test;

import com.flexdms.flexims.accesscontrol.ACLHelper;
import com.flexdms.flexims.accesscontrol.Decision;
import com.flexdms.flexims.accesscontrol.action.AllAction;
import com.flexdms.flexims.accesscontrol.action.CreateAction;
import com.flexdms.flexims.accesscontrol.action.DeleteAction;
import com.flexdms.flexims.accesscontrol.action.EditAction;
import com.flexdms.flexims.accesscontrol.action.GrantAction;
import com.flexdms.flexims.accesscontrol.action.QueryAction;
import com.flexdms.flexims.accesscontrol.action.ReadAction;
import com.flexdms.flexims.accesscontrol.action.WatcherAction;
import com.flexdms.flexims.accesscontrol.model.InstanceACE;
import com.flexdms.flexims.accesscontrol.model.PropertyPermission;
import com.flexdms.flexims.accesscontrol.model.RolePermission;
import com.flexdms.flexims.accesscontrol.model.TypeACL;
import com.flexdms.flexims.accesscontrol.rs.InstanceACES;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;

public class JaxbTest {

	@BeforeClass
	public static void setupActions() {
		ACLHelper.actions = Arrays.asList(new EditAction(), new WatcherAction(), new ReadAction(), new QueryAction(), new AllAction(),
				new CreateAction(), new DeleteAction(), new GrantAction());
	}

	public static TypeACL createTypeACL() {
		TypeACL typeACL = new TypeACL();
		typeACL.setTypeid("FxUser");

		// role permission
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(1l);
		permission.setActions(Arrays.asList(new EditAction(), new WatcherAction()));
		// permission.setTypeACL(typeACL);

		// role permission 2
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(2l);
		permission1.setActions(Arrays.asList(new EditAction(), new WatcherAction()));
		// permission1.setTypeACL(typeACL);

		typeACL.setRolePermissions(Arrays.asList(permission, permission1));

		// parent
		typeACL.setAclParentTypes(Arrays.asList("doombuild", "parent2"));

		// property permission
		PropertyPermission ppermission = new PropertyPermission();
		ppermission.setPropName("IncludedBy");
		ppermission.setDecision(Decision.Allow);
		ppermission.setActions(Arrays.asList(new EditAction(), new WatcherAction()));
		typeACL.setPropPermissions(Arrays.asList(ppermission));
		return typeACL;
	}

	@Test
	public void TestTypeAClXml() throws JAXBException {

		TypeACL typeACL = createTypeACL();
		JAXBContext ctx = JaxbHelper.createMoxyJaxbContext(TypeACL.class);
		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(typeACL, stringWriter);

		String s = stringWriter.toString();
		System.out.println(s);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.unmarshal(new StringReader(s));
	}

	@Test
	public void TestTypeAClJson() throws JAXBException {

		TypeACL typeACL = createTypeACL();
		JAXBContext ctx = JaxbHelper.createMoxyJaxbContext(TypeACL.class);
		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.marshal(typeACL, stringWriter);

		String s = stringWriter.toString();
		System.out.println(s);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		unmarshaller.unmarshal(new StringReader(s));
	}

	public static InstanceACES createInstACL() {

		InstanceACES aces = new InstanceACES();

		InstanceACE ace = new InstanceACE("Test", 52l);

		// role permission
		RolePermission permission = new RolePermission();
		permission.setDecision(Decision.Allow);
		permission.setRoleid(1l);
		permission.setActions(Arrays.asList(new EditAction(), new WatcherAction()));
		ace.setRolePermission(permission);

		InstanceACE ace1 = new InstanceACE("Test", 52l);
		// role permission 2
		RolePermission permission1 = new RolePermission();
		permission1.setDecision(Decision.Deny);
		permission1.setRoleid(2l);
		permission1.setActions(Arrays.asList(new EditAction(), new WatcherAction()));
		ace1.setRolePermission(permission1);

		aces.setAces(Arrays.asList(ace, ace1));
		return aces;
	}

	@Test
	public void TestInstAClXml() throws JAXBException {

		InstanceACES aces = createInstACL();
		JAXBContext ctx = JaxbHelper.createMoxyJaxbContext(InstanceACES.class);
		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(aces, stringWriter);

		String s = stringWriter.toString();
		System.out.println(s);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.unmarshal(new StringReader(s));
	}

	@Test
	public void TestInstAClJson() throws JAXBException {

		InstanceACES aces = createInstACL();
		JAXBContext ctx = JaxbHelper.createMoxyJaxbContext(InstanceACES.class);
		StringWriter stringWriter = new StringWriter();
		Marshaller marshaller = ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.marshal(aces, stringWriter);

		String s = stringWriter.toString();
		System.out.println(s);

		Unmarshaller unmarshaller = ctx.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		unmarshaller.unmarshal(new StringReader(s));
	}
}
