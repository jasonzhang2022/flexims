package com.flexdms.flexims.users.unit;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.derby.iapi.sql.dictionary.PasswordHasher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.util.ByteSource;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.AppInitializerRule;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.TestOXMSetup;
import com.flexdms.flexims.unit.Util;
import com.flexdms.flexims.users.FxRole;
import com.flexdms.flexims.users.FxUser;
import com.flexdms.flexims.users.FxUserSettings;

/**
 * Not a actual test. but a utility to add a bunch of user to system.
 * @author jason.zhang
 *
 */
public final class DataUtil {

	private DataUtil() {
	}
	
	public static void hashPasswordStatic(FxUser user) {
		String hashedString = hashPassword(user.getPassword(), user.getId());
		user.setPassword(hashedString);
	
	}
	
	public static String hashPassword(String password, long userid) {
		Md5Hash hash = new Md5Hash(password, ByteSource.Util.bytes(String.valueOf(userid)), 10);
		String hashedString = hash.toBase64();
		return hashedString;
	}

	/*
	 * testrole0: test01, test02, test03, test04, test05
	 * 
	 * testrole1: test11,test12, test13, test14, test15
	 * 
	 * testrole2: test21, test22, test23,test24, test25
	 * 
	 * testrole3: test31,test32,test33,test34,test35
	 * 
	 * testrole4: test41,test42,test43,test44,test45
	 * 
	 * testrole5: testrole4,testrole4,
	 * 
	 * test61: memeber of Admin Role. //skip this. This is added to deployment logic
	 */
	public static void createTestUsers(EntityManager em) {
		em.getTransaction().begin();
		em.createQuery("delete from "+FxUserSettings.TYPE_NAME).executeUpdate();
		em.createQuery("delete from FxRole r where r.id <= 105 and r.id != 61").executeUpdate();
		em.getTransaction().commit();
		
		
		em.getTransaction().begin();
		FleximsDynamicEntityImpl role3 = null;
		FleximsDynamicEntityImpl role4 = null;
		for (int j = 0; j < 5; j++) {
			FleximsDynamicEntityImpl rde = JpaHelper.createNewEntity(em, "FxRole");
			FxRole role = new FxRole(rde);
			role.setName("testrole" + j);

			for (int i = 1; i <= 5; i++) {
				FxUser user = FxUser.createUser(em);
				user.setEmail("test" + j + i + "@email.com");
				user.setName("test" + j + i);
				user.setPassword("123456");
				user.getEntityImpl().setId(j * 10 + i);
				user.getUserSettings().getEntityImpl().setId(j * 10 + i);

				hashPasswordStatic(user);
				em.persist(user.getEntityImpl());
				em.persist(user.getUserSettings().getEntityImpl());

				role.addSubRoles(user.getEntityImpl());

			}

			rde.setId(100 + j);
			em.persist(rde);
			if (j == 3) {
				role3 = rde;
			} else if (j == 4) {
				role4 = rde;
			}
			em.flush();
		}

		// admin user 61
		/*FxUser user = FxUser.createUser(em);
		user.setEmail("testadmin@email.com");
		user.setName("testadmin");
		user.setPassword("123456");
		user.getEntityImpl().setId(61l);
		user.getUserSettings().getEntityImpl().setId(61l);
		FxRole.adminRole.addSubRoles(user.getEntityImpl());

		hashPasswordStatic(user);
		em.persist(user.getEntityImpl());
		em.persist(user.getUserSettings().getEntityImpl());
		em.merge(FxRole.adminRole.getEntityImpl());*/

		// a role with two sub grouping roles.
		FleximsDynamicEntityImpl rde = JpaHelper.createNewEntity(em, "FxRole");
		FxRole role = new FxRole(rde);
		role.setName("testrole" + 5);
		role.addSubRoles(role3, role4);
		rde.setId(105l);
		em.persist(rde);
		em.getTransaction().commit();
	}
	
}
