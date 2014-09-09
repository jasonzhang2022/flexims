package com.flexdms.flexims.jpa;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.BasicCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ElementCollectionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.EmbeddedAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.ManyToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.MappingAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToManyAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.OneToOneAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.VersionAccessor;
import org.eclipse.persistence.internal.jpa.metadata.converters.TemporalMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.SequenceGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.sequencing.TableGeneratorMetadata;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.util.Utils;

/**
 * Convenient methods to retrieve information from ORM mapping data.
 * 
 * @author jason.zhang
 * 
 */
public final class JpaMapHelper {

	private JpaMapHelper() {

	}

	public static XMLEntityMappings entityMaps = null;

	public static XMLEntityMappings getInternaEntityMappings() {
		if (entityMaps != null) {
			return entityMaps;
		}
		return DynamicMetaSource.getEntityMaps();
	}

	public static ClassAccessor findClassAccessor(XMLEntityMappings entityMappings, String typename) {
		for (EntityAccessor entityAccessor : entityMappings.getEntities()) {
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				return entityAccessor;
			}
		}
		for (EmbeddableAccessor embeddableAccessor : getInternaEntityMappings().getEmbeddables()) {
			if (embeddableAccessor.getName().equalsIgnoreCase(typename)) {
				return embeddableAccessor;
			}

		}
		return null;
	}

	public static void removeGenerator(XMLEntityMappings entityMappings, String generator) {
		for (ListIterator<TableGeneratorMetadata> listIterator = entityMappings.getTableGenerators().listIterator(); listIterator.hasNext();) {
			TableGeneratorMetadata tMetadata = listIterator.next();
			if (tMetadata.getName().equals(generator)) {
				listIterator.remove();
				return;
			}
		}
		for (ListIterator<SequenceGeneratorMetadata> listIterator = entityMappings.getSequenceGenerators().listIterator(); listIterator.hasNext();) {
			SequenceGeneratorMetadata tMetadata = listIterator.next();
			if (tMetadata.getName().equals(generator)) {
				listIterator.remove();
				return;
			}
		}
	}

	public static ClassAccessor deleteClassAccessor(XMLEntityMappings entityMappings, String typename) {

		for (ListIterator<EntityAccessor> listIterator = entityMappings.getEntities().listIterator(); listIterator.hasNext();) {
			EntityAccessor entityAccessor = listIterator.next();
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				listIterator.remove();
				if (!entityAccessor.getAttributes().getIds().isEmpty()) {
					IdAccessor idAccessor = entityAccessor.getAttributes().getIds().get(0);
					if (idAccessor.getGeneratedValue() != null) {
						String generatorString = idAccessor.getGeneratedValue().getGenerator();
						if (generatorString != null) {
							removeGenerator(entityMappings, generatorString);
						}
					}
				}
				return entityAccessor;
			}
		}
		for (ListIterator<EmbeddableAccessor> listIterator = entityMappings.getEmbeddables().listIterator(); listIterator.hasNext();) {
			EmbeddableAccessor entityAccessor = listIterator.next();
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				listIterator.remove();
				return entityAccessor;
			}
		}

		for (ListIterator<MappedSuperclassAccessor> listIterator = entityMappings.getMappedSuperclasses().listIterator(); listIterator.hasNext();) {
			MappedSuperclassAccessor entityAccessor = listIterator.next();
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				listIterator.remove();
				return entityAccessor;
			}
		}
		return null;
	}

	public static boolean replaceClassAccessor(XMLEntityMappings entityMappings, EntityAccessor accessor) {
		String typename = accessor.getName();
		for (ListIterator<EntityAccessor> iterator = entityMappings.getEntities().listIterator(); iterator.hasNext();) {
			EntityAccessor entityAccessor = iterator.next();
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				iterator.set(accessor);
				return true;
			}
		}
		return false;
	}

	public static boolean replaceClassAccessor(XMLEntityMappings entityMappings, EmbeddableAccessor accessor) {
		String typename = accessor.getName();
		for (ListIterator<EmbeddableAccessor> iterator = entityMappings.getEmbeddables().listIterator(); iterator.hasNext();) {
			EmbeddableAccessor entityAccessor = iterator.next();
			if (entityAccessor.getName().equalsIgnoreCase(typename)) {
				iterator.set(accessor);
				return true;
			}
		}
		return false;
	}

	public static void addClassAccessor(XMLEntityMappings entityMappings, ClassAccessor accessor) {
		if (accessor instanceof EmbeddableAccessor) {
			entityMappings.getEmbeddables().add((EmbeddableAccessor) accessor);
			return;
		}
		if (accessor instanceof EntityAccessor) {
			entityMappings.getEntities().add((EntityAccessor) accessor);
			return;
		}

		// we do not have mappedsupper class
		if (accessor instanceof MappedSuperclassAccessor) {
			entityMappings.getMappedSuperclasses().add((MappedSuperclassAccessor) accessor);
			return;
		}

	}

	public static TableGeneratorMetadata retrieveTableGenerator(XMLEntityMappings entityMappings, ClassAccessor accessor) {
		if (accessor.getAttributes().getIds() == null || accessor.getAttributes().getIds().isEmpty()) {
			return null;
		}
		String name = accessor.getAttributes().getIds().get(0).getGeneratedValue().getGenerator();
		for (TableGeneratorMetadata tmeta : entityMappings.getTableGenerators()) {
			if (tmeta.getGeneratorName().equals(name)) {
				return tmeta;
			}
		}
		return null;
	}

	public static MappingAccessor deleteProp(ClassAccessor accessor, String propname) {
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getBasics().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getBasicCollections().listIterator(); propIterator
				.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getBasicMaps().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getElementCollections().listIterator(); propIterator
				.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getEmbeddeds().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getOneToOnes().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getOneToManys().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getManyToManys().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}
		for (ListIterator<? extends MappingAccessor> propIterator = accessor.getAttributes().getManyToOnes().listIterator(); propIterator.hasNext();) {
			MappingAccessor prop = propIterator.next();
			if (prop.getName().equalsIgnoreCase(propname)) {
				propIterator.remove();
				return prop;
			}
		}

		// id and version can not be removed
		return null;
	}

	// TODO should have handle the situation that the mappedBy is in
	// parentClass?
	public static RelationshipAccessor findPropertyBbyMappedBy(String type, String mappedBy) {
		ClassAccessor accessor = findClassAccessor(getInternaEntityMappings(), type);
		if (accessor == null) {
			// the type is not managed by JPA
			return null;
		}
		if (accessor.getAttributes() == null) {
			return null;
		}
		if (accessor.getAttributes().getOneToOnes() != null) {
			for (OneToOneAccessor prop : accessor.getAttributes().getOneToOnes()) {
				if (prop.getMappedBy() != null && prop.getMappedBy().equals(mappedBy)) {
					return prop;
				}
			}
		}
		if (accessor.getAttributes().getOneToManys() != null) {
			for (OneToManyAccessor prop : accessor.getAttributes().getOneToManys()) {
				if (prop.getMappedBy() != null && prop.getMappedBy().equals(mappedBy)) {
					return prop;
				}
			}
		}
		if (accessor.getAttributes().getManyToManys() != null) {
			for (ManyToManyAccessor prop : accessor.getAttributes().getManyToManys()) {
				if (prop.getMappedBy() != null && prop.getMappedBy().equals(mappedBy)) {
					return prop;
				}
			}
		}
		if (accessor.getAttributes().getManyToOnes() != null) {
			for (ManyToOneAccessor prop : accessor.getAttributes().getManyToOnes()) {
				if (prop.getMappedBy() != null && prop.getMappedBy().equals(mappedBy)) {
					return prop;
				}
			}
		}
		return null;

	}

	public static MappingAccessor findProp(ClassAccessor accessor, String propname) {
		for (BasicAccessor prop : accessor.getAttributes().getBasics()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (BasicCollectionAccessor prop : accessor.getAttributes().getBasicCollections()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (BasicCollectionAccessor prop : accessor.getAttributes().getBasicMaps()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (ElementCollectionAccessor prop : accessor.getAttributes().getElementCollections()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (EmbeddedAccessor prop : accessor.getAttributes().getEmbeddeds()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (OneToOneAccessor prop : accessor.getAttributes().getOneToOnes()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (OneToManyAccessor prop : accessor.getAttributes().getOneToManys()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (ManyToManyAccessor prop : accessor.getAttributes().getManyToManys()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (ManyToOneAccessor prop : accessor.getAttributes().getManyToOnes()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (IdAccessor prop : accessor.getAttributes().getIds()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		for (VersionAccessor prop : accessor.getAttributes().getVersions()) {
			if (prop.getName().equalsIgnoreCase(propname)) {
				return prop;
			}
		}
		String pname = accessor.getParentClassName();
		if (pname != null) {
			ClassAccessor superAccessor = findClassAccessor(getInternaEntityMappings(), pname);
			return findProp(superAccessor, propname);
		}

		return null;
	}

	public static boolean isPrivateRelation(String typeName, String attrName) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);

		if (prop instanceof RelationshipAccessor) {
			return ((RelationshipAccessor) prop).isPrivateOwned();
		}

		return false;

	}

	public static boolean isSummaryProp(String typeName, String attrName) {

		String sv = getExtraProp(typeName, attrName, "summaryprop");
		if (sv == null) {
			return false;
		}
		return Utils.stringToBoolean(sv, false);
	}

	public static String getMappedBy(String typeName, String attrName) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);

		if (prop instanceof RelationshipAccessor) {
			return ((RelationshipAccessor) prop).getMappedBy();
		}

		return null;

	}

	public static List<PropertyMetadata> getClassExtraProps(String typeName) {
		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		return typeAccessor.getProperties();
	}

	public static List<PropertyMetadata> getExtraProps(String typeName, String attrName) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);
		return prop.getProperties();
	}
	
	public static String getExtraProp(String typeName, String propname) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		for (PropertyMetadata propmeta : typeAccessor.getProperties()) {
			if (propmeta.getName().equalsIgnoreCase(propname)) {
				return propmeta.getValue();
			}
		}
		return null;
	}
	

	public static String getExtraProp(String typeName, String attrName, String propname) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);
		for (PropertyMetadata propmeta : prop.getProperties()) {
			if (propmeta.getName().equalsIgnoreCase(propname)) {
				return propmeta.getValue();
			}
		}
		return null;
	}

	public static Integer getProptypeIdx(String typeName, String attrName) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);
		for (PropertyMetadata propmeta : prop.getProperties()) {
			if (propmeta.getName().equalsIgnoreCase("typeidx")) {
				return Integer.parseInt(propmeta.getValue());
			}
		}
		return null;

	}

	public static String getPropTemporal(String typeName, String attrName) {

		XMLEntityMappings entityMappings = getInternaEntityMappings();

		ClassAccessor typeAccessor = findClassAccessor(entityMappings, typeName);
		MappingAccessor prop = findProp(typeAccessor, attrName);
		TemporalMetadata temporalMetadata = prop.getTemporal(false);
		if (temporalMetadata != null) {
			return temporalMetadata.getTemporalType();
		}
		return null;

	}

}
