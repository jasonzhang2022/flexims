package com.flexdms.flexims.unit.query;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.Parameter;
import com.flexdms.flexims.query.PropertyCondition;
import com.flexdms.flexims.unit.CDIContainerRule;
import com.flexdms.flexims.unit.EntityManagerRule;
import com.flexdms.flexims.unit.JPA_JAXB_EmbeddedDerby_Rule;
import com.flexdms.flexims.unit.RequestScopeRule;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;

public class QueryTestBase {

	@ClassRule
	public static CDIContainerRule cdiContainerRule = new CDIContainerRule();
	@ClassRule
	public static JPA_JAXB_EmbeddedDerby_Rule clientSetupRule = new JPA_JAXB_EmbeddedDerby_Rule();

	@Rule
	public EntityManagerRule eManagerRule = new EntityManagerRule();
	@Rule
	public RequestScopeRule requestScopeRule = new RequestScopeRule();

	public EntityManager em;

	@Before
	public void emsetup() throws JAXBException, SQLException {
		em = eManagerRule.em;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM DEFAULTTYPEDQUERY_CONDITIONS");
		connection.createStatement().execute("DELETE FROM TYPEDQUERY");
		tx.commit();

	}

	public ConditionQuery createSimpleQuery(String qType, String propName, Operator op) {
		return QueryUtil.createSimpleQuery(qType, propName, op, em);
	}

	public List<FleximsDynamicEntityImpl> runQuery(ConditionQuery query, Object... values) {
		query.buildQuery(em);
		List<Parameter> paramsList = query.getParameters();
		int i = 0;
		for (Parameter param : paramsList) {
			param.setValue(values[i]);
			i++;
		}

		List results = query.fetchAllResult(em);
		return results;

	}

	public int getCount(ConditionQuery query, Object... values) {
		query.buildQuery(em);
		List<Parameter> paramsList = query.getParameters();
		int i = 0;
		for (Parameter param : paramsList) {
			param.setValue(values[i]);
			i++;
		}

		return query.getResultCount(em);

	}

	public List<FleximsDynamicEntityImpl> performSimpleQuery(String qType, String propName, Operator op, Object... values) {
		ConditionQuery query = createSimpleQuery(qType, propName, op);
		return runQuery(query, values);
	}

	public List<FleximsDynamicEntityImpl> performIgnoreCaseLike(String qType, String propName, Operator op, Object... values) {
		ConditionQuery query = createSimpleQuery(qType, propName, op);
		PropertyCondition pcCondition = (PropertyCondition) query.getConditions().get(0);
		pcCondition.setIgnoreCase(true);
		return runQuery(query, values);
	}

	public List<FleximsDynamicEntityImpl> performTraceDown(String qType, String propName, ConditionQuery traceDown, Object... values) {
		ConditionQuery query = createSimpleQuery(qType, propName, Operator.tracedown);
		PropertyCondition pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(traceDown.getEntity());
		return runQuery(query, values);
	}

	public List<FleximsDynamicEntityImpl> performTraceDownAndSimple(String qType, String simpleProp, Operator simpleOp, String propName,
			ConditionQuery traceDown, Object... values) {
		ConditionQuery query = createSimpleQuery(qType, simpleProp, simpleOp);

		List<PropertyCondition> conditions = query.getConditions();

		FleximsDynamicEntityImpl entity = JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME);
		PropertyCondition pc = new PropertyCondition(entity, query);

		pc.setProp(propName);
		pc.setOperator(Operator.tracedown);
		pc.setTracceDown(traceDown.getEntity());

