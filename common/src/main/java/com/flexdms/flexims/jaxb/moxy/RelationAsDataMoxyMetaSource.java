package com.flexdms.flexims.jaxb.moxy;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.PluralAttribute;
import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.internal.jpa.metadata.accessors.mappings.RelationshipAccessor;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;

import com.flexdms.flexims.jpa.JpaMapHelper;

public class RelationAsDataMoxyMetaSource extends RelationAsIDMoxyMetaSource {

	public RelationAsDataMoxyMetaSource() {
		super();
	}

	public RelationAsDataMoxyMetaSource(EntityManagerFactory emf) {
		super(emf);
	}

	@Override
	public JAXBElement<? extends JavaAttribute> createRelation(Attribute<?, ?> attr, JavaType jType) {
		String mappedBy = JpaMapHelper.getMappedBy(jType.getName(), attr.getName());
		if (mappedBy != null) {
			return createIDRefRelation(attr, jType, mappedBy);
		} else {
			return createNestedRelation(attr, jType);
		}
	}

	protected JAXBElement<? extends JavaAttribute> createNestedRelation(Attribute<?, ?> attr, JavaType jType) {

		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		Class<?> clz = getRelatedType(attr);
		prop.setJavaAttribute(attr.getName());
		if (attr.isCollection()) {
			PluralAttribute<?, ?, ?> cattr = (PluralAttribute<?, ?, ?>) attr;
			setContainerType(prop, cattr);
		}
		prop.setType(clz.getName());
		// Not supported for virtual methods
//		RelationshipAccessor relation = JpaMapHelper.findPropertyBbyMappedBy(clz.getSimpleName(), attr.getName());
//		if (relation != null) {
//			XmlElement.XmlInverseReference rref = new XmlElement.XmlInverseReference();
//			rref.setMappedBy(relation.getName());
//			prop.setXmlInverseReference(rref);
//		}

		return of.createXmlElement(prop);
	}

}
