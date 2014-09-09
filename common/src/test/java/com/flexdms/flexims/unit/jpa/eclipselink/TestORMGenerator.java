package com.flexdms.flexims.unit.jpa.eclipselink;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import mockit.MockUp;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;
import com.flexdms.flexims.jpa.helper.ByteArray;
import com.flexdms.flexims.jpa.helper.NameValueList;
import com.flexdms.flexims.unit.TestEntityUtil;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestBasicCollectionMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestBasicMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestDataTypeMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestDemoRelation;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbeddedCollectionMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbeddedMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbededXmlMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestInheritanceMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestManyManyMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestMetaSourceModEmbedded;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestMetaSourceModEntity;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneManyMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneManyUnidirectionalMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneOneMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestSpecialTypeMetaSource;

/**
 * Test various feature of Dynamic Entity to make sure it behaves as we
 * expected.
 * 
 * @author jason.zhang
 * 
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestORMGenerator extends TestORMSetup {

	public void generateSchema(Class<?> srcCLass) throws Exception {
		String schema = App.getPersistenceUnit().getSchema();
		System.err.println("generated schema");
		System.err.println(schema);
		assertTrue(schema.toString().length() > 10);

	}

	public final static class DynamicMetaSourceMock extends MockUp<DynamicMetaSource> {

		void $clinit() {

		}
	}

	/**
	 * Whether how all basic type are persisted
	 * 
	 * @throws Exception
	 */
	@Test
	public void testDataTypes() throws Exception {
		this.metaSrcClass = TestDataTypeMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);

		// text
		de.set("shortstring", "this is a short string");
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
		em.persist(de);
		tx.commit();
		em.close();

	}

	/**
	 * Test collection can be created
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBasicCollection() throws Exception {

		// -create EMF
		this.metaSrcClass = TestBasicCollectionMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);

		Set<String> strs = new HashSet<>();
		strs.add("jason");
		strs.add("jason1");
		de.set("propstrs", strs);

		Set<Calendar> dates = new HashSet<>();
		dates.add(Calendar.getInstance());
		dates.add(Calendar.getInstance());
		de.set("propdates", dates);

		em.persist(de);
		tx.commit();
		em.close();

	}

	

	/**
	 * Make sure Dynamic Entity Can be updated.
	 */
	@Test
	public void testUpdate() throws Exception {

		// -create EMF
		this.metaSrcClass = TestBasicMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);

		de.set("fname", "test");
		de.set("time1", Calendar.getInstance());
		em.persist(de);
		tx.commit();

		tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl de1 = em.find(de.getClass(), de.getId());
		de1.set("fname", "test2");
		tx.commit();

		FleximsDynamicEntityImpl de2 = em.find(de.getClass(), de.getId());
		assertEquals(de2.get("fname"), "test2");

		em.close();

	}

	/**
	 * test basic functionality 1. a basic entity with string and time can be
	 * created. 2. new property can be added dynamically.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBasic() throws Exception {

		// -create EMF
		this.metaSrcClass = TestBasicMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);

		de.set("fname", "test");
		de.set("time1", Calendar.getInstance());
		em.persist(de);
		tx.commit();
		em.close();

		// add one more attributes
		TestBasicMetaSource.twoAttributes = true;
		this.refreshEmf();
		// refresh
		// refreshEmf();
		//

		// make sure second attributes are added and table column are created
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		de = JpaHelper.createNewEntity(em, testName);

		de.set("fname", "test");
		de.set("lname", "zhang");
		em.persist(de);
		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), de.getId());
		em.refresh(fresh);
		assertThat(de.get("lname"), equalTo(fresh.get("lname")));

		// have to remove this before we could output scripts
		// //properties.remove(PersistenceUnitProperties.DDL_GENERATION);
		generateSchema(TestBasicMetaSource.class);
	}

	/**
	 * Test custom object can be saved and readed using jaxb
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSpecialTypeObject() throws Exception {

		// -create EMF
		this.metaSrcClass = TestSpecialTypeMetaSource.class;
		this.initEmf();

		String testName = "Mspecial";

		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		// object
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);
		TestEntityUtil.setSpecialtype(de);
		em.persist(de);

		// byte
		tx.commit();

		FleximsDynamicEntityImpl de1 = em.find(de.getClass(), de.getId());
		em.refresh(de1);
		NameValueList nv = de.get("propobject");
		NameValueList nv1 = de1.get("propobject");

		ByteArray b = de.get("propbyte");
		ByteArray b1 = de1.get("propbyte");
		assertThat(nv.toMap().get("first name"), equalTo(nv1.toMap().get("first name")));
		assertThat(b.value, equalTo(b1.value));
		System.err.println(new String(b1.value));

		em.close();

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);
		generateSchema(TestBasicMetaSource.class);
	}

	/**
	 * relation can be added at a later stage
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOneToManyRelation() throws Exception {

		this.metaSrcClass = TestOneManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);

		tx.commit();
		em.close();

		// refresh
		TestOneManyMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		test1.set("test", test);
		em.persist(test1);

		FleximsDynamicEntityImpl test2 = JpaHelper.createNewEntity(em, test1Name);
		test2.set("fname", "test1");
		test2.set("test", test);
		em.persist(test2);
		tx.commit();

		tx.begin();
		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		List<DynamicEntity> list = fresh.get("test1s");
		assertTrue(2 == list.size());

		assertTrue(((DynamicEntity) list.get(0)).get("test") == fresh);
		tx.commit();
		em.close();

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);
		generateSchema(TestOneManyMetaSource.class);
	}

	@Test
	public void testOneToOneRelation() throws Exception {

		this.metaSrcClass = TestOneOneMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);

		tx.commit();
		em.close();

		// refresh
		TestOneOneMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		test1.set("test", test);
		em.persist(test1);
		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		FleximsDynamicEntityImpl newtest1 = fresh.get("test1");
		assertTrue(newtest1.getId() == test1.getId());
		em.close();

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestOneOneMetaSource.class);
	}

	@Test
	public void testManyManyRelation() throws Exception {

		this.metaSrcClass = TestManyManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);

		tx.commit();
		em.close();

		// -----------------------
		TestManyManyMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();

		tx = em.getTransaction();
		// remove legacy data from previous test
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("delete from test1_test");
		tx.commit();

		tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl xtest0 = JpaHelper.createNewEntity(em, testName);
		xtest0.set("fname", "xtest0");
		xtest0.set("lname", "zhang");
		em.persist(xtest0);

		FleximsDynamicEntityImpl xtest1 = JpaHelper.createNewEntity(em, testName);
		xtest1.set("fname", "xtest1");
		xtest1.set("lname", "zhang");
		em.persist(xtest1);

		List tests = new ArrayList<>();
		tests.add(xtest0);
		tests.add(xtest1);

		test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		// ---relationship
		test1.set("tests", tests);
		em.persist(test1);

		FleximsDynamicEntityImpl test2 = JpaHelper.createNewEntity(em, test1Name);
		test2.set("fname", "test12");
		test2.set("tests", tests);
		em.persist(test2);

		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), xtest0.getId());
		em.refresh(fresh);
		List<FleximsDynamicEntityImpl> newtests = fresh.get("test1s");
		assertTrue(newtests.size() == 2);

		fresh = em.find(JpaHelper.getEntityClass(em, testName), xtest1.getId());
		em.refresh(fresh);
		newtests = fresh.get("test1s");
		assertTrue(newtests.size() == 2);
		em.close();

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestManyManyMetaSource.class);
	}

	@Test
	public void testOneManyUnidirectionRelation() throws Exception {

		// -create EMF

		this.metaSrcClass = TestOneManyUnidirectionalMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);

		tx.commit();
		em.close();

		// -----------------------
		TestOneManyUnidirectionalMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();

		tx = em.getTransaction();
		// remove legacy data from previous test
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("delete from test_test1");
		tx.commit();

		tx = em.getTransaction();
		tx.begin();

		test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);

		FleximsDynamicEntityImpl test2 = JpaHelper.createNewEntity(em, test1Name);
		test2.set("fname", "test12");
		em.persist(test2);

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "xtest0");
		test.set("lname", "zhang");
		List test1sList = new ArrayList(2);
		test1sList.add(test1);
		test1sList.add(test2);
		test.set("test1s", test1sList);
		em.persist(test);

		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		List<FleximsDynamicEntityImpl> newtests = fresh.get("test1s");
		assertTrue(newtests.size() == 2);

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestOneManyUnidirectionalMetaSource.class);
	}

	@Test
	public void testEmbedded() throws Exception {

		this.metaSrcClass = TestEmbeddedMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		tx.commit();
		em.close();

		// -----------------------
		TestEmbeddedMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();

		tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "xtest0");
		test.set("lname", "zhang");
		test.set("test1", test1);

		em.persist(test);

		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		assertThat((String) ((DynamicEntity) fresh.get("test1")).get("fname"), equalTo("test1"));

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestEmbeddedMetaSource.class);
	}

	@Test
	public void testEmbeddedCollection() throws Exception {

		this.metaSrcClass = TestEmbeddedCollectionMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		tx.commit();
		em.close();

		// -----------------------
		TestEmbeddedCollectionMetaSource.hasRel = true;
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();

		tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl test11 = JpaHelper.createNewEntity(em, test1Name);
		test11.set("fname", "test11");

		FleximsDynamicEntityImpl test12 = JpaHelper.createNewEntity(em, test1Name);
		test12.set("fname", "test12");

		List<FleximsDynamicEntityImpl> test1 = new ArrayList<>(2);
		test1.add(test11);
		test1.add(test12);

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "xtest0");
		test.set("lname", "zhang");
		test.set("test1", test1);

		em.persist(test);

		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		Set<DynamicEntityImpl> list = fresh.get("test1");

		assertThat(list, hasSize(2));

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestEmbeddedMetaSource.class);
	}

	@Test
	public void testEmbeddedFromXml() throws Exception {

		// -create EMF
		this.metaSrcClass = TestEmbededXmlMetaSource.class;
		this.initEmf();

		String testName = "Embedmain";

		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		// object
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);
		TestEntityUtil.setEmbedmain(de, em);
		em.persist(de);

		tx.commit();

		// we can update

		FleximsDynamicEntityImpl de1 = em.find(de.getClass(), de.getId());
		FleximsDynamicEntityImpl embeded1 = de1.get("singleembed");
		List<Integer> mints = embeded1.get("mint");
		mints.add(9);
		tx.begin();
		em.merge(de1);
		em.close();

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestBasicMetaSource.class);
	}

	@Test
	public void testInheritance() throws Exception {

		this.metaSrcClass = TestInheritanceMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		tx.commit();
		em.close();

		// -----------------------
		TestInheritanceMetaSource.hasRel = true;
		refreshEmf();

		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname1", "test1");

		test1.set("fname", "xtest0");
		test1.set("lname", "zhang");

		em.persist(test1);

		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test1.getId());
		em.refresh(fresh);
		assertThat(fresh.getClass().getSimpleName(), equalTo("Test1"));

		// have to remove this before we could output scripts
		// properties.remove(PersistenceUnitProperties.DDL_GENERATION);

		generateSchema(TestInheritanceMetaSource.class);
	}

	/**
	 * make sure we can use static entity mapping cache to hold the relation.
	 * This is a simulation the strategy we used in production
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStaticEntityMappingInstance() throws Exception {

		this.metaSrcClass = TestMetaSourceModEntity.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		tx.commit();
		em.close();

		// refresh
		TestMetaSourceModEntity.addTest1();
		refreshEmf();

		// make sure second attributes are added and table column are created
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);

		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, test1Name);
		test1.set("fname", "test1");
		em.persist(test1);
		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, test1Name), test1.getId());
		em.refresh(fresh);
		assertTrue(fresh.get("fname").equals("test1"));
		em.close();

		System.err.println("Start to generate schema---------------");
		generateSchema(TestMetaSourceModEntity.class);
		refreshEmf();

		System.err.println("Start to generate schema again---------------");
		generateSchema(TestMetaSourceModEntity.class);
		refreshEmf();
		// make sure we still can use the entity after schema generation
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);
		tx.commit();

	}

	/**
	 * make sure we can use static entity mapping cache to hold the relation.
	 * This is a simulation the strategy we used in production Verify that
	 * un-associated embedded can be inserted into entity mapping
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStaticEntityMappingEmbedded() throws Exception {

		this.metaSrcClass = TestMetaSourceModEmbedded.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";

		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);
		tx.commit();
		em.close();

		// refresh
		TestMetaSourceModEmbedded.addEmbedded();
		refreshEmf();

		// make sure system still works when embedded is added
		// make sure second attributes are added and table column are created
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();
		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);
		tx.commit();

		DynamicEntity fresh = em.find(JpaHelper.getEntityClass(em, testName), test.getId());
		em.refresh(fresh);
		assertTrue(fresh.get("fname").equals("test"));
		em.close();

		MetaSourceBuilder.toXml(TestMetaSourceModEmbedded.entityMappings, new OutputStreamWriter(System.out));

		System.err.println("Start to generate schema---------------");
		generateSchema(TestMetaSourceModEntity.class);
		refreshEmf();

		System.err.println("Start to generate schema again---------------");
		generateSchema(TestMetaSourceModEntity.class);
		refreshEmf();

		// make sure we still can use the entity after schema generation
		em = App.getEM();
		tx = em.getTransaction();
		tx.begin();

		test = JpaHelper.createNewEntity(em, testName);
		test.set("fname", "test");
		test.set("lname", "zhang");
		em.persist(test);
		tx.commit();

	}

	@Test
	public void testDemoRelation() throws Exception {

		// -create EMF

		this.metaSrcClass = TestDemoRelation.class;
		this.initEmf();

		// make sure basic test works
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl student = TestEntityUtil.createAndPersisteDemoRelationObject(em);

		em.close();

	}

	@Test
	public void testDemoRelationPrivate() throws Exception {

		// -create EMF

		this.metaSrcClass = TestDemoRelation.class;
		this.initEmf();

		// make sure basic test works
		EntityManager em = App.getEM();
		FleximsDynamicEntityImpl build = TestEntityUtil.createDoomBuild(em);
		EntityTransaction tx = em.getTransaction();

		tx.begin();
		em.refresh(build);
		List<FleximsDynamicEntityImpl> rooms = build.get("rooms");
		int size = rooms.size();
		rooms.remove(size - 1);
		tx.commit();

		tx.begin();
		em.refresh(build);
		rooms = build.get("rooms");
		int size1 = rooms.size();
		long roomid = rooms.get(0).getId();
		em.remove(build);
		tx.commit();

		assertThat(size1, equalTo(size - 1));
		tx.begin();
		DynamicEntity room = em.find(JpaHelper.getEntityClass(em, "Mdoomroom"), roomid);

		tx.commit();

		assertNull(room);
		em.close();

	}

	@Test
	public void testDemoRelationPrivateCascadePersist() throws Exception {

		// -create EMF

		this.metaSrcClass = TestDemoRelation.class;
		this.initEmf();

		EntityManager em = App.getEM();

		FleximsDynamicEntityImpl student = TestEntityUtil.createStudentOneManys(em);

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(student);
		tx.commit();

		em.refresh(student);
		List<FleximsDynamicEntityImpl> oneManysList = student.get("OneManys");
		assertThat(oneManysList, hasSize(2));
	}

}
