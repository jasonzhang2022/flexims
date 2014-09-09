package com.flexdms.flexims.unit.query;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.query.ConditionQuery;
import com.flexdms.flexims.query.Operator;
import com.flexdms.flexims.query.OrderDirection;
import com.flexdms.flexims.query.OrderField;
import com.flexdms.flexims.query.QueryContext;
import com.flexdms.flexims.unit.rs.report.helper.QueryUtil;

@RunWith(JUnit4.class)
public class TestQueryFeature extends QueryTestBase {

	// -----------------order by
	@Test
	public void testSorting() throws SQLException {
		String qType = "Basictype";
		String propName = "propint";
		setupsingleInt();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = QueryUtil.createSimpleQuery(qType, propName, Operator.gt, em);
		QueryContext queryContext = new QueryContext();

		OrderField oField = new OrderField(propName, OrderDirection.DESC);
		List<OrderField> orderFields = new ArrayList<>(1);
		orderFields.add(oField);
		queryContext.setOrderFields(orderFields);
		query.setQueryContext(queryContext);
		results = runQuery(query, 500);
		idsList = getIds(results);
		assertThat(results, hasSize(6));
		assertThat(idsList, contains(506l, 505l, 504l, 503l, 502l, 501l));
	}

	// fetch group for simple attributes
	@Test
	public void testFetchGroupSimple() throws SQLException {
		String qType = "Basictype";
		String propName = "propint";
		setupsingleInt();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;

		ConditionQuery query = createSimpleQuery(qType, propName, Operator.gt);
		QueryContext queryContext = new QueryContext();
		queryContext.setProps("propint");
		query.setQueryContext(queryContext);
		results = runQuery(query, 500);
		idsList = getIds(results);
		assertThat(results, hasSize(6));

		FleximsDynamicEntityImpl de = results.get(0);
		assertNotNull(de.get("propint"));
		assertNull(de.get("shortstring"));
	}

	@Test
	@Ignore
	public void testFetchGroupEmbedded() throws SQLException {
		String qType = "Embedmain";
		String propName = "fname";
		FleximsDynamicEntityImpl main = setupEmbedmain();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> idsList = null;
		ConditionQuery query;
		QueryContext queryContext;

		// do not load embedded
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("fname");
		query.setQueryContext(queryContext);
		results = runQuery(query);
		idsList = getIds(results);

		FleximsDynamicEntityImpl de = results.get(0);
		assertNotNull(de.get("fname"));
		List multiembed = de.get("multiembed");
		assertTrue(multiembed == null || multiembed.isEmpty());

		FleximsDynamicEntityImpl singleembed = de.get("singleembed");
		assertNull(singleembed);

		// load whole property
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("fname", "singleembed", "multiembed");
		query.setQueryContext(queryContext);
		results = runQuery(query);
		idsList = getIds(results);

		de = results.get(0);
		assertNotNull(de.get("fname"));
		multiembed = de.get("multiembed");
		assertNotNull(multiembed);
		assertTrue(multiembed.size() > 1);
		FleximsDynamicEntityImpl firstmDynamicEntityImpl = (FleximsDynamicEntityImpl) multiembed.get(0);
		assertNotNull(firstmDynamicEntityImpl.get("streetAddress"));

		singleembed = de.get("singleembed");
		assertNotNull(singleembed);
		assertNotNull(singleembed.get("streetAddress"));
		List list = singleembed.get("mint");
		assertTrue(!list.isEmpty());

		// nested---------not supported by Eclipselink right now
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("fname", "singleembed.streetAddress", "multiembed.streetAddress");
		query.setQueryContext(queryContext);
		results = runQuery(query);
		idsList = getIds(results);

		de = results.get(0);
		assertNotNull(de.get("fname"));

		singleembed = de.get("singleembed");
		assertNotNull(singleembed);
		assertNotNull(singleembed.get("streetAddress"));
		list = singleembed.get("mint");
		assertTrue(list.isEmpty());

		multiembed = de.get("multiembed");
		assertNotNull(multiembed);
		assertTrue(multiembed.size() > 1);
		firstmDynamicEntityImpl = (FleximsDynamicEntityImpl) multiembed.get(0);
		assertNotNull(firstmDynamicEntityImpl.get("streetAddress"));
		assertNull(firstmDynamicEntityImpl.get("extra1"));

	}