		conditions.add(pc);
		return runQuery(query, values);
	}

	public List<Long> getIds(List<FleximsDynamicEntityImpl> results) {
		List<Long> ids = new ArrayList<Long>();
		for (FleximsDynamicEntityImpl i : results) {
			ids.add(i.getId());
		}
		return ids;
	}

	public void setMultiInt() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM COLLECTION1_PROPINT");
		connection.createStatement().execute("DELETE FROM COLLECTION1_SHORTSTRING");
		connection.createStatement().execute("DELETE FROM COLLECTION1");
		tx.commit();
		tx.begin();

		int[][] ints = { { 5000, 5001 }, { 5010, 5011, 5012 }, { 5020, 5021 }, { 5030, 5031 }, { 5040, 5000 }, {}, {} };

		for (int i = 500; i <= 506; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			List<String> names = new ArrayList<>(1);
			names.add("jason50" + i);
			entity.set("shortstring", names);
			entity.setId(Long.valueOf(i));
			List<Integer> ints1 = new ArrayList<>(3);
			for (int j = 0; j < ints[i - 500].length; j++) {
				ints1.add(ints[i - 500][j]);
			}
			entity.set(propName, ints1);
			em.persist(entity);
		}
		tx.commit();
	}

	public void setMultiStr() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM COLLECTION1_PROPINT");
		connection.createStatement().execute("DELETE FROM COLLECTION1_SHORTSTRING");
		connection.createStatement().execute("DELETE FROM COLLECTION1");
		tx.commit();
		tx.begin();

		String[][] strss = { { "jj5001", "jj5002" }, { "pp5011", "pp5012", "jj5013" }, { "kk5021", "kk5022" }, { "mm5031", "kk5032" },
				{ "nn5021", "KK5022" }, {}, {} };

		for (int i = 500; i <= 506; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			List<String> names = new ArrayList<>(1);
			for (int j = 0; j < strss[i - 500].length; j++) {
				names.add(strss[i - 500][j]);
			}
			entity.set(propName, names);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
	}

	public void saveQuery(ConditionQuery query) {
		QueryUtil.saveQuery(query, em);
	}

	public void deleteStudent() throws SQLException {
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM MSTUDENT_MCOURSE");
		connection.createStatement().execute("DELETE FROM MSTUDENT_MONEMANY");
		connection.createStatement().execute("DELETE FROM MSTUDENT_PROPINT");

		connection.createStatement().execute("DELETE FROM MCOURSE");
		connection.createStatement().execute("DELETE FROM MONEMANY");
		connection.createStatement().execute("DELETE FROM MSTUDENT");

		connection.createStatement().execute("DELETE FROM MDOOMROOM");
		connection.createStatement().execute("DELETE FROM MDOOMBUILD");
		tx.commit();
	}

	public void setOne2One() throws SQLException {

		FleximsDynamicEntityImpl entity;

		deleteStudent();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl build = JpaHelper.createNewEntity(em, "Mdoombuild");
		build.set("name", "build");
		em.persist(build);
		em.flush();

		String roomname[] = { "1", "11", "2", "21", "22" };
		List<FleximsDynamicEntityImpl> rooms = new ArrayList<>(5);
		for (int i = 501; i <= 505; i++) {
			entity = JpaHelper.createNewEntity(em, "Mdoomroom");
			entity.set("doombuild", build);
			rooms.add(entity);
			entity.set("number", i);
			entity.set("name", roomname[i - 501]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();
		String studentnames[] = { "j1", "j2", "k1", "k2", "k3" };
		int propint[][] = { { 2, 1 }, { 6, 4, 5 }, { 6, 7 }, {}, {} };
		int roomidxs[] = { 1, 0, 2, 3, -1 };
		for (int i = 501; i <= 505; i++) {
			entity = JpaHelper.createNewEntity(em, "Mstudent");
			int roomidx = roomidxs[i - 501];
			if (roomidx != -1) {
				entity.set("doomroom", rooms.get(roomidx));
			}
			entity.set("Name", studentnames[i - 501]);
			entity.set("number", i);
			entity.setId(Long.valueOf(i));
			List<Integer> pints = new ArrayList<>(2);
			for (int j = 0; j < propint[i - 501].length; j++) {
				pints.add(Integer.valueOf(propint[i - 501][j]));
			}
			entity.set("propint", pints);
			em.persist(entity);
		}
		tx.commit();
	}

	public void setManyToMany() throws SQLException {
		FleximsDynamicEntityImpl entity;
		deleteStudent();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		String coursename[] = { "many1", "many11", "many2", "man21", "many" };
		List<FleximsDynamicEntityImpl> courses = new ArrayList<>(5);
		for (int i = 501; i <= 505; i++) {
			entity = JpaHelper.createNewEntity(em, "Mcourse");
			courses.add(entity);
			entity.set("name", coursename[i - 501]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		List<FleximsDynamicEntityImpl> builds = new ArrayList<>(5);
		String buildnames[] = { "main1", "main11", "main2", "main22" };
		for (int i = 601; i <= 604; i++) {
			entity = JpaHelper.createNewEntity(em, "Mdoombuild");
			builds.add(entity);
			entity.set("name", buildnames[i - 601]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		String studentnames[] = { "jason1", "jason11", "jason12", "jason22" };

		int coursesidxs[][] = { { 0, 1 }, { 1, 2 }, { 2, 3 }, {} };
		int buildidx[] = { 0, 2, 0, -1 };
		for (int i = 701; i <= 704; i++) {
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
			em.persist(entity);
		}
		tx.commit();
	}

	public void setupsingleInt() throws SQLException {
		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM BASICTYPE");
		tx.commit();
		tx.begin();
		int vs[] = { 500, 501, 502, 503, 504, 505, 506 };
		for (int i = 500; i <= 506; i++) {
			entity = JpaHelper.createNewEntity(em, "Basictype");
			entity.set("shortstring", "test" + i);
			entity.set("propint", vs[i - 500]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
	}

	public void setOneManyTrace() throws SQLException {
		FleximsDynamicEntityImpl entity;
		deleteStudent();
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

	public void setMultiIntCollectionMode() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM COLLECTION1_PROPINT");
		connection.createStatement().execute("DELETE FROM COLLECTION1_SHORTSTRING");
		connection.createStatement().execute("DELETE FROM COLLECTION1");
		tx.commit();
		tx.begin();

		int[][] ints = { { 5000, 5001 }, { 6010, 6011, 5012 }, { 5020, 5021 }, { 7030, 6031 } };

		for (int i = 500; i <= 503; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			List<String> names = new ArrayList<>(1);
			names.add("jason50" + i);
			entity.set("shortstring", names);
			entity.setId(Long.valueOf(i));
			List<Integer> ints1 = new ArrayList<>(3);
			for (int j = 0; j < ints[i - 500].length; j++) {
				ints1.add(ints[i - 500][j]);
			}
			entity.set(propName, ints1);
			em.persist(entity);
		}
		tx.commit();

	}

	public void setMultiStrCollectionMode() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM COLLECTION1_PROPINT");
		connection.createStatement().execute("DELETE FROM COLLECTION1_SHORTSTRING");
		connection.createStatement().execute("DELETE FROM COLLECTION1");
		tx.commit();
		tx.begin();

		String[][] strss = { { "jj5001", "JJ5002" }, { "JJ5001" }, { "kk5021", "kk5022" }, { "KK5021", "KK5022" }, { "jj5001", "JJ5001" } };

		for (int i = 500; i <= 504; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			List<String> names = new ArrayList<>(1);
			for (int j = 0; j < strss[i - 500].length; j++) {
				names.add(strss[i - 500][j]);
			}
			entity.set(propName, names);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
	}

	public void setOneMany() throws SQLException {
		FleximsDynamicEntityImpl entity;
		deleteStudent();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<FleximsDynamicEntityImpl> builds = new ArrayList<>(5);
		String buildnames[] = { "main1", "main11", "main2", "main22" };
		for (int i = 601; i <= 604; i++) {
			entity = JpaHelper.createNewEntity(em, "Mdoombuild");
			builds.add(entity);
			entity.set("name", buildnames[i - 601]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		String studentnames[] = { "jason1", "jason11", "jason12", "jason21" };

		int buildidx[] = { 0, 1, 0, -1 };
		for (int i = 701; i <= 704; i++) {
			entity = JpaHelper.createNewEntity(em, "Mdoomroom");
			entity.set("name", studentnames[i - 701]);
			int buildIdx = buildidx[i - 701];
			if (buildIdx != -1) {
				entity.set("doombuild", builds.get(buildIdx));
			}
			entity.setId(Long.valueOf(i));
			entity.set("number", i - 700);
			em.persist(entity);
		}
		tx.commit();
	}

	public void setOneManyTable() throws SQLException {
		FleximsDynamicEntityImpl entity;
		deleteStudent();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		String coursename[] = { "many1", "many11", "many2", "man21", "many" };
		List<FleximsDynamicEntityImpl> onemanys = new ArrayList<>(5);
		for (int i = 501; i <= 504; i++) {
			entity = JpaHelper.createNewEntity(em, "MOneMany");
			onemanys.add(entity);
			entity.set("name", coursename[i - 501]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		em.flush();

		String studentnames[] = { "jason1", "jason11", "jason12", "jason22" };

		int onemanyidxs[][] = { { 1, 2, 0 }, { 3 }, {}, {} };
		for (int i = 601; i <= 603; i++) {
			entity = JpaHelper.createNewEntity(em, "Mstudent");

			List<FleximsDynamicEntityImpl> oms = new ArrayList<>(3);
			for (int j = 0; j < onemanyidxs[i - 601].length; j++) {
				oms.add(onemanys.get(onemanyidxs[i - 601][j]));
			}

			entity.set("OneManys", oms);
			entity.set("Name", studentnames[i - 601]);
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
	}

	public FleximsDynamicEntityImpl setupEmbedmain() {

		int i = 0;
		FleximsDynamicEntityImpl main = JpaHelper.createNewEntity(em, "Embedmain");
		main.set("fname", "main" + i);

		FleximsDynamicEntityImpl embed1 = JpaHelper.createNewEntity(em, "Embed1");
		embed1.set("streetAddress", "singlestr" + i);
		embed1.set("mstr", Arrays.asList("mstr" + i, "mstr" + (i + 1)));
		embed1.set("mint", Arrays.asList(i, i + 1));
		main.set("singleembed", embed1);

		FleximsDynamicEntityImpl embed2 = JpaHelper.createNewEntity(em, "Embed2");
		embed2.set("streetAddress", "mstr" + i);
		embed2.set("extra1", "extra" + i);

		FleximsDynamicEntityImpl embed3 = JpaHelper.createNewEntity(em, "Embed2");
		embed3.set("streetAddress", "mstr" + (i + 1));
		embed3.set("extra1", "extra" + i);
		main.set("multiembed", Arrays.asList(embed2, embed3));
		em.getTransaction().begin();
		em.persist(main);
		em.getTransaction().commit();
		em.refresh(main);
		return main;

	}
}
