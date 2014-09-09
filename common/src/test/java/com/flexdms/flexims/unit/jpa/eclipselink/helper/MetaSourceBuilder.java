package com.flexdms.flexims.unit.jpa.eclipselink.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.XMLAttributes;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicMapAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.ColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.columns.JoinColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.CascadeMetadata;
import org.eclipse.persistence.internal.jpa.metadata.structures.ArrayAccessor;
import org.eclipse.persistence.internal.jpa.metadata.tables.IndexMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.util.Utils;

public class MetaSourceBuilder extends com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder {
	public static EntityAccessor createEntityAccessor(XMLEntityMappings entityMaps, String name) {

		name = Utils.upperFirstLetter(name);

		// add Sequences

		EntityAccessor entity = new EntityAccessor();
		entity.setClassName(name);
		entity.setName(name);
		entity.setEntityName(name);
		entity.setParentClassName("com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl");

		XMLAttributes attributes = new XMLAttributes();
		entity.setAttributes(attributes);

		entity.setProperties(new ArrayList<PropertyMetadata>(1));
		// basic
		attributes.setVersions(new ArrayList<VersionAccessor>(1));
		attributes.setIds(new ArrayList<IdAccessor>(1));
		attributes.setBasics(new ArrayList<BasicAccessor>(10));
		// relation
		attributes.setManyToManys(new ArrayList<ManyToManyAccessor>(3));
		attributes.setManyToOnes(new ArrayList<ManyToOneAccessor>(2));
		attributes.setOneToOnes(new ArrayList<OneToOneAccessor>(2));
		attributes.setOneToManys(new ArrayList<OneToManyAccessor>(2));

		// collection
		attributes.setBasicCollections(new ArrayList<BasicCollectionAccessor>(2));
		attributes.setElementCollections(new ArrayList<ElementCollectionAccessor>(3));
		attributes.setArrays(new ArrayList<ArrayAccessor>(2));
		attributes.setBasicMaps(new ArrayList<BasicMapAccessor>(2));

		// embedded
		attributes.setEmbeddeds(new ArrayList<EmbeddedAccessor>());

		entityMaps.getEntities().add(entity);
		return entity;

	}

	public static EmbeddableAccessor createEmbeddableAccessor(XMLEntityMappings entityMaps, String name) {

		name = Utils.upperFirstLetter(name);

		EmbeddableAccessor entity = new EmbeddableAccessor();
		entity.setClassName(name);
		entity.setName(name);
		entity.setParentClassName("com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl");
		entity.setAccess("VIRTUAL");

		XMLAttributes attributes = new XMLAttributes();
		entity.setAttributes(attributes);

		// basic
		attributes.setVersions(new ArrayList<VersionAccessor>(1));
		attributes.setIds(new ArrayList<IdAccessor>(1));
		attributes.setBasics(new ArrayList<BasicAccessor>(10));
		// relation
		attributes.setManyToManys(new ArrayList<ManyToManyAccessor>(3));
		attributes.setManyToOnes(new ArrayList<ManyToOneAccessor>(2));
		attributes.setOneToOnes(new ArrayList<OneToOneAccessor>(2));
		attributes.setOneToManys(new ArrayList<OneToManyAccessor>(2));

		// collection
		attributes.setBasicCollections(new ArrayList<BasicCollectionAccessor>(2));
		attributes.setElementCollections(new ArrayList<ElementCollectionAccessor>(3));
		attributes.setArrays(new ArrayList<ArrayAccessor>(2));
		attributes.setBasicMaps(new ArrayList<BasicMapAccessor>(2));

		// embedded
		attributes.setEmbeddeds(new ArrayList<EmbeddedAccessor>());

		entityMaps.getEmbeddables().add(entity);
		return entity;
	}

	public static EmbeddedAccessor addEmbeddedProperty(ClassAccessor entity, String propName, String type) {
		EmbeddedAccessor accessor = new EmbeddedAccessor();
		accessor.setName(propName);
		accessor.setAttributeType(type);

		// avoid name duplication:

		entity.getAttributes().getEmbeddeds().add(accessor);
		return accessor;

	}

