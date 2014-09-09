package com.flexdms.flexims.unit.jpa.eclipselink;

import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import mockit.MockUp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbeddedOrderMetasource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestManyToManyOrderMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOrderMetaSource;

@RunWith(JUnit4.class)
public class TestCollectionOrder extends TestORMSetup {

	static enum ordertype {
		id, index, asc, desc
	};

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

	@SuppressWarnings("unchecked")
	@Test
	public void testBasicCollectionOrder() throws Exception {

		// -create EMF
		this.metaSrcClass = TestOrderMetaSource.class;
		this.initEmf();

		String testName = "Testorder";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl de = JpaHelper.createNewEntity(em, testName);

		de.set("OrderByDefault", Arrays.asList("b", "c", "a"));
		de.set("OrderByIndex", Arrays.asList("b", "c", "a"));
		de.set("OrderByValue", Arrays.asList("b", "c", "a"));
		em.persist(de);
		tx.commit();
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		de = em.find(de.getClass(), de.getId());

		assertThat(((List<String>) de.get("OrderByValue")), contains("a", "b", "c"));
		assertThat(((List<String>) de.get("OrderByIndex")), contains("b", "c", "a"));
		em.close();

	}

	public void testManytoManyOrder(ordertype ot) {
		// -create EMF
		this.metaSrcClass = TestManyToManyOrderMetaSource.class;
		TestManyToManyOrderMetaSource.buildMap();
		switch (ot) {
		case id:
			TestManyToManyOrderMetaSource.addOrderbyId();
			break;

		case asc:
			TestManyToManyOrderMetaSource.addOrderbyfnameAsc();
			break;
		case desc:
			TestManyToManyOrderMetaSource.addOrderbyfnameDesc();
			break;
		default:
			TestManyToManyOrderMetaSource.addOrderbyIndex();
			break;
		}
		this.initEmf();

		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, "Test");
		test1.set("fname", "test1");
		test1.set("lname", "lname");
		em.persist(test1);
		em.flush();

		FleximsDynamicEntityImpl test2 = JpaHelper.createNewEntity(em, "Test");
		test2.set("fname", "test4");
		test2.set("lname", "lname");
		em.persist(test2);
		em.flush();

		FleximsDynamicEntityImpl test3 = JpaHelper.createNewEntity(em, "Test");
		test3.set("fname", "test3");
		test3.set("lname", "lname");
		em.persist(test3);
		em.flush();

		FleximsDynamicEntityImpl test11 = JpaHelper.createNewEntity(em, "Test1");
		test11.set("fname", "test11");
		List<FleximsDynamicEntityImpl> tests = new ArrayList<>();
		tests.add(test3);
		tests.add(test2);
		tests.add(test1);
		test11.set("tests", tests);
		em.persist(test11);
		em.flush();

		em.getTransaction().commit();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		test11 = em.find(test11.getClass(), test11.getId());
		tests = test11.get("tests");

