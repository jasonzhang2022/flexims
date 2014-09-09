package com.flexdms.flexims.it;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.junit.Test;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.TestEntityUtil;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;

public class DataUtil {

	/**
	 * 500 entities: each entity has one hour gap.
	 * 
	 * @param em
	 */
	public static void prepareBasictype(EntityManager em) {
		String typename = "Basictype";
		// delete all to void constraint error
		Calendar calendar = Calendar.getInstance();
		// one month before
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		em.getTransaction().begin();
		for (int i = 0; i < 500; i++) {
			FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, typename);
			de.set("shortstring", "jason" + i);
			de.set("propint", i);
			de.set("propcurrency", new BigDecimal(i));
			// one entity in each hours.
			calendar.add(Calendar.HOUR_OF_DAY, 1);
			de.set("propdate", calendar.clone());
			de.set("propboolean", true);
			de.set("proptimestamp", Calendar.getInstance());
			de.set("proptime", createTime(calendar, i));
			de.set("propemail", "x" + i + "@test.com");
			de.set("propurl", "http://www.test.com");

			em.persist(de);
		}
		em.getTransaction().commit();
	}

	public static void prepareCollection1type(EntityManager em) {
		String typename = "Collection1";
		// delete all to void constraint error
		Calendar calendar = Calendar.getInstance();
		// one month before
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		em.getTransaction().begin();
		for (int i = 0; i < 500; i++) {
			FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, typename);
			de.set("shortstring", Arrays.asList("jason" + i, "jason" + (i + 1)));
			de.set("propint", Arrays.asList(i, i + 1));
			de.set("propcurrency", Arrays.asList(new BigDecimal(i), new BigDecimal(i + 1)));

			calendar.add(Calendar.HOUR_OF_DAY, 1);
			Calendar c1 = (Calendar) calendar.clone();
			Calendar c2 = (Calendar) c1.clone();
			c2.add(Calendar.HOUR_OF_DAY, 1);
			de.set("propdate", Arrays.asList(c1, c2));
			de.set("proptime", Arrays.asList(createTime(calendar, i), createTime(calendar, i + 1)));
			de.set("propurl", Arrays.asList("http://www.test.com"));

			em.persist(de);
		}
		em.getTransaction().commit();
	}

	private static void deleteStudent(EntityManager em) throws SQLException {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		QueryUtil.deleteData("Mstudent", em);
		QueryUtil.deleteData("Mcourse", em);
		QueryUtil.deleteData("Mdoomroom", em);
		QueryUtil.deleteData("Mdoombuild", em);
		tx.commit();
	}

	public static void setOneManyTrace(EntityManager em) throws SQLException {
		FleximsDynamicEntityImpl entity;
		deleteStudent(em);
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		/*
		 * 
		 * Build: 601(main11) student(1,jason11), (2, jason15) (3, jason13)
		 * 602:main12 (4,jason21)(5, jason22) 603 main21 (6, jason32) (7,
		 * jason42) course 1: many11, many12 2: many12, many21 3: many21, many22
		 * 4: many21, many22 5: many21, many22 6:many41, many 42, 7: many43,
		 * many 44
		 */

		String coursename[] = { "many11", "many12", "many21", "man22", "many", "many41", "many42", "many43", "many44" };

		List<FleximsDynamicEntityImpl> courses = new ArrayList<>(5);
		for (int i = 501; i <= 509; i++) {
			entity = JpaHelper.createNewEntity(em, "Mcourse");
			courses.add(entity);
			entity.set("name", coursename[i - 501]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		List<FleximsDynamicEntityImpl> builds = new ArrayList<>(5);
		String buildnames[] = { "main11", "main12", "main21" };
		for (int i = 601; i <= 603; i++) {
			entity = JpaHelper.createNewEntity(em, "Mdoombuild");
			builds.add(entity);
			entity.set("name", buildnames[i - 601]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		String studentnames[] = { "jason11", "jason15", "jason13", "jason21", "jason22", "jason32", "jason42" };
		int buildidx[] = { 0, 0, 0, 1, 1, 2, 2 };
		int coursesidxs[][] = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 2, 3 }, { 2, 3 }, { 5, 6 }, { 7, 8 } };
		int propint[][] = { {}, {}, {}, { 38, 39 }, { 36, 37 }, { 32, 33 }, { 34, 35 } };
		for (int i = 701; i <= 707; i++) {
			entity = JpaHelper.createNewEntity(em, "Mstudent");

			List<FleximsDynamicEntityImpl> css = new ArrayList<>(2);
			for (int j = 0; j < coursesidxs[i - 701].length; j++) {
				css.add(courses.get(coursesidxs[i - 701][j]));
			}

			entity.set("Courses", css);
			entity.set("Name", studentnames[i - 701]);
			int buildIdx = buildidx[i - 701];
			if (buildIdx != -1) {
				entity.set("doombuild", builds.get(buildIdx));
			}
			entity.setId(Long.valueOf(i));
			entity.set("number", i - 700); // student number is 0,
											// 1,2,3,4,5,6,7,

			List<Integer> pints = new ArrayList<>(2);
			for (int j = 0; j < propint[i - 701].length; j++) {
				pints.add(Integer.valueOf(propint[i - 701][j]));
			}
			entity.set("propint", pints);
			em.persist(entity);
		}
		tx.commit();
	}

	public static void setupEmbedmain(EntityManager em) {
		for (int i = 0; i < 21; i++) {
			FleximsDynamicEntityImpl main = JpaHelper.createNewEntity(em, "Embedmain");
			main.set("fname", "main" + i);
			FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "Embed1");
			embed1.set("streetAddress", "singlestr" + i);
			embed1.set("mstr", Arrays.asList("mstr" + i, "mstr" + (i + 1)));
			embed1.set("mint", Arrays.asList(i, i + 1));
			main.set("singleembed", embed1);

			FleximsDynamicEntityImpl embed2 = JpaHelper.createNewEntity(em, "Embed2");
			embed2.set("streetAddress", "mstr" + i);

			FleximsDynamicEntityImpl embed3 = JpaHelper.createNewEntity(em, "Embed2");
			embed3.set("streetAddress", "mstr" + (i + 1));
			main.set("multiembed", Arrays.asList(embed2, embed3));
			em.persist(main);
		}

	}

	public static Calendar createTime(Calendar calendar, int i) {
		Calendar c = (Calendar) calendar.clone();
		c.set(1970, 0, 1);
		c.set(Calendar.HOUR_OF_DAY, i % 24);
		c.set(Calendar.MINUTE, 30);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;

	}

	/**
	 * Build Porter with 200 rooms Build Kmart with 200 room
	 * 
	 * College Arts with 200 Courses College Engineer witj 200 courses
	 * 
	 * 200 OneManys
	 * 
	 * @throws SQLException
	 */
	@Test
	public static void studentForRelationEditor(EntityManager em) throws SQLException {
		// delete all to void constraint error
		deleteStudent(em);

		em.getTransaction().begin();

		// build Porter
		FleximsDynamicEntityImpl doombuild = JpaHelper.createNewEntity(em, "Mdoombuild");
		doombuild.set("name", "Porter");
		doombuild.setId(1);
		em.persist(doombuild);
		em.flush();

		// rooms
		FleximsDynamicEntityImpl doomroom;
		for (int i = 0; i < 200; i++) {
			doomroom = JpaHelper.createNewEntity(em, "Mdoomroom");
			doomroom.set("number", i);
			doomroom.set("name", "name" + i);
			doomroom.set("doombuild", doombuild);
			em.persist(doomroom);
		}
		// another build
		doombuild = JpaHelper.createNewEntity(em, "Mdoombuild");
		doombuild.set("name", "Kmart");
		doombuild.setId(2);
		em.persist(doombuild);
		em.flush();
		for (int i = 200; i < 400; i++) {
			doomroom = JpaHelper.createNewEntity(em, "Mdoomroom");
			doomroom.set("number", i);
			doomroom.set("name", "name" + i);
			doomroom.set("doombuild", doombuild);
			em.persist(doomroom);
		}

		// courses
		FleximsDynamicEntityImpl course;
		for (int i = 0; i < 200; i++) {
			course = JpaHelper.createNewEntity(em, "Mcourse");
			course.set("name", "chemi" + i);
			course.set("college", "Arts");
			em.persist(course);
		}
		for (int i = 0; i < 200; i++) {
			course = JpaHelper.createNewEntity(em, "Mcourse");
			course.set("name", "biotech" + i);
			course.set("college", "Engineer");
			em.persist(course);
		}
		FleximsDynamicEntityImpl onemany;
		for (int i = 0; i < 200; i++) {
			onemany = JpaHelper.createNewEntity(em, "MOneMany");
			onemany.set("name", "onemany" + i);
			em.persist(onemany);
		}

		em.getTransaction().commit();
	}
}
