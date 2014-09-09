package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

public class TestManyToManyOrderMetaSource extends XMLMetadataSource {

	static XMLEntityMappings entityMaps;

	public static void buildMap() {
		entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

		EntityAccessor test1 = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test1");
		MetaSourceBuilder.addID(test1, entityMaps);
		MetaSourceBuilder.addVersion(test1);
		MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");

		MetaSourceBuilder.addManyToMany(test1, test, "tests", null);
		MetaSourceBuilder.addManyToMany(test, test1, "test1s", "tests");

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());
	}

	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		return entityMaps;
	}

	public static void addOrderbyId() {
		EntityAccessor test1 = entityMaps.getEntities().get(1);

		// relationship is owned by test1
		ManyToManyAccessor testsinTest1 = test1.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		testsinTest1.setOrderBy(orderByMetadata);

	}

	public static void addOrderbyIndex() {
		EntityAccessor test1 = entityMaps.getEntities().get(1);

		// relationship is owned by test1
		ManyToManyAccessor testsinTest1 = test1.getAttributes().getManyToManys().get(0);

		PropertyMetadata propertyMetadata = new PropertyMetadata();
		propertyMetadata.setName("orderColumn");
		propertyMetadata.setValue("true");
		testsinTest1.getProperties().add(propertyMetadata);

	}
	
	public static void addOrderbyfnameAsc() {
		EntityAccessor test1 = entityMaps.getEntities().get(1);

		// relationship is owned by test1
		ManyToManyAccessor testsinTest1 = test1.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		orderByMetadata.setValue("fname ASC");
		testsinTest1.setOrderBy(orderByMetadata);


	}
	public static void addOrderbyfnameDesc() {
		EntityAccessor test1 = entityMaps.getEntities().get(1);

		// relationship is owned by test1
		ManyToManyAccessor testsinTest1 = test1.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		orderByMetadata.setValue("fname DESC");
		testsinTest1.setOrderBy(orderByMetadata);


	}
	
	public static void addInverseOrderbyId() {
		EntityAccessor test = entityMaps.getEntities().get(0);

		// relationship is owned by test1
		ManyToManyAccessor test1sintest = test.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		test1sintest.setOrderBy(orderByMetadata);

	}

	public static void addInverseOrderbyfnameAsc() {
		EntityAccessor test = entityMaps.getEntities().get(0);
		ManyToManyAccessor test1sintest = test.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		orderByMetadata.setValue("fname ASC");
		test1sintest.setOrderBy(orderByMetadata);


	}
	public static void addInverseOrderbyfnameDesc() {
		EntityAccessor test = entityMaps.getEntities().get(0);

		// relationship is owned by test1
		ManyToManyAccessor test1sintest = test.getAttributes().getManyToManys().get(0);

		OrderByMetadata orderByMetadata = new OrderByMetadata();
		orderByMetadata.setValue("fname DESC");
		test1sintest.setOrderBy(orderByMetadata);


	}
}
