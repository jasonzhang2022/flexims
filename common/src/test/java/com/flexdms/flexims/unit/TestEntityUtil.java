package com.flexdms.flexims.unit;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.helper.ByteArray;
import com.flexdms.flexims.jpa.helper.NameValueList;

public class TestEntityUtil {
	public static void setIDandVersion(FleximsDynamicEntityImpl de) {
		de.setId(3);
		de.setVersion(new Timestamp(new Date().getTime()));
	}

	public static void setBasicType(FleximsDynamicEntityImpl de) {

		de.set("shortstring", "short");
		de.set("mediumstring", "this is a medium string");
		de.set("longstring", "this is a long string");

		// number
		de.set("propint", 5);
		de.set("proplong", 5l);
		de.set("propfloat", 5.0f);
		de.set("propdouble", 5.9d);
		de.set("propcurrency", new BigDecimal(79.6545));

		// boolean
		de.set("propboolean", true);

		de.set("propdate", Calendar.getInstance());
		de.set("proptimestamp", Calendar.getInstance());
		de.set("proptime", Calendar.getInstance());
	}

	public static <T> List<T> fillList(T... vs) {

		return Arrays.asList(vs);

	}

	public static void setCollection1(FleximsDynamicEntityImpl de) {

		de.set("shortstring", fillList("short1", "shor][]t2"));

		// number
		de.set("propint", fillList(5, 6));
		de.set("proplong", fillList(5l, 6l));
		de.set("propfloat", fillList(5.0, 6.0));
		de.set("propdouble", fillList(5.0d, 6.0d));
		de.set("propcurrency", fillList(new BigDecimal(79.6545), new BigDecimal(78.32)));

		de.set("propdate", fillList(Calendar.getInstance(), Calendar.getInstance()));
		de.set("proptimestamp", fillList(Calendar.getInstance(), Calendar.getInstance()));
		de.set("proptime", fillList(Calendar.getInstance(), Calendar.getInstance()));

		de.set("propemail", fillList("j@example.com", "z@example.com"));
	}

	public static void setSpecialtype(FleximsDynamicEntityImpl de) {

		Map<String, String> map = new HashMap<>(2);
		map.put("first name", "jason");
		map.put("last name", "zhang");
		de.set("propobject", NameValueList.fromMap(map));

		// collection of dates
		Set<Calendar> dates = new HashSet<>();
		;
		dates.add(Calendar.getInstance());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		dates.add(calendar);
		de.set("propdates", dates);

		de.set("propbyte", new ByteArray("this is a test".getBytes()));
	}

	public static FleximsDynamicEntityImpl setEmbeded(FleximsDynamicEntityImpl de, boolean single) {
		de.set("streetAddress", "Street Address");
		if (single) {
			List<String> mStrings = new ArrayList<>();
			mStrings.add("value1");
			mStrings.add("value,");

			de.set("mstr", mStrings);
			List<Integer> mints = new ArrayList<>();
			mints.add(6);
			mints.add(7);
			mints.add(8);

			de.set("mint", mints);
		}

		return de;

	}

	public static void setEmbedmain(FleximsDynamicEntityImpl de, EntityManager em) {

		de.set("fname", "simple");
		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "Embed1");
		FleximsDynamicEntityImpl embed2 = JpaHelper.createNewEntity(em, "Embed2");
		FleximsDynamicEntityImpl embed3 = JpaHelper.createNewEntity(em, "Embed2");
		setEmbeded(embed2, false);
		setEmbeded(embed3, false);
		de.set("singleembed", setEmbeded(embed1, true));
		de.set("multiembed", fillList(embed2, embed3));
	}

	public static void deleteStudent(EntityManager em) throws SQLException {

		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM MSTUDENT_MCOURSE");
		connection.createStatement().execute("DELETE FROM MSTUDENT_MONEMANY");
		connection.createStatement().execute("DELETE FROM MSTUDENT_PROPINT");

		connection.createStatement().execute("DELETE FROM MCOURSE");
		connection.createStatement().execute("DELETE FROM MONEMANY");
		connection.createStatement().execute("DELETE FROM MSTUDENT");

		connection.createStatement().execute("DELETE FROM MDOOMROOM");
		connection.createStatement().execute("DELETE FROM MDOOMBUILD");
	}

	public static FleximsDynamicEntityImpl createDoomBuild(EntityManager em) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl doombuild = JpaHelper.createNewEntity(em, "Mdoombuild");
		doombuild.set("name", "Porter");
		em.persist(doombuild);
		em.flush();

		FleximsDynamicEntityImpl doomroom;
		for (int i = 0; i < 5; i++) {
			doomroom = JpaHelper.createNewEntity(em, "Mdoomroom");
			doomroom.set("number", 100 + i);
			doomroom.set("name", "name" + i);
			doomroom.set("doombuild", doombuild);
			em.persist(doomroom);
		}
		tx.commit();
		return em.find(doombuild.getClass(), doombuild.getId());
	}

	public static FleximsDynamicEntityImpl createStudentOneManys(EntityManager em) {
		FleximsDynamicEntityImpl OneMany1 = JpaHelper.createNewEntity(em, "MOneMany");
		OneMany1.set("name", "OneMany1");

		FleximsDynamicEntityImpl OneMany2 = JpaHelper.createNewEntity(em, "MOneMany");
		OneMany2.set("name", "OneMany2");

		FleximsDynamicEntityImpl student = JpaHelper.createNewEntity(em, "Mstudent");

		List<FleximsDynamicEntityImpl> oneManysList = new ArrayList<>();
		oneManysList.add(OneMany1);
		oneManysList.add(OneMany2);
		student.set("OneManys", oneManysList);
		return student;
	}

	public static FleximsDynamicEntityImpl createAndPersisteDemoRelationObject(EntityManager em) {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl OneMany1 = JpaHelper.createNewEntity(em, "MOneMany");
		OneMany1.set("name", "OneMany1");
		em.persist(OneMany1);

		FleximsDynamicEntityImpl OneMany2 = JpaHelper.createNewEntity(em, "MOneMany");
		OneMany2.set("name", "OneMany2");
		em.persist(OneMany2);

		em.flush();

		FleximsDynamicEntityImpl doombuild = JpaHelper.createNewEntity(em, "Mdoombuild");
		doombuild.set("name", "Porter");
		em.persist(doombuild);
		em.flush();

		FleximsDynamicEntityImpl doomroom = JpaHelper.createNewEntity(em, "Mdoomroom");
		doomroom.set("number", 312);
		doomroom.set("doombuild", doombuild);
		em.persist(doomroom);
		// em.flush();

		FleximsDynamicEntityImpl course1 = JpaHelper.createNewEntity(em, "Mcourse");
		course1.set("name", "CS1");
		em.persist(course1);

		FleximsDynamicEntityImpl course2 = JpaHelper.createNewEntity(em, "Mcourse");
		course2.set("name", "CS2");
		em.persist(course2);

		FleximsDynamicEntityImpl student = JpaHelper.createNewEntity(em, "Mstudent");
		student.set("doombuild", doombuild);
		student.set("doomroom", doomroom);
		List<FleximsDynamicEntityImpl> oneManysList = new ArrayList<>();
		oneManysList.add(OneMany1);
		oneManysList.add(OneMany2);
		student.set("OneManys", oneManysList);

		List<FleximsDynamicEntityImpl> courses = new ArrayList<>();
		courses.add(course2);
		courses.add(course1);
		student.set("Courses", courses);
		course1.set("Students", Arrays.asList(student));
		course2.set("Students", Arrays.asList(student));
		em.persist(student);

		tx.commit();

		return student;
	}
}
