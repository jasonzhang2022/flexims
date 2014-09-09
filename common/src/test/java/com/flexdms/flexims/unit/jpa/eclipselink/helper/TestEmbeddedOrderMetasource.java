package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.io.StringWriter;
import java.util.Map;

import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.OrderByMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.jpa.metadata.XMLMetadataSource;
import org.eclipse.persistence.logging.SessionLog;

public class TestEmbeddedOrderMetasource extends XMLMetadataSource {


	@Override
	public XMLEntityMappings getEntityMappings(Map<String, Object> properties, ClassLoader classLoader, SessionLog log) {

		XMLEntityMappings entityMaps = MetaSourceBuilder.createEmptyEntityMappings();
		EntityAccessor test = MetaSourceBuilder.createEntityAccessor(entityMaps, "Test");
		MetaSourceBuilder.addID(test, entityMaps);
		MetaSourceBuilder.addVersion(test);
		MetaSourceBuilder.AddBasicProperty(test, "fname", "String");
		MetaSourceBuilder.AddBasicProperty(test, "lname", "String");

		EmbeddableAccessor test1 = MetaSourceBuilder.createEmbeddableAccessor(entityMaps, "Test1");
		BasicAccessor basicAccessor = MetaSourceBuilder.AddBasicProperty(test1, "fname", "String");
		ColumnMetadata columnMetadata = new ColumnMetadata();
		columnMetadata.setName("test1_fname");
		basicAccessor.setColumn(columnMetadata);

		MetaSourceBuilder.addEmbeddedProperty(test, "test1", "Test1");

		EntityAccessor top = MetaSourceBuilder.createEntityAccessor(entityMaps, "Top");
		MetaSourceBuilder.addID(top, entityMaps);
		MetaSourceBuilder.addVersion(top);

		ManyToManyAccessor tests = MetaSourceBuilder.addManyToMany(top, test, "tests", null);
		OrderByMetadata orderByMetadata = new OrderByMetadata();
		orderByMetadata.setValue("test1.fname DESC");
		tests.setOrderBy(orderByMetadata);

		entityMaps = MetaSourceBuilder.roundTripEntityMappings(entityMaps);
		StringWriter writer = new StringWriter();
		MetaSourceBuilder.toXml(entityMaps, writer);
		System.out.println(writer.toString());
		return entityMaps;
	}
}