	/*
	 * Use element colleciton instead public static BasicCollectionAccessor
	 * createBasicCollection(ClassAccessor entity, String propName) {
	 * BasicCollectionAccessor accessor = new BasicCollectionAccessor();
	 * accessor.setName(propName); accessor.setAttributeType("java.uitl.List");
	 * 
	 * entity.getAttributes().getBasicCollections().add(accessor); return
	 * accessor;
	 * 
	 * }
	 */

	public static BasicMapAccessor createBasicMap(ClassAccessor entity, String propName) {
		BasicMapAccessor accessor = new BasicMapAccessor();
		accessor.setName(propName);
		accessor.setAttributeType("java.uitl.List");

		entity.getAttributes().getBasicMaps().add(accessor);
		return accessor;

	}

	public static BasicAccessor AddBasicProperty(ClassAccessor entity, String propName, String type) {
		BasicAccessor prop = new BasicAccessor();
		prop.setName(propName);
		prop.setAttributeType(type);

		prop.setColumn(new ColumnMetadata());
		entity.getAttributes().getBasics().add(prop);
		return prop;
	}

	public static ElementCollectionAccessor AddElementCollectionProperty(ClassAccessor entity, String propName, String type) {
		ElementCollectionAccessor prop = new ElementCollectionAccessor();
		prop.setName(propName);
		// set a default collection type
		prop.setAttributeType("java.util.Set");
		prop.setTargetClassName(type);
		prop.setColumn(new ColumnMetadata());
		entity.getAttributes().getElementCollections().add(prop);
		return prop;
	}

	private static RelationshipAccessor setBasicAccessor(RelationshipAccessor accessor, ClassAccessor from, ClassAccessor to, String propName,
			String mappedBy) {
		accessor.setName(propName);
		accessor.setTargetEntityName(to.getName());
		if (mappedBy != null) {
			accessor.setMappedBy(mappedBy);
		}
		return accessor;

	}

	public static ManyToOneAccessor addManyToOne(ClassAccessor from, ClassAccessor to, String propName) {
		ManyToOneAccessor accessor = new ManyToOneAccessor();
		setBasicAccessor(accessor, from, to, propName, null);

		from.getAttributes().getManyToOnes().add(accessor);
		return accessor;
	}

	public static OneToManyAccessor addOneToMany(ClassAccessor from, ClassAccessor to, String propName, String mappedBy) {
		OneToManyAccessor accessor = new OneToManyAccessor();
		setBasicAccessor(accessor, from, to, propName, mappedBy);
		accessor.setAttributeType("java.util.List");
		CascadeMetadata metadata = new CascadeMetadata();
		metadata.setCascadePersist(true);
		accessor.setCascade(metadata);
		from.getAttributes().getOneToManys().add(accessor);
		return accessor;
	}

	public static OneToOneAccessor addOneToOne(ClassAccessor from, ClassAccessor to, String propName, String mappedBy) {
		OneToOneAccessor accessor = new OneToOneAccessor();
		setBasicAccessor(accessor, from, to, propName, mappedBy);
		if (mappedBy == null) {
			// Owning entity. We need a unique index
			accessor.setOrphanRemoval(true);

			JoinColumnMetadata columnMetadata = new JoinColumnMetadata();
			columnMetadata.setName(to.getName() + "_ID");
			columnMetadata.setUnique(true);
			accessor.setJoinColumns(new ArrayList<JoinColumnMetadata>(1));
			accessor.getJoinColumns().add(columnMetadata);
		}

		from.getAttributes().getOneToOnes().add(accessor);
		return accessor;
	}

	public static ManyToManyAccessor addManyToMany(ClassAccessor from, ClassAccessor to, String propName, String mappedBy) {
		ManyToManyAccessor accessor = new ManyToManyAccessor();
		setBasicAccessor(accessor, from, to, propName, mappedBy);
		accessor.setAttributeType("java.util.List");
		from.getAttributes().getManyToManys().add(accessor);
		return accessor;
	}

	public static IndexMetadata addIndex(EntityAccessor from, String name, boolean unique, String... props) {
		IndexMetadata index = new IndexMetadata();
		index.setName(name);
		index.setUnique(unique);
		index.setColumnNames(Arrays.asList(props));
		List<IndexMetadata> indices = from.getIndexes();
		if (indices == null) {
			indices = new ArrayList<>();
			from.setIndexes(indices);
		}
		indices.add(index);
		return index;

	}

}
