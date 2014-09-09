package com.flexdms.flexims.jpa.eclipselink;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.columns.DiscriminatorColumnMetadata;
import org.eclipse.persistence.internal.jpa.metadata.listeners.EntityListenerMetadata;
import org.eclipse.persistence.internal.jpa.metadata.mappings.AccessMethodsMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.GeneratedValueMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsMappingProject;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappingsReader;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitDefaults;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLPersistenceUnitMetadata;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.platform.SAXPlatform;

import com.flexdms.flexims.jpa.event.EntityListener;

public class MetaSourceBuilder {

	public static final String GENERATED_PACKAGE = "com.flexdms.flexims.dynamic.model.generated";
	public static final int DESCRIMINATOR_COLUMN_LENGTH = 40;
	public static final int SEQ_ID_START = 10000;
	public static final int SEQ_ID_BATCH = 50;
	public static final int EMBEDED_INITIAL_NUM = 10;
	public static final int MAPPED_SUPERCLASS_INITIAL_NUM = 10;
	public static final int ENTITY_INITIAL_NUM = 50;

	// publish event from JPA to CDI event.
	public static EntityListenerMetadata createCDIListener() {
		EntityListenerMetadata listenerMetadata = new EntityListenerMetadata();
		listenerMetadata.setClassName(EntityListener.class.getCanonicalName());
		listenerMetadata.setPostLoad("postLoad");
		listenerMetadata.setPostPersist("postPersist");
		listenerMetadata.setPostRemove("postRemove");
		listenerMetadata.setPostUpdate("postUpdate");

		listenerMetadata.setPrePersist("prePersist");
		listenerMetadata.setPreRemove("preRemove");
		listenerMetadata.setPreUpdate("preUpdate");

		return listenerMetadata;
	}

	public static XMLEntityMappings createEmptyEntityMappings() {
		XMLEntityMappings entityMaps = new XMLEntityMappings();

		XMLPersistenceUnitMetadata unitMeta = new XMLPersistenceUnitMetadata();
		entityMaps.setPersistenceUnitMetadata(unitMeta);
		unitMeta.setExcludeDefaultMappings(true);
		unitMeta.setXMLMappingMetadataComplete(true);

		// Unit Defaults
		XMLPersistenceUnitDefaults unitDefaults = new XMLPersistenceUnitDefaults();
		unitDefaults.setAccess("VIRTUAL");
		AccessMethodsMetadata accessMethodsMetadata = new AccessMethodsMetadata();
		accessMethodsMetadata.setGetMethodName("get");
		accessMethodsMetadata.setSetMethodName("set");
		unitDefaults.setAccessMethods(accessMethodsMetadata);
		unitDefaults.setEntityListeners(Arrays.asList(createCDIListener()));
		unitMeta.setPersistenceUnitDefaults(unitDefaults);

		entityMaps.setPackage(GENERATED_PACKAGE);

		List<TableGeneratorMetadata> tMetadatas = new LinkedList<>();
		entityMaps.setTableGenerators(tMetadatas);

		entityMaps.setEmbeddables(new ArrayList<EmbeddableAccessor>(EMBEDED_INITIAL_NUM));
		entityMaps.setMappedSuperclasses(new ArrayList<MappedSuperclassAccessor>(MAPPED_SUPERCLASS_INITIAL_NUM));
		entityMaps.setEntities(new ArrayList<EntityAccessor>(ENTITY_INITIAL_NUM));

		return entityMaps;
	}

	public static TableGeneratorMetadata addTableGenerator(EntityAccessor entity, XMLEntityMappings entityMappings) {
		String name = entity.getName();
		String seqname = name + "seq";
		TableGeneratorMetadata tMetadata = new TableGeneratorMetadata(name);
		tMetadata.setAllocationSize(SEQ_ID_BATCH);
		tMetadata.setGeneratorName(seqname);
		tMetadata.setName("seqs");
		// tMetadata.setDatabaseTable(new DatabaseTable("seqs", ""));
		tMetadata.setInitialValue(SEQ_ID_START);
		tMetadata.setPkColumnName("name");
		tMetadata.setValueColumnName("seq");
		entityMappings.getTableGenerators().add(tMetadata);
		
		return tMetadata;
	}
	
	public static IdAccessor addID(EntityAccessor entity, XMLEntityMappings entityMappings) {
		addTableGenerator(entity, entityMappings);

		IdAccessor idAccessor = new IdAccessor();
		idAccessor.setName("id");
		idAccessor.setAttributeType("Long");

		GeneratedValueMetadata generatedValueMetadata = new GeneratedValueMetadata();
		generatedValueMetadata.setStrategy("TABLE");
		generatedValueMetadata.setGenerator(entity.getName() + "seq");
		idAccessor.setGeneratedValue(generatedValueMetadata);
		entity.getAttributes().getIds().add(idAccessor);
		return idAccessor;
	}

