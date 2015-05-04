package com.flexdms.flexims.users;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.flexdms.flexims.AppInitializer;
import com.flexdms.flexims.jaxb.moxy.JaxbHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.rsutil.Entities;
@Dependent
public class RoleInitializer {

	public static final Logger LOGGER = Logger.getLogger(RoleInitializer.class.getCanonicalName());

	@Inject
	JaxbHelper jaxbHelper;

	public void init(@Observes AppInitializer.AppInitalizeContext ctx) throws SQLException, IOException {

		EntityManager em = ctx.em;
		//load this one more time incase it is not executed yet.
		insertRoles(em, jaxbHelper);
		loadPredefinedRole(em);
	}

	public static void insertRoles(EntityManager em, JaxbHelper jaxbHelper) {
		try {

			em.getTransaction().begin();
			Entities entities = (Entities) jaxbHelper.fromXml(
					new InputStreamReader(RoleInitializer.class.getClassLoader().getResourceAsStream("META-INF/0predefined_roles_data.fxext.xml")), em);
			for (Object obj : entities.getItems()) {
				em.persist(obj);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			// ignore
			try {
				em.getTransaction().rollback();
			} catch (Exception e1) {

			}
		}
	}

	public static void loadPredefinedRole(EntityManager em) {
		String qString = "select r from FxRole r where r.id<1000";
		Query query = em.createQuery(qString);
		@SuppressWarnings("rawtypes")
		List list = query.getResultList();
		LOGGER.info("Loaded " + list.size() + " predefined roles");
		for (Object obj : list) {

			FxRole role = new FxRole((FleximsDynamicEntityImpl) obj);

			if (role.getId() == RoleUtils.ADMIN_ROLE_ID) {
				RoleUtils.adminRole = role;
			} else if (role.getId() == RoleUtils.ALL_ROLE_ID) {
				RoleUtils.allRole = role;
			} else if (role.getId() == RoleUtils.DEVELOPER_ROLE_ID) {
				RoleUtils.developerRole = role;
			} else if (role.getId() == RoleUtils.ANONYMOUS_ID) {
				RoleUtils.anonymousRole = role;
			}

		}
	}
}
