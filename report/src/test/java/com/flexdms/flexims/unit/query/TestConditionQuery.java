package com.flexdms.flexims.unit.query;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.eclipse.persistence.dynamic.DynamicEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.PropertyCondition;

@RunWith(JUnit4.class)
public class TestConditionQuery extends QueryTestBase {

	@Test
	public void TestPrimitiveSingleInt() throws SQLException {
		String qType = "Basictype";
		String propName = "propint";

		setupsingleInt();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;
		results = performSimpleQuery(qType, propName, Operator.eq, 500);
		assertThat(results, hasSize(1));
		assertThat((Integer) results.iterator().next().get(propName), equalTo(500));

		results = performSimpleQuery(qType, propName, Operator.gt, 504);
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.ge, 505);
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.lt, 502);
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l));

		results = performSimpleQuery(qType, propName, Operator.le, 501);
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l));

		results = performSimpleQuery(qType, propName, Operator.ne, 501);
		assertThat(results, hasSize(6));

		results = performSimpleQuery(qType, propName, Operator.between, 501, 504);
		assertThat(results, hasSize(4));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(501l, 502l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.notbetween, 501, 505);
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 506l));

		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(7));

		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(0));

		results = performSimpleQuery(qType, propName, Operator.oneof, "501,502");
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(501l, 502l));

		results = performSimpleQuery(qType, propName, Operator.notoneof, "501,502");
		assertThat(results, hasSize(5));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 503l, 504l, 505l, 506l));

	}

	@Test
	public void TestPrimitiveSingleStr() throws SQLException {
		String qType = "Basictype";
		String propName = "shortstring";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM BASICTYPE");
		tx.commit();
		tx.begin();

		for (int i = 500; i <= 506; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			if (i >= 504) {
				entity.set(propName, "req" + (i + 10));
			} else {
				entity.set(propName, "req" + i);
			}
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;
		results = performSimpleQuery(qType, propName, Operator.eq, "req500");
		assertThat(results, hasSize(1));
		assertThat((String) results.iterator().next().get(propName), equalTo("req500"));

		results = performSimpleQuery(qType, propName, Operator.ne, "req500");
		assertThat(results, hasSize(6));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(501l, 502l, 503l, 504l, 505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.like, "req50_");
		assertThat(results, hasSize(4));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l, 502l, 503l));

		results = performSimpleQuery(qType, propName, Operator.like, "req51%");
		assertThat(results, hasSize(3));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l, 504l));

		results = performSimpleQuery(qType, propName, Operator.notlike, "req50_");
		assertThat(results, hasSize(3));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l, 504l));
		;

		results = performSimpleQuery(qType, propName, Operator.notlike, "req51%");
		assertThat(results, hasSize(4));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l, 502l, 503l));

		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(7));

		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(0));

		results = performSimpleQuery(qType, propName, Operator.oneof, "req500,req501");
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l));

		results = performSimpleQuery(qType, propName, Operator.notoneof, "req500,req501");
		assertThat(results, hasSize(5));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(502l, 503l, 504l, 505l, 506l));

	}

	@Test
	public void TestPrimitiveSingleStrIgnoreCase() throws SQLException {
		String qType = "Basictype";
		String propName = "shortstring";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM BASICTYPE");
		tx.commit();
		tx.begin();
		for (int i = 500; i <= 506; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			if (i >= 504) {
				entity.set(propName, "req" + (i + 10));
			} else {
				entity.set(propName, "req" + i);
			}
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		results = performIgnoreCaseLike(qType, propName, Operator.like, "Req50_");
		assertThat(results, hasSize(4));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l, 502l, 503l));

		results = performIgnoreCaseLike(qType, propName, Operator.like, "Req51%");
		assertThat(results, hasSize(3));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l, 504l));

		results = performIgnoreCaseLike(qType, propName, Operator.notlike, "Req50_");
		assertThat(results, hasSize(3));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(505l, 506l, 504l));
		;

		results = performIgnoreCaseLike(qType, propName, Operator.notlike, "Req51%");
		assertThat(results, hasSize(4));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l, 502l, 503l));

		results = performIgnoreCaseLike(qType, propName, Operator.oneof, "Req500,Req501");
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(500l, 501l));

		results = performIgnoreCaseLike(qType, propName, Operator.notoneof, "Req500,Req501");
		assertThat(results, hasSize(5));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(502l, 503l, 504l, 505l, 506l));

	}

	@Test
	public void TestPrimitiveTrueFalse() throws SQLException {
		String qType = "Basictype";
		String propName = "propboolean";

		FleximsDynamicEntityImpl entity;

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		Connection connection = em.unwrap(Connection.class);
		connection.createStatement().execute("DELETE FROM BASICTYPE");
		tx.commit();
		tx.begin();
		for (int i = 500; i <= 503; i++) {
			entity = JpaHelper.createNewEntity(em, qType);
			if (i >= 504) {
				entity.set("shortstring", "req" + (i + 10));
			} else {
				entity.set("shortstring", "req" + i);
			}
			if (i % 2 == 0) {
				entity.set(propName, false);
			} else {
				entity.set(propName, true);
			}
			entity.setId(Long.valueOf(i));
			em.persist(entity);
		}
		tx.commit();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.checked);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 503l));

		results = performSimpleQuery(qType, propName, Operator.unchecked);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 502l));
	}

	@Test
	public void TestPrimitiveMultiInts() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiInt();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(5));
		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(5));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 501l, 502l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(2));
		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.eq, 5000);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 504l));

		results = performSimpleQuery(qType, propName, Operator.ne, 5000);
		assertThat(results, hasSize(5));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 501l, 502l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.size, 2);
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l, 504l));
	}

	@Test
	public void TestPrimitiveMultiStr() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStr();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(5));
		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(5));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l, 501l, 504l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(2));
		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.like, "kk502%");
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(502l));

		results = performIgnoreCaseLike(qType, propName, Operator.notlike, "kk502%");
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 501l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.notlike, "kk502%");
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 501l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.size, 2);
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l, 504l));
	}

	@Test
	public void TestRelationOnetoOneOwn() throws SQLException {
		String qType = "Mstudent";
		String propName = "doomroom";
		String relatedTypeString = "Mdoomroom";
		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(4));

		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(505)));

		DynamicEntity rInstance = em.find(JpaHelper.getEntityClass(em, relatedTypeString), 501l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(502)));

		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(501));
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(502)));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 503l, 504l, 505l));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "_1");
		assertThat(results, hasSize(2));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "Name", Operator.like, propName, traceDown, "k%", "_1");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(504)));
	}

	@Test
	public void TestRelationOnetoOneReverse() throws SQLException {
		String qType = "Mdoomroom";
		String propName = "student";
		String relatedTypeString = "Mstudent";

		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(4));

		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(505)));

		DynamicEntity rInstance = em.find(JpaHelper.getEntityClass(em, relatedTypeString), 501l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(502)));

		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(501));
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(502)));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 503l, 504l, 505l));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "k%");
		assertThat(results, hasSize(2));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "name", Operator.like, propName, traceDown, "_1", "k%");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(504)));
	}

	@Test
	public void TestRelationManytoMany() throws SQLException {
		String qType = "Mstudent";
		String propName = "Courses";
		String relatedTypeString = "Mcourse";

		setManyToMany();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(3));
		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(3));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(1));
		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(704)));

		DynamicEntity rInstance = em.find(JpaHelper.getEntityClass(em, relatedTypeString), 502l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l));

		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(502));
		assertThat(results, hasSize(2));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		results = performSimpleQuery(qType, propName, Operator.size, 2);
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "many2%");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(703l, 702l));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "Name", Operator.like, propName, traceDown, "%2", "many2%");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(703l)));
	}

	@Test
	public void TestCollectionModeOneofRelationMany() throws SQLException {
		String qType = "Mstudent";
		String propName = "Courses";
		String relatedTypeString = "Mcourse";

		setManyToMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.oneof, "502,503");
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		List<FleximsDynamicEntityImpl> rs = new ArrayList<FleximsDynamicEntityImpl>(2);
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 502l));
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 503l));

		results = performSimpleQuery(qType, propName, Operator.oneof, rs);
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		ConditionQuery query = createSimpleQuery(qType, propName, Operator.oneof);
		PropertyCondition propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, "502,503");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(702l, 704l));

		query = createSimpleQuery(qType, propName, Operator.oneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, "502,501");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(703l, 704l));

	}

	@Test
	public void TestCollectionModenNotOneofRelationMany() throws SQLException {
		String qType = "Mstudent";
		String propName = "Courses";
		String relatedTypeString = "Mcourse";

		setManyToMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notoneof, "502,503");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 703l));

		List<FleximsDynamicEntityImpl> rs = new ArrayList<FleximsDynamicEntityImpl>(2);
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 502l));
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 503l));

		results = performSimpleQuery(qType, propName, Operator.notoneof, rs);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 703l));

		ConditionQuery query = createSimpleQuery(qType, propName, Operator.notoneof);
		PropertyCondition propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, "502,501");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(703l, 704l));

		query = createSimpleQuery(qType, propName, Operator.notoneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, "502,501");
		assertThat(results, hasSize(2));
	}

	@Test
	public void TestCollectionModeOneofRelationOne() throws SQLException {
		String qType = "Mstudent";
		String propName = "doomroom";
		String relatedTypeString = "Mdoomroom";
		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.oneof, "502,503");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 503l));

		List<FleximsDynamicEntityImpl> rs = new ArrayList<FleximsDynamicEntityImpl>(2);
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 502l));
		rs.add((FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 503l));

		results = performSimpleQuery(qType, propName, Operator.oneof, rs);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 503l));

		results = performSimpleQuery(qType, propName, Operator.notoneof, "502,503");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(502l, 504l));

	}

	@Test
	public void TestRealtionManytoManyInverse() throws SQLException {
		String qType = "Mcourse";
		String propName = "Students";
		String relatedTypeString = "Mstudent";

		setManyToMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(4));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 502l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(505)));

		FleximsDynamicEntityImpl rInstance = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 701l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 502l));

		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(701));
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(501l, 502l));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(502l, 503l, 504l));

		results = performSimpleQuery(qType, propName, Operator.size, 2);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(502l, 503l));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "%2");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(503l, 504l));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "name", Operator.like, propName, traceDown, "%1", "%2");
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(504l));
	}

	/*
	 * #==Relation:many-tone, with relation table, relation is in one -side; it
	 * is a collection query for many-to-one, relation is in one side:empty
	 * query for many-to-one, relation is in one side:no empty query for
	 * many-to-one, relation is in one side:contains query for many-to-one,
	 * relation is in one side:not contains query for many-to-one, relation is
	 * in one side:number of elements query for many-to-one, relation is in one
	 * side:trace down
	 */

	@Test
	public void TestRelationOneManyTable() throws SQLException {
		String qType = "Mstudent";
		String propName = "OneManys";
		String relatedTypeString = "MOneMany";

		setOneManyTable();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l, 602l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(603l)));

		FleximsDynamicEntityImpl rInstance = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 501l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));

		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(501));
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l, 602l));

		results = performSimpleQuery(qType, propName, Operator.size, 3);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "%2");
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "Name", Operator.like, propName, traceDown, "%11", "%1");
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(602l));
	}

	/*
	 * #==Relation:many-to-one, without relation table, relation is in many
	 * side, inverse side is collection query for many-to-one, relation is in
	 * many side, inverse side:empty query for many-to-one, relation is in many
	 * side, inverse side:no empty query for many-to-one, relation is in many
	 * side, inverse side:contains query for many-to-one, relation is in many
	 * side, inverse side:not contains query for many-to-one, relation is in
	 * many side, inverse side:number of elements query for many-to-one,
	 * relation is in many side, inverse side:trace down
	 */
	@Test
	public void TestRelationOneManyInverse() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "rooms";
		String relatedTypeString = "Mdoomroom";
		setOneMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l, 602l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(603l, 604l));

		FleximsDynamicEntityImpl rInstance = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 701l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));
		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(701));
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l, 602l));

		results = performSimpleQuery(qType, propName, Operator.size, 2);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(new Long(601l)));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "jason1%");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l, 602l));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "name", Operator.eq, propName, traceDown, "main1", "jason1%");
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(601l));
	}

	/*
	 * 
	 * #==Relation:many-to-one, without relation table, relation is in many
	 * side: essentially, it is one to one query for many-to-one, relation is in
	 * many side, owning side:has value query for many-to-one, relation is in
	 * many side, owning side:no value query for many-to-one, relation is in
	 * many side, owning side:equal query for many-to-one, relation is in many
	 * side, owning side:not equal query for many-to-one, relation is in many
	 * side, owning side:trace down
	 */

	@Test
	public void TestRelationOneMany() throws SQLException {
		String qType = "Mdoomroom";
		String propName = "doombuild";
		String relatedTypeString = "Mdoombuild";
		setOneMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;

		results = performSimpleQuery(qType, propName, Operator.notnull);
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		results = performSimpleQuery(qType, propName, Operator.isnull);
		assertThat(results, hasSize(1));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(704l));

		FleximsDynamicEntityImpl rInstance = (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, relatedTypeString), 601l);
		// can query by instance
		results = performSimpleQuery(qType, propName, Operator.eq, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 703l));
		// can query by id
		results = performSimpleQuery(qType, propName, Operator.eq, new Long(601));
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 703l));

		results = performSimpleQuery(qType, propName, Operator.ne, rInstance);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(702l, 704l));

		// tracedown
		// create s tracedown query
		ConditionQuery traceDown = createSimpleQuery(relatedTypeString, "name", Operator.like);
		saveQuery(traceDown);
		results = performTraceDown(qType, propName, traceDown, "main1%");
		assertThat(results, hasSize(3));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(701l, 702l, 703l));

		// tracedown and a simple
		results = performTraceDownAndSimple(qType, "name", Operator.like, propName, traceDown, "jason1_", "main1%");
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(702l, 703l));
	}

	@Test
	public void testCollectionModeGt() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.gt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 503l));

		query = createSimpleQuery(qType, propName, Operator.gt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(503l));

		query = createSimpleQuery(qType, propName, Operator.gt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(500l, 502l));

	}

	@Test
	public void testCollectionModeLt() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.lt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 502l));

		query = createSimpleQuery(qType, propName, Operator.lt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(500l, 502l));

		query = createSimpleQuery(qType, propName, Operator.lt);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 6000);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(503l));
	}

	@Test
	public void testCollectionModeLe() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.le);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 502l));

		query = createSimpleQuery(qType, propName, Operator.le);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(500l));

		query = createSimpleQuery(qType, propName, Operator.le);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(503l));
	}

	@Test
	public void testCollectionModeGe() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.ge);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(503l, 501l, 502l));

		query = createSimpleQuery(qType, propName, Operator.ge);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.ge);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(500l));
	}

	@Test
	public void testCollectionModeBetween() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.between);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 6010, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(503l, 501l));

		query = createSimpleQuery(qType, propName, Operator.between);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 6010, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(503l));

		query = createSimpleQuery(qType, propName, Operator.between);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 6010, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(500l, 502l));
	}

	@Test
	public void testCollectionModeNotBetween() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.notbetween);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 6031, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 502l));

		query = createSimpleQuery(qType, propName, Operator.notbetween);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 6010, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(500l, 502l));

		query = createSimpleQuery(qType, propName, Operator.notbetween);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 6010, 7030);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(503l));
	}

	@Test
	public void testCollectionModeEqInteger() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(1));
		assertThat(ids, containsInAnyOrder(502l));

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(0));

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 503l));
	}

	@Test
	public void testCollectionModeNeInteger() throws SQLException {
		String qType = "Collection1";
		String propName = "propint";

		setMultiIntCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(4));

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(3));

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		results = runQuery(query, 5020);
		ids = getIds(results);
		assertThat(results, hasSize(0));
	}

	@Test
	public void testCollectionModeEqStr() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);

		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 504l));

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));

		query = createSimpleQuery(qType, propName, Operator.eq);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(502l, 503l));
	}

	@Test
	public void testCollectionModeNeStr() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");

		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.ne);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));
	}

	@Test
	public void testCollectionModeCount() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.like);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);

		assertThat(getCount(query, "jj500%"), equalTo(3)); // element is not
															// count multiple
															// time

	}

	@Test
	public void testCollectionModeLike() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.like);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");
		ids = getIds(results);
		assertThat(results, hasSize(5));

		query = createSimpleQuery(qType, propName, Operator.like);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");

		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));

		query = createSimpleQuery(qType, propName, Operator.like);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");
		ids = getIds(results);
		assertThat(results, hasSize(0));

	}

	@Test
	public void testCollectionModeNotLike() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.notlike);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.notlike);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");

		ids = getIds(results);
		assertThat(results, hasSize(0));

		query = createSimpleQuery(qType, propName, Operator.notlike);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "%1");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));

	}

	@Test
	public void testCollectionModeOneOf() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.oneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 501l, 504l));

		query = createSimpleQuery(qType, propName, Operator.oneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");

		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));

		query = createSimpleQuery(qType, propName, Operator.oneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(502l, 503l));

	}

	@Test
	public void testCollectionModeNotOneOf() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStrCollectionMode();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		query = createSimpleQuery(qType, propName, Operator.notoneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setSomeColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(3));
		assertThat(ids, containsInAnyOrder(500l, 502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.notoneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setAllColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");

		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(502l, 503l));

		query = createSimpleQuery(qType, propName, Operator.notoneof);
		propertyCondition = (PropertyCondition) query.getConditions().get(0);
		propertyCondition.setNoneColllectionMode();
		propertyCondition.setIgnoreCase(true);
		results = runQuery(query, "jj5001");
		ids = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(ids, containsInAnyOrder(501l, 504l));

	}

	@Test
	public void testSimpleCollectionSize() throws SQLException {
		String qType = "Collection1";
		String propName = "shortstring";
		setMultiStr();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query = null;
		PropertyCondition propertyCondition = null;

		results = performSimpleQuery(qType, propName, Operator.sizeGt, 2);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(501l));

		results = performSimpleQuery(qType, propName, Operator.size, 3);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(501l));

		results = performSimpleQuery(qType, propName, Operator.sizeLt, 2);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.empty);
		assertThat(results, hasSize(2));
		ids = getIds(results);
		assertThat(ids, containsInAnyOrder(505l, 506l));

		results = performSimpleQuery(qType, propName, Operator.notempty);
		assertThat(results, hasSize(5));

	}

	@Test
	public void testRelationTraceDownAll() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		setOneManyTrace();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;

		ConditionQuery studentname = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		saveQuery(studentname);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentname);
		pc.setAllColllectionMode(); // A build with all its students's name end
									// with 2. room 603, students 6, 7,
		results = runQuery(query, "%2");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(603l));

		// two condition.
		ConditionQuery studnetnameAndumber = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		// add DateCrate Condition.
		PropertyCondition roomNumber = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		roomNumber.setProp("number");
		roomNumber.setOperator(Operator.gt);
		studnetnameAndumber.getConditions().add(roomNumber);
		saveQuery(studnetnameAndumber);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studnetnameAndumber);
		pc.setAllColllectionMode();
		// A build with all its students's name end with 2. room 603, students
		// 6, 7,
		// and student number > 6,
		// No such build
		results = runQuery(query, "%2", 6);
		assertThat(results, hasSize(0));

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studnetnameAndumber);
		pc.setAllColllectionMode();
		results = runQuery(query, "%2", 5);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(603l));

		// ##################nested All collection mode
		// Create Nested TraceDown query:all OneManyManyMany has third Name like
		ConditionQuery courseName = createSimpleQuery("Mcourse", "name", Operator.like);
		saveQuery(courseName);

		// trace down to thirdName
		ConditionQuery studentCourseAndStudentName = createSimpleQuery("Mstudent", "Courses", Operator.tracedown);
		PropertyCondition m2TraceDownCondition = (PropertyCondition) studentCourseAndStudentName.getConditions().get(0);
		m2TraceDownCondition.setTracceDown(courseName);
		m2TraceDownCondition.setAllColllectionMode();

		// add a ManyNumber to m2
		PropertyCondition studentName = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		studentName.setProp("Name");
		studentName.setOperator(Operator.like);
		studentCourseAndStudentName.getConditions().add(studentName);
		saveQuery(studentCourseAndStudentName);

		// final trace down: find build with all students in with name ends with
		// 2 and all those students takes courses starts with many 4%
		// first all student with name ends with 2: build 603, all courses take
		// bu students start with many4
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentCourseAndStudentName);
		pc.setAllColllectionMode();
		results = runQuery(query, "many4%", "%2");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(603l));

	}

	@Test
	public void testRelationTraceDownComplexSubQueryForOne() throws SQLException {
		String qType = "Mdoomroom";
		String propName = "student";
		String relatedTypeString = "Mstudent";

		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;
		ConditionQuery subQuery = null;
		PropertyCondition roomname = null;

		// all subquery
		subQuery = createSimpleQuery(relatedTypeString, "propint", Operator.gt);
		pc = subQuery.getConditions().get(0);
		pc.setAllColllectionMode();
		saveQuery(subQuery);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = query.getConditions().get(0);
		pc.setTracceDown(subQuery);

		roomname = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		roomname.setProp("name");
		roomname.setOperator(Operator.like);
		query.getConditions().add(roomname);

		// room with name start with 2, and with student with ALL propint >3.
		results = runQuery(query, 3, "2%");
		idsList = getIds(results);
		assertThat(results, hasSize(2));
		assertThat(idsList, containsInAnyOrder(503l, 504l));

		// some subquery
		subQuery = createSimpleQuery(relatedTypeString, "propint", Operator.gt);
		pc = subQuery.getConditions().get(0);
		pc.setSomeColllectionMode();
		saveQuery(subQuery);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = query.getConditions().get(0);
		pc.setTracceDown(subQuery);

		roomname = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		roomname.setProp("name");
		roomname.setOperator(Operator.like);
		query.getConditions().add(roomname);

		// room with name start with 2, and with student with SOME propint >3.
		results = runQuery(query, 3, "2%");
		assertThat(results, hasSize(1));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(503l));

		// none of subquery
		subQuery = createSimpleQuery(relatedTypeString, "propint", Operator.gt);
		pc = subQuery.getConditions().get(0);
		pc.setNoneColllectionMode();
		saveQuery(subQuery);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = query.getConditions().get(0);
		pc.setTracceDown(subQuery);

		roomname = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), query);
		roomname.setProp("name");
		roomname.setOperator(Operator.like);
		query.getConditions().add(roomname);

		// room with name start with 2, and with student with NONE propint >3.
		results = runQuery(query, 3, "1%");
		assertThat(results, hasSize(1));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(502l));

	}

	@Test
	public void testRelationTraceDownComplexSubQueryForMany() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		setOneManyTrace();
		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;
		ConditionQuery studentNameAndStudentProptin = null;
		PropertyCondition datePcCondition = null;

		// Query on student
		// 1: name like
		// 2. all propint (a collection) >
		studentNameAndStudentProptin = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		datePcCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), studentNameAndStudentProptin);
		datePcCondition.setProp("propint");
		datePcCondition.setOperator(Operator.gt);
		datePcCondition.setAllColllectionMode();
		studentNameAndStudentProptin.getConditions().add(datePcCondition);
		saveQuery(studentNameAndStudentProptin);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentNameAndStudentProptin);
		pc.setAllColllectionMode(); // ALL COllection mode.
		results = runQuery(query, "%2", "30");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(603l));

		// NONE tracedown
		studentNameAndStudentProptin = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		datePcCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), studentNameAndStudentProptin);
		datePcCondition.setProp("propint");
		datePcCondition.setOperator(Operator.gt);
		datePcCondition.setAllColllectionMode();
		studentNameAndStudentProptin.getConditions().add(datePcCondition);
		saveQuery(studentNameAndStudentProptin);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentNameAndStudentProptin);
		pc.setNoneColllectionMode(); // NONE collection mode.
		results = runQuery(query, "%2", "30");
		assertThat(results, hasSize(1));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(601l));

		// Some Tracedown
		studentNameAndStudentProptin = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		datePcCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME), studentNameAndStudentProptin);
		datePcCondition.setProp("propint");
		datePcCondition.setOperator(Operator.gt);
		datePcCondition.setAllColllectionMode();
		studentNameAndStudentProptin.getConditions().add(datePcCondition);
		saveQuery(studentNameAndStudentProptin);

		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentNameAndStudentProptin);
		pc.setSomeColllectionMode(); // SOME collection mode
		results = runQuery(query, "%2", "30");
		assertThat(results, hasSize(2));
		idsList = getIds(results);
		assertThat(idsList, containsInAnyOrder(602l, 603l));

	}

	@Test
	public void testRelationTraceDownNone() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		setOneManyTrace();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;

		ConditionQuery studentName = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		saveQuery(studentName);

		// No students with name ending with %2: build 601, student 1, 2,3,
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentName);
		pc.setNoneColllectionMode(); // NONE of student with name ends with 2.
		results = runQuery(query, "%2");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(601l));

		// two condition: name like and number is geater.
		ConditionQuery studentNameAndStudentNumber = createSimpleQuery(relatedTypeString, "Name", Operator.like);
		// add DateCrate Condition.
		PropertyCondition datePcCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME),
				studentNameAndStudentNumber);
		datePcCondition.setProp("number");
		datePcCondition.setOperator(Operator.gt);
		studentNameAndStudentNumber.getConditions().add(datePcCondition);
		saveQuery(studentNameAndStudentNumber);

		// build with none student
		// (name end %2 and number 2: student 5,6,7)
		// build 601: does not have 5,6,7
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentNameAndStudentNumber);
		pc.setNoneColllectionMode(); // NONE mode.
		results = runQuery(query, "%2", 2);
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(601l));
		idsList = getIds(results);

		// ##################nested All collection mode
		// Create Nested TraceDown query:all OneManyManyMany has third Name like
		ConditionQuery courseName = createSimpleQuery("Mcourse", "name", Operator.like);
		saveQuery(courseName);

		// student with none course like
		ConditionQuery studentCourseAndStudentName = createSimpleQuery("Mstudent", "Courses", Operator.tracedown);
		PropertyCondition m2TraceDownCondition = (PropertyCondition) studentCourseAndStudentName.getConditions().get(0);
		m2TraceDownCondition.setTracceDown(courseName);
		m2TraceDownCondition.setNoneColllectionMode();

		// student with name like
		PropertyCondition studentName1 = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME),
				studentCourseAndStudentName);
		studentName1.setProp("Name");
		studentName1.setOperator(Operator.like);
		studentCourseAndStudentName.getConditions().add(studentName1);
		saveQuery(studentCourseAndStudentName);

		// build with none of student
		// (name ends with %2(student 5,6,7), and (none of the course ends with
		// %4: student 1-6)
		// So the student satisfies these two conditions are 5 and 6
		// Which build does no contain 5, 6?: build 601
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentCourseAndStudentName);
		pc.setNoneColllectionMode();
		results = runQuery(query, "%4", "%2");
		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(601l));

	}

	@Test
	public void testRelationTraceDownSomeAll() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		setOneManyTrace();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;

		// ##################nested All collection mode
		// Create Nested TraceDown query:all OneManyManyMany has third Name like
		ConditionQuery courseName = createSimpleQuery("Mcourse", "name", Operator.like);
		saveQuery(courseName);

		// student with all course like
		ConditionQuery studentCourseAndName = createSimpleQuery("Mstudent", "Courses", Operator.tracedown);
		PropertyCondition m2TraceDownCondition = (PropertyCondition) studentCourseAndName.getConditions().get(0);
		m2TraceDownCondition.setTracceDown(courseName);
		m2TraceDownCondition.setAllColllectionMode();

		// add a ManyNumber to m2
		PropertyCondition m2ManyNumberCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME),
				studentCourseAndName);
		m2ManyNumberCondition.setProp("Name");
		m2ManyNumberCondition.setOperator(Operator.like);
		studentCourseAndName.getConditions().add(m2ManyNumberCondition);

		saveQuery(studentCourseAndName);
		// final trace down
		// find build wiht some student.
		// 1. name end with %2.
		// 2. all courses starts with many4.
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentCourseAndName);
		pc.setSomeColllectionMode();
		results = runQuery(query, "many4%", "%2");

		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(603l));

	}

	@Test
	public void testRelationTraceDownSomeNone() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "students";
		String relatedTypeString = "Mstudent";

		setOneManyTrace();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = null;
		PropertyCondition pc = null;

		// ##################nested All collection mode
		// Create Nested TraceDown query:all OneManyManyMany has third Name like
		ConditionQuery courseName = createSimpleQuery("Mcourse", "name", Operator.like);
		saveQuery(courseName);

		// student with all course like
		ConditionQuery studentCourseAndName = createSimpleQuery("Mstudent", "Courses", Operator.tracedown);
		PropertyCondition m2TraceDownCondition = (PropertyCondition) studentCourseAndName.getConditions().get(0);
		m2TraceDownCondition.setTracceDown(courseName);
		m2TraceDownCondition.setNoneColllectionMode();

		// add a ManyNumber to m2
		// add a ManyNumber to m2
		PropertyCondition m2ManyNumberCondition = new PropertyCondition(JpaHelper.createNewEntity(em, PropertyCondition.TYPE_NAME),
				studentCourseAndName);
		m2ManyNumberCondition.setProp("Name");
		m2ManyNumberCondition.setOperator(Operator.like);
		studentCourseAndName.getConditions().add(m2ManyNumberCondition);

		saveQuery(studentCourseAndName);
		// final trace down
		// find build wiht some student.
		// 1. name end with %2.
		// 2. none of courses starts with many4.
		query = createSimpleQuery(qType, propName, Operator.tracedown);
		pc = (PropertyCondition) query.getConditions().get(0);
		pc.setTracceDown(studentCourseAndName);
		pc.setSomeColllectionMode();
		results = runQuery(query, "many4%", "%2");

		assertThat(results, hasSize(1));
		assertThat(results.get(0).getId(), equalTo(602l));

	}

}