	@Test
	public void testFetchGroupRelationOneOne() throws SQLException {
		String qType = "Mstudent";
		String propName = "doomroom";
		String relatedTypeString = "Mdoomroom";
		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query;
		QueryContext queryContext;
		Object nameObject;
		Object numObject;
		FleximsDynamicEntityImpl de, room;

		// -------------------related entity is not loaded if not specified
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("Name");
		query.setQueryContext(queryContext);
		results = runQuery(query);
		assertThat(results, hasSize(4));

		de = results.get(0);
		assertNotNull(de.get("Name"));
		List pintsList = de.get("propint");
		assertTrue(pintsList == null || pintsList.isEmpty());
		assertNull(de.get("doomroom"));

		// related attributes is loaed if specified
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("Name", "doomroom");
		query.setQueryContext(queryContext);
		query.buildQuery(em);
		// query.getReadAllQuery().getFetchGroup().setShouldLoadAll(true);
		results = query.fetchAllResult(em);
		assertThat(results, hasSize(4));

		de = results.get(0);
		room = de.get("doomroom");
		assertNotNull(room);
		nameObject = room.get("name");
		numObject = room.get("number");
		assertNotNull(nameObject);
		assertNotNull(numObject);
	}

	// -------------nested property in related item.
	@Test
	public void testFetchGroupRelationOneOneNested() throws SQLException {
		String qType = "Mstudent";
		String propName = "doomroom";
		String relatedTypeString = "Mdoomroom";
		setOne2One();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query;
		QueryContext queryContext;
		Object nameObject;
		Object numObject;
		FleximsDynamicEntityImpl de, room;
		// only specified sub attribute is exposed
		nameObject = null;
		numObject = null;
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("Name", "doomroom.name");
		query.setQueryContext(queryContext);
		query.buildQuery(em);

		results = query.fetchAllResult(em);
		assertThat(results, hasSize(4));

		de = results.get(0);
		room = de.get("doomroom");
		assertNotNull(room);
		nameObject = room.get("name");
		numObject = room.get("number");
		Object buildObject = room.get("doombuild");
		assertNotNull(nameObject);
		assertNull(numObject);
		assertNull(buildObject);

	}

	@Test
	public void testFetchGroupOneMany() throws SQLException {
		String qType = "Mdoombuild";
		String propName = "rooms";
		setOneMany();

		List<FleximsDynamicEntityImpl> results = null;
		List<Long> ids = null;
		ConditionQuery query;
		QueryContext queryContext;

		// no rooms
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("name");
		query.setQueryContext(queryContext);
		query.buildQuery(em);

		results = query.fetchAllResult(em);
		FleximsDynamicEntityImpl de = results.get(0);

		assertNotNull(de.get("name"));
		List rooms = de.get("rooms");
		assertTrue(rooms.isEmpty());

		// --------------with rooms
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("name", "rooms");
		query.setQueryContext(queryContext);
		query.buildQuery(em);

		results = query.fetchAllResult(em);
		de = results.get(0);
		assertNotNull(de.get("name"));
		rooms = de.get("rooms");
		assertFalse(rooms.isEmpty());
		FleximsDynamicEntityImpl room = (FleximsDynamicEntityImpl) rooms.get(0);
		assertNotNull(room.get("name"));

		// -------------room with limited property
		query = createSimpleQuery(qType, propName, Operator.notnull);
		queryContext = new QueryContext();
		queryContext.setProps("name", "rooms.name");
		query.setQueryContext(queryContext);
		query.buildQuery(em);

		results = query.fetchAllResult(em);
		de = results.get(0);
		assertNotNull(de.get("name"));
		rooms = de.get("rooms");
		assertFalse(rooms.isEmpty());
		room = (FleximsDynamicEntityImpl) rooms.get(0);
		assertNotNull(room.get("name"));
		assertNull(room.get("number"));
		assertNull(room.get("doombuild"));

	}
}