	public static VersionAccessor addVersion(EntityAccessor entity) {
		VersionAccessor versionAccessor = new VersionAccessor();
		versionAccessor.setName("fxversion");

		versionAccessor.setAttributeType("java.sql.Timestamp");
		// TemporalMetadata temporalMetadata=new TemporalMetadata();
		// temporalMetadata.setTemporalType("TIMESTAMP");
		// versionAccessor.setTemporal(temporalMetadata);
		versionAccessor.setMutable(true);
		entity.getAttributes().getVersions().add(versionAccessor);
		return versionAccessor;
	}

	public static EntityAccessor establishInheritance(EntityAccessor child, EntityAccessor parent) {
		child.setParentClassName(parent.getClassName());

		// eclipselink automatically treat child class as embedded.
		PropertyMetadata propertyMetadata = new PropertyMetadata();
		propertyMetadata.setName("entity");
		propertyMetadata.setValue("true");

		child.getProperties().add(propertyMetadata);
		// test1.setCustomizerClassName("com.flexdms.flexims.jpa.eclipselink.InheritanceDescriptorCustomizer");

		child.setDiscriminatorValue(child.getName());

		if (parent.getDiscriminatorColumn() == null) {
			DiscriminatorColumnMetadata metadata = new DiscriminatorColumnMetadata();
			metadata.setDiscriminatorType("java.lang.String");
			metadata.setLength(DESCRIMINATOR_COLUMN_LENGTH);
			parent.setDiscriminatorColumn(metadata);
			parent.setDiscriminatorValue(parent.getName());
		}
		return child;
	}

	/**
	 * Why this? It seems that Unmarshaller adds some default behavior which is
	 * not available when XmlEntityMappings is built programmatically.
	 * 
	 * @param entityMaps
	 * @return
	 */
	public static XMLEntityMappings roundTripEntityMappings(XMLEntityMappings entityMaps) {
		StringWriter sWriter = new StringWriter();
		toXml(entityMaps, sWriter);
		XMLEntityMappings entityMaps1 = fromXml(new StringReader(sWriter.toString()));
		return entityMaps1;
	}

	public static final org.eclipse.persistence.jaxb.JAXBContext JAXB_CONTEXT;
	static {
		XMLEntityMappingsMappingProject project = new XMLEntityMappingsMappingProject(XMLEntityMappingsReader.ECLIPSELINK_ORM_NAMESPACE,
				XMLEntityMappingsReader.ECLIPSELINK_ORM_XSD);
		project.getDatasourceLogin().setDatasourcePlatform(new SAXPlatform());
		XMLContext context = new XMLContext(project);
		JAXB_CONTEXT = new org.eclipse.persistence.jaxb.JAXBContext(context);

	}

	private static void setMarshallerProperties(JAXBMarshaller marshaller) throws PropertyException {
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(MarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
		// without root
		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
		// empty collection to avoid nullpointer error
		marshaller.setProperty(MarshallerProperties.JSON_MARSHAL_EMPTY_COLLECTIONS, true);
	}

	private static void setUnMarshallerProperties(JAXBUnmarshaller unmarshaller) throws PropertyException {

		unmarshaller.setProperty(UnmarshallerProperties.JSON_ATTRIBUTE_PREFIX, "@");
		// without root
		unmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, true);
	}

	public static void toXml(XMLEntityMappings entityMaps, Writer writer) {

		try {
			JAXBMarshaller marshaller = JAXB_CONTEXT.createMarshaller();
			setMarshallerProperties(marshaller);
			marshaller.marshal(entityMaps, writer);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	public static void toJson(XMLEntityMappings entityMaps, Writer writer) {
		try {
			JAXBMarshaller marshaller = JAXB_CONTEXT.createMarshaller();
			setMarshallerProperties(marshaller);
			marshaller.setProperty("eclipselink.media-type", "application/json");
			marshaller.marshal(entityMaps, writer);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

	public static void fixUnmarshal(XMLEntityMappings entityMappings) {
		for (EntityAccessor entityAccessor : entityMappings.getEntities()) {
			if (entityAccessor.getName() == null) {
				entityAccessor.setName(entityAccessor.getClassName());
			}
		}
		for (EmbeddableAccessor embeddableAccessor : entityMappings.getEmbeddables()) {
			if (embeddableAccessor.getName() == null) {
				embeddableAccessor.setName(embeddableAccessor.getClassName());
			}
		}
		for (MappedSuperclassAccessor mappedSuperclassAccessor : entityMappings.getMappedSuperclasses()) {
			if (mappedSuperclassAccessor.getName() == null) {
				mappedSuperclassAccessor.setName(mappedSuperclassAccessor.getClassName());
			}
		}

	}

	public static XMLEntityMappings fromXml(Reader reader) {
		try {
			JAXBUnmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			setUnMarshallerProperties(unmarshaller);
			XMLEntityMappings entityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader);
			fixUnmarshal(entityMappings);
			return entityMappings;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}

	}

	public static XMLEntityMappings fromJson(Reader reader) {
		try {
			JAXBUnmarshaller unmarshaller = JAXB_CONTEXT.createUnmarshaller();
			setUnMarshallerProperties(unmarshaller);
			unmarshaller.setProperty("eclipselink.media-type", "application/json");
			XMLEntityMappings entityMappings = (XMLEntityMappings) unmarshaller.unmarshal(reader);
			fixUnmarshal(entityMappings);
			return entityMappings;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

}