		switch (ot) {
		case id:
			assertThat(tests.get(0).getId(), equalTo(test1.getId()));
			assertThat(tests.get(1).getId(), equalTo(test2.getId()));
			assertThat(tests.get(2).getId(), equalTo(test3.getId()));
			break;

		case asc:
			assertThat(tests.get(0).getId(), equalTo(test1.getId()));
			assertThat(tests.get(1).getId(), equalTo(test3.getId()));
			assertThat(tests.get(2).getId(), equalTo(test2.getId()));
			break;
		case desc:
			assertThat(tests.get(0).getId(), equalTo(test2.getId()));
			assertThat(tests.get(1).getId(), equalTo(test3.getId()));
			assertThat(tests.get(2).getId(), equalTo(test1.getId()));
			break;
		default:
			assertThat(tests.get(0).getId(), equalTo(test3.getId()));
			assertThat(tests.get(1).getId(), equalTo(test2.getId()));
			assertThat(tests.get(2).getId(), equalTo(test1.getId()));
			break;
		}

	}

	@Test
	public void testManytoManyOrderById() throws Exception {
		testManytoManyOrder(ordertype.id);
	}

	@Test
	public void testManytoManyOrderByIndex() throws Exception {
		testManytoManyOrder(ordertype.index);
	}

	@Test
	public void testManytoManyOrderByfnameAsc() throws Exception {
		testManytoManyOrder(ordertype.asc);
	}

	@Test
	public void testManytoManyOrderByfnameDesc() throws Exception {
		testManytoManyOrder(ordertype.desc);
	}

	public void testInverseOrder(ordertype ot) {
		// -create EMF
		this.metaSrcClass = TestManyToManyOrderMetaSource.class;
		TestManyToManyOrderMetaSource.buildMap();

		switch (ot) {
		case id:
			TestManyToManyOrderMetaSource.addInverseOrderbyId();
			break;

		case asc:
			TestManyToManyOrderMetaSource.addInverseOrderbyfnameAsc();
			break;
		case desc:
			TestManyToManyOrderMetaSource.addInverseOrderbyfnameDesc();
			break;
		default:
			break;
		}

		this.initEmf();

		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, "Test");
		test1.set("fname", "test1");
		test1.set("lname", "lname");
		em.persist(test1);
		em.flush();

		List<FleximsDynamicEntityImpl> tests = null;

		FleximsDynamicEntityImpl test12 = JpaHelper.createNewEntity(em, "Test1");
		test12.set("fname", "test12");
		tests = new ArrayList<>();
		tests.add(test1);
		test12.set("tests", tests);
		em.persist(test12);
		em.flush();

		FleximsDynamicEntityImpl test11 = JpaHelper.createNewEntity(em, "Test1");
		test11.set("fname", "test11");
		tests = new ArrayList<>();
		tests.add(test1);
		test11.set("tests", tests);
		em.persist(test11);
		em.flush();

		FleximsDynamicEntityImpl test13 = JpaHelper.createNewEntity(em, "Test1");
		test13.set("fname", "test13");
		tests = new ArrayList<>();
		tests.add(test1);
		test13.set("tests", tests);
		em.persist(test13);
		em.flush();

		em.getTransaction().commit();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		test1 = em.find(test1.getClass(), test1.getId());
		List<FleximsDynamicEntityImpl> test1s = test1.get("test1s");

		switch (ot) {
		case id:
			assertThat(test1s.get(0).getId(), equalTo(test12.getId()));
			assertThat(test1s.get(1).getId(), equalTo(test11.getId()));
			assertThat(test1s.get(2).getId(), equalTo(test13.getId()));
			break;

		case asc:
			assertThat(test1s.get(0).getId(), equalTo(test11.getId()));
			assertThat(test1s.get(1).getId(), equalTo(test12.getId()));
			assertThat(test1s.get(2).getId(), equalTo(test13.getId()));
			break;
		case desc:
			assertThat(test1s.get(0).getId(), equalTo(test13.getId()));
			assertThat(test1s.get(1).getId(), equalTo(test12.getId()));
			assertThat(test1s.get(2).getId(), equalTo(test11.getId()));
			break;
		default:
			break;
		}
	}

	@Test
	public void testInverseManytoManyOrderByfnameDesc() throws Exception {
		testInverseOrder(ordertype.desc);
	}

	@Test
	public void testInverseManytoManyOrderByfnameAsc() throws Exception {
		testInverseOrder(ordertype.asc);
	}

	@Test
	public void testInverseManytoManyOrderById() throws Exception {
		testInverseOrder(ordertype.id);
	}

	@Test
	public void testManyToManyWithEmbeddedOrder() throws Exception {
		// -create EMF
		this.metaSrcClass = TestEmbeddedOrderMetasource.class;
		this.initEmf();

		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		FleximsDynamicEntityImpl test1 = JpaHelper.createNewEntity(em, "Test");
		test1.set("fname", "test1");
		test1.set("lname", "lname");

		FleximsDynamicEntityImpl embed = JpaHelper.createNewEntity(em, "Test1");
		embed.set("fname", "test11"); ////------------------test11
		test1.set("test1", embed);

		em.persist(test1);
		em.flush();

		FleximsDynamicEntityImpl test2 = JpaHelper.createNewEntity(em, "Test");
		test2.set("fname", "test1");
		test2.set("lname", "lname");

		embed = JpaHelper.createNewEntity(em, "Test1");
		embed.set("fname", "test14"); //------------------test14
		test2.set("test1", embed);

		em.persist(test2);
		em.flush();

		FleximsDynamicEntityImpl test3 = JpaHelper.createNewEntity(em, "Test");
		test3.set("fname", "test1");
		test3.set("lname", "lname");
		embed = JpaHelper.createNewEntity(em, "Test1");
		embed.set("fname", "test12"); //--------------------test12
		test3.set("test1", embed);

		em.persist(test3);
		em.flush();

		FleximsDynamicEntityImpl top = JpaHelper.createNewEntity(em, "Top");

		List<FleximsDynamicEntityImpl> tests = new ArrayList<>();
		tests.add(test1);
		tests.add(test2);
		tests.add(test3);
		top.set("tests", tests);
		em.persist(top);
		em.flush();

		em.getTransaction().commit();

		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();

		top = em.find(top.getClass(), top.getId());
		tests = top.get("tests");

		assertThat(tests.get(0).getId(), equalTo(test2.getId()));
		assertThat(tests.get(1).getId(), equalTo(test3.getId()));
		assertThat(tests.get(2).getId(), equalTo(test1.getId()));

	}
}
