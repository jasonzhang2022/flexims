package com.flexdms.flexims.unit.jpa.eclipselink;

import java.sql.Connection;
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
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestBasicCollectionMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestDataTypeMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbeddedCollectionMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestEmbeddedMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestManyManyMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneManyMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneManyUnidirectionalMetaSource;
import com.flexdms.flexims.unit.jpa.eclipselink.helper.TestOneOneMetaSource;

@RunWith(JUnit4.class)
public class TestDBDeletion extends TestORMSetup {

	public final static class DynamicMetaSourceMock extends MockUp<DynamicMetaSource> {

		void $clinit() {

		}
	}

	/**
	 * test Direct Mapping column deletion
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBasic() throws Exception {
		this.metaSrcClass = TestDataTypeMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, testName, "propint");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}

		tx.commit();
		em.close();
	}
	
	@Test
	public void testBasicTable() throws Exception {
		this.metaSrcClass = TestDataTypeMetaSource.class;
		this.initEmf();

		String testName = "Test";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, testName);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}

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
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, testName, "propstrs");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();

	}
	
	/**
	 * Test collection can be created
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEmbedded() throws Exception {

		// -create EMF
		TestEmbeddedMetaSource.hasRel = true;
		this.metaSrcClass = TestEmbeddedMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, testName, "test1");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();

	}
	
	
	/**
	 * Test collection can be created
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMultiEmbedded() throws Exception {

		// -create EMF
		TestEmbeddedCollectionMetaSource.hasRel = true;
		this.metaSrcClass = TestEmbeddedCollectionMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, testName, "test1");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();

	}
	
	@Test
	public void testOneToOneRelation() throws Exception {

		TestOneOneMetaSource.hasRel = true;
		this.metaSrcClass = TestOneOneMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name, "test");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		//no database deletion for inverseRelation
		sqlsList = JpaHelper.deletePropDBStructure(em, testName, "test1");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}
	
	@Test
	public void testOneToOneRelationTable() throws Exception {

		TestOneOneMetaSource.hasRel = true;
		this.metaSrcClass = TestOneOneMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		//no database deletion for inverseRelation
		sqlsList = JpaHelper.deletePropDBStructure(em, testName);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}
	

	
	@Test
	public void testOneToManyNonTypicalRelation() throws Exception {

		TestOneManyUnidirectionalMetaSource.hasRel = true;
		this.metaSrcClass = TestOneManyUnidirectionalMetaSource.class;
		this.initEmf();
		

		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, "Test", "test1s");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}
	@Test
	public void testOneToManyNonTypicalRelationTable() throws Exception {

		TestOneManyUnidirectionalMetaSource.hasRel = true;
		this.metaSrcClass = TestOneManyUnidirectionalMetaSource.class;
		this.initEmf();
		

		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, "Test");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}
	
	@Test
	public void testManyToOneTypicalRelation() throws Exception {

		TestOneManyMetaSource.hasRel = true;
		this.metaSrcClass = TestOneManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name, "test");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		sqlsList = JpaHelper.deletePropDBStructure(em, testName, "test1s");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}

	@Test
	public void testManyToOneTypicalRelationTable() throws Exception {

		TestOneManyMetaSource.hasRel = true;
		this.metaSrcClass = TestOneManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		sqlsList = JpaHelper.deletePropDBStructure(em, testName);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}
	
	@Test
	public void testManyManyRelation() throws Exception {
		TestManyManyMetaSource.hasRel = true;
		this.metaSrcClass = TestManyManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name, "tests");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		sqlsList = JpaHelper.deletePropDBStructure(em, testName, "test1s");
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}

	@Test
	public void testManyManyRelationTable() throws Exception {
		TestManyManyMetaSource.hasRel = true;
		this.metaSrcClass = TestManyManyMetaSource.class;
		this.initEmf();

		String testName = "Test";
		String test1Name = "Test1";
		// make sure basic test works
		EntityManager em = App.getEM();
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		
		Connection conn = em.unwrap(Connection.class);

		List<String> sqlsList = JpaHelper.deletePropDBStructure(em, test1Name);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		sqlsList = JpaHelper.deletePropDBStructure(em, testName);
		for (String sql : sqlsList) {
			System.out.println(sql);
			conn.createStatement().execute(sql);
		}
		tx.commit();
		em.close();
	}

}
