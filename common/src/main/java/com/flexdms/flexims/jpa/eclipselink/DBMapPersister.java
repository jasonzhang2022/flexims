package com.flexdms.flexims.jpa.eclipselink;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ListIterator;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.persistence.internal.jpa.metadata.accessors.PropertyMetadata;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.ClassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EmbeddableAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.EntityAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.classes.MappedSuperclassAccessor;
import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.IdAccessor;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.textstore.ClobData;
import com.flexdms.flexims.jpa.textstore.TextStoreEMF;
import com.flexdms.flexims.util.Utils;

@Dependent
public class DBMapPersister implements MapPersister {

	public static final String NAME = "_FX_ECLIPSE_JPA_MAP";
	@Inject
	TextStoreEMF emf;

	@Override
	public void save(XMLEntityMappings map) {
		// make a copy
		map = MetaSourceBuilder.roundTripEntityMappings(map);
		filterFixed(map);
		StringWriter stringWriter = new StringWriter();
		MetaSourceBuilder.toXml(map, stringWriter);
		emf.save(new ClobData(NAME, stringWriter.toString()));
		App.fireEvent(new TypeUpdated());

	}

	@Override
	public XMLEntityMappings load() {
		ClobData data = emf.load(NAME);
		if (data == null) {
			return null;
		}
		return MetaSourceBuilder.fromXml(new StringReader(data.getData()));
	}

	public static boolean isFixed(ClassAccessor accessor) {
		for (PropertyMetadata propertyMetadata : accessor.getProperties()) {
			if (propertyMetadata.getName().equalsIgnoreCase(DynamicMetaSource.FIXED_NAME)) {
				boolean v = Utils.stringToBoolean(propertyMetadata.getValue(), false);
				return v;
			}
		}
		return false;
	}

	public static void filterFixed(XMLEntityMappings entityMappings) {

		for (ListIterator<EntityAccessor> listIterator = entityMappings.getEntities().listIterator(); listIterator.hasNext();) {
			EntityAccessor entityAccessor = listIterator.next();
			if (isFixed(entityAccessor)) {
				listIterator.remove();
				if (entityAccessor.getAttributes() != null && entityAccessor.getAttributes().getIds() != null
						&& !entityAccessor.getAttributes().getIds().isEmpty()) {
					IdAccessor idAccessor = entityAccessor.getAttributes().getIds().get(0);
					if (idAccessor.getGeneratedValue() != null) {
						String generatorString = idAccessor.getGeneratedValue().getGenerator();
						if (generatorString != null) {
							JpaMapHelper.removeGenerator(entityMappings, generatorString);
						}
					}
				}
			}
		}
		for (ListIterator<EmbeddableAccessor> listIterator = entityMappings.getEmbeddables().listIterator(); listIterator.hasNext();) {
			EmbeddableAccessor entityAccessor = listIterator.next();
			if (isFixed(entityAccessor)) {
				listIterator.remove();
			}
		}

		for (ListIterator<MappedSuperclassAccessor> listIterator = entityMappings.getMappedSuperclasses().listIterator(); listIterator.hasNext();) {
			MappedSuperclassAccessor entityAccessor = listIterator.next();
			if (isFixed(entityAccessor)) {
				listIterator.remove();
			}
		}
	}

}
