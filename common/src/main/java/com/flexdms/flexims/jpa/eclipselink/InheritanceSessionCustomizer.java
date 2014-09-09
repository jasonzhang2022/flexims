package com.flexdms.flexims.jpa.eclipselink;

import java.util.Calendar;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectCollectionMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;

import com.flexdms.flexims.jpa.PrimitiveType;
import com.flexdms.flexims.jpa.helper.BigdecimalConverter;
import com.flexdms.flexims.jpa.helper.ByteArrayConveter;
import com.flexdms.flexims.jpa.helper.JAXBConverter;

/**
 * Workaround for many trick issues or bugs in eclipselink
 * 
 * @author jason.zhang
 * 
 */
public class InheritanceSessionCustomizer implements SessionCustomizer {

	@Override
	public void customize(Session session) throws Exception {
		// byte[] x=new byte[0];
		// https://wiki.eclipse.org/EclipseLink/UserGuide/JPA/Basic_JPA_Development/Mapping/Basic_Mappings/Lob
		session.getLogin().setUsesStreamsForBinding(true);

		/**
		 * Why all the date time converter. If the there is no data/time
		 * converter, eclipselink JPA metamodel data will think the element type
		 * for direct collection is java.lang.Object.
		 */
		org.eclipse.persistence.mappings.converters.TypeConversionConverter dateConverter = new org.eclipse.persistence.mappings.converters.TypeConversionConverter();
		dateConverter.setDataClass(java.sql.Date.class);
		dateConverter.setObjectClass(Calendar.class);

		org.eclipse.persistence.mappings.converters.TypeConversionConverter timeConverter = new org.eclipse.persistence.mappings.converters.TypeConversionConverter();
		timeConverter.setDataClass(java.sql.Time.class);
		timeConverter.setObjectClass(Calendar.class);

		org.eclipse.persistence.mappings.converters.TypeConversionConverter timestampConverter = new org.eclipse.persistence.mappings.converters.TypeConversionConverter();
		timestampConverter.setDataClass(java.sql.Timestamp.class);
		timestampConverter.setObjectClass(Calendar.class);

		ByteArrayConveter byteArrayConveter = new ByteArrayConveter();

		BigdecimalConverter bigdecimalConverter = new BigdecimalConverter();
		for (ClassDescriptor descriptor : session.getDescriptors().values()) {

			if (!FleximsDynamicEntityImpl.class.isAssignableFrom(descriptor.getJavaClass())) {

				continue;
			}

			// change eclipselink event to CDI event.
			// descriptor.getEventManager().addListener(new
			// com.flexdms.flexims.jpa.eclipselink.event.EntityDescriptorEventListener());

			// fix inheritance issues
			if (descriptor.getJavaClass().getSuperclass() != FleximsDynamicEntityImpl.class) {
				InheritancePolicy iPolicy = descriptor.getInheritancePolicy();
				iPolicy.getParentClassName();
				ClassDescriptor parentClassDescriptor = iPolicy.getParentDescriptor();
				if (parentClassDescriptor == null) {
					parentClassDescriptor = session.getClassDescriptor(descriptor.getJavaClass().getSuperclass());
					iPolicy.setParentDescriptor(parentClassDescriptor);
					parentClassDescriptor.getInheritancePolicy().addChildDescriptor(descriptor);
					String indicator = descriptor.getJavaClass().getSimpleName();
					parentClassDescriptor.getInheritancePolicy().addClassIndicator(descriptor.getJavaClass(), indicator);
					if (!parentClassDescriptor.getInheritancePolicy().getClassIndicatorMapping().containsKey(parentClassDescriptor.getJavaClass())) {
						parentClassDescriptor.getInheritancePolicy().addClassIndicator(parentClassDescriptor.getJavaClass(),
								parentClassDescriptor.getJavaClass().getSimpleName());

					}

				}
				if (descriptor.getProperty("entity") != null && descriptor.getProperty("entity").equals("true")) {
					descriptor.setDescriptorType(0);
				}

			}

			for (DatabaseMapping mapping : descriptor.getMappings()) {

				// converter fixes
				if (mapping.isDirectCollectionMapping()) {

					DirectCollectionMapping dMapping = (DirectCollectionMapping) mapping;
					if (mapping.getAttributeClassification().equals(java.math.BigDecimal.class)) {
						dMapping.setValueConverter(bigdecimalConverter);
					}

					if (dMapping.getProperty("typeidx") == null) {
						continue;
					}
					int typeidx = Integer.valueOf(dMapping.getProperty("typeidx").toString());
					if (typeidx == PrimitiveType.TYPE_INDEX_DATE) {
						dMapping.setValueConverter(dateConverter);
					} else if (typeidx == PrimitiveType.TYPE_INDEX_TIMESTAMP) {
						dMapping.setValueConverter(timestampConverter);
					} else if (typeidx == PrimitiveType.TYPE_INDEX_TIME) {
						dMapping.setValueConverter(timeConverter);
					}
					// order Column is not supported by xml.
					if (dMapping.getProperty("orderColumn") != null && dMapping.getProperty("orderColumn").equals("true")) {
						dMapping.setListOrderFieldName(dMapping.getAttributeName() + "_ORDER");
					}
				}
				if (mapping instanceof OneToManyMapping) {
					OneToManyMapping manyMapping = (OneToManyMapping) mapping;
					// order Column is not supported by xml.
					if (manyMapping.getProperty("orderColumn") != null && manyMapping.getProperty("orderColumn").equals("true")) {
						manyMapping.setListOrderFieldName(manyMapping.getAttributeName() + "_ORDER");
					}
				}
				if (mapping instanceof ManyToManyMapping) {
					ManyToManyMapping manyMapping = (ManyToManyMapping) mapping;
					// order Column is not supported by xml.
					if (manyMapping.getProperty("orderColumn") != null && manyMapping.getProperty("orderColumn").equals("true")) {
						manyMapping.setListOrderFieldName(manyMapping.getAttributeName() + "_ORDER");
					}
				}
				// custom object using JAXBConverter
				if (mapping.getProperty("rootClass") != null) {
					DirectToFieldMapping dmap = (DirectToFieldMapping) mapping;
					JAXBConverter jaxbConverter = new JAXBConverter();
					jaxbConverter.initialize(mapping, session);
					dmap.setConverter(jaxbConverter);
					Class<?> rootClass = Class.forName(mapping.getProperty("rootClass").toString());
					dmap.setAttributeClassification(rootClass);
					dmap.setAttributeClassificationName(rootClass.getName());
					continue;
				}

				// bigDecimal convert for direct field.
				if (mapping instanceof DirectToFieldMapping && mapping.getAttributeClassification() != null
						&& mapping.getAttributeClassification().equals(java.math.BigDecimal.class)) {
					DirectToFieldMapping dmap = (DirectToFieldMapping) mapping;
					dmap.setConverter(bigdecimalConverter);
				}

				if (mapping.getProperty("typeidx") == null) {
					continue;
				}

				// this is how we handle byte array
				int typeidx = Integer.valueOf(mapping.getProperty("typeidx").toString());
				if (typeidx == PrimitiveType.TYPE_INDEX_BYTEARRAY) {
					DirectToFieldMapping dmap = (DirectToFieldMapping) mapping;
					dmap.setConverter(byteArrayConveter);
				}

			}
		}
	}

}
