package com.flexdms.flexims.jaxb.moxy;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.metadata.MetadataSourceAdapter;
import org.eclipse.persistence.jaxb.xmlmodel.JavaAttribute;
import org.eclipse.persistence.jaxb.xmlmodel.JavaType;
import org.eclipse.persistence.jaxb.xmlmodel.ObjectFactory;
import org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings;
import org.eclipse.persistence.jaxb.xmlmodel.XmlBindings.JavaTypes;
import org.eclipse.persistence.jaxb.xmlmodel.XmlElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement;
import org.eclipse.persistence.jaxb.xmlmodel.XmlVirtualAccessMethods;

import com.flexdms.flexims.jpa.JpaMapHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;
import com.flexdms.flexims.jpa.eclipselink.MetaSourceBuilder;

public class RelationAsIDMoxyMetaSource extends MetadataSourceAdapter {

	protected ObjectFactory of;
	protected EntityManagerFactory emf;

	public RelationAsIDMoxyMetaSource() {

	}

	public RelationAsIDMoxyMetaSource(EntityManagerFactory emf) {
		of = new ObjectFactory();
		this.emf = emf;
	}

	@Override
	public XmlBindings getXmlBindings(Map<String, ?> properties, ClassLoader classLoader) {
		XmlBindings bindings = new XmlBindings();
		bindings.setPackageName(MetaSourceBuilder.GENERATED_PACKAGE);

		// do not generate any extra field/property from class. Only use
		// information in xml.
		bindings.setXmlAccessorType(XmlAccessType.NONE);

		// ignore annotation.
		// generated class does not have any annotation
		bindings.setXmlMappingMetadataComplete(true);

		// XmlSchema schema=new XmlSchema();
		// schema.setNamespace("http://www.flexdms.flexims/instance");
		// schema.setElementFormDefault(XmlNsForm.QUALIFIED);
		// schema.setAttributeFormDefault(XmlNsForm.UNQUALIFIED);
		//
		//
		// XmlNs ns=new XmlNs();
		// ns.setNamespaceUri("http://www.w3.org/2001/XMLSchema");
		// ns.setPrefix("xsd");
		// schema.getXmlNs().add(ns);
		//
		// XmlNs ns1=new XmlNs();
		// ns1.setNamespaceUri("http://www.flexdms.flexims/instance");
		// ns1.setPrefix("");
		// schema.getXmlNs().add(ns1);

		/*
		 * ns=new XmlNs();
		 * ns.setNamespaceUri("http://www.w3.org/2001/XMLSchema-instance");
		 * ns.setPrefix("xsi"); schema.getXmlNs().add(ns);
		 */
		// bindings.setXmlSchema(schema);

		bindings.setJavaTypes(new JavaTypes());
		Metamodel metamodel = emf.getMetamodel();
		for (ManagedType<?> entityType : metamodel.getManagedTypes()) {
			JavaType type = createJavaType(entityType);
			bindings.getJavaTypes().getJavaType().add(type);
		}
		return bindings;
	}

	public static void toOXM(XmlBindings bindings, Writer writer) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance("org.eclipse.persistence.jaxb.xmlmodel");
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.marshal(bindings, writer);
	}

	public static XmlBindings fromOXM(Reader reader) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance("org.eclipse.persistence.jaxb.xmlmodel");
		Unmarshaller unmarshaller = context.createUnmarshaller();
		return (XmlBindings) unmarshaller.unmarshal(reader);
	}

	public JavaType createJavaType(ManagedType<?> t) {
		// System.out.println("create java type "+t.getName());
		JavaType jType = new JavaType();
		jType.setName(t.getJavaType().getSimpleName());
		if (t instanceof IdentifiableType) {
			IdentifiableType<?> i = (IdentifiableType<?>) t;
			if (i.getSupertype() != null) {
				jType.setSuperType(i.getSupertype().getJavaType().getName());
			} else {
				jType.setSuperType(FleximsDynamicEntityImpl.class.getName());
			}
		} else {
			jType.setSuperType(FleximsDynamicEntityImpl.class.getName());
		}

		XmlVirtualAccessMethods vs = new XmlVirtualAccessMethods();
		// vs.setGetMethod("realGet");
		// vs.setSetMethod("realSet");
		// vs.setSchema(XmlVirtualAccessMethodsSchema.NODES);
		jType.setXmlVirtualAccessMethods(vs);
		// should be root elements
		boolean isroot = true;
		if (t.getPersistenceType() == Type.PersistenceType.EMBEDDABLE) {
			isroot = false;
		}

		if (isroot) {
			XmlRootElement rootElement = new XmlRootElement();
			// specify name so name case is not ambiguous
			rootElement.setName(t.getJavaType().getSimpleName());
			jType.setXmlRootElement(rootElement);
		}

		JavaType.JavaAttributes attributes = of.createJavaTypeJavaAttributes();
		jType.setJavaAttributes(attributes);

		for (Attribute<?, ?> attr : t.getDeclaredAttributes()) {
			// for (Attribute<?, ?> attr : t.getAttributes()) {
			switch (attr.getPersistentAttributeType()) {
			case BASIC:
				if (attr.getJavaType().equals(Serializable.class)) {
					attributes.getJavaAttribute().add(createObjectProperty(attr, jType));
				} else {
					attributes.getJavaAttribute().add(createSinglePrimitiveProperty(attr));
				}

				break;
			case ELEMENT_COLLECTION:
				PluralAttribute<?, ?, ?> cattr = (PluralAttribute<?, ?, ?>) attr;
				if (isPrimitiveType(cattr.getElementType().getJavaType())) {
					attributes.getJavaAttribute().add(createCollectionhPrimitiveProperty(cattr));
				} else {
					// embedded
					attributes.getJavaAttribute().add(createCollectionhPrimitiveProperty(cattr));
				}

				break;
			case EMBEDDED:
				attributes.getJavaAttribute().add(createEmbeded(attr));
				break;
			case MANY_TO_MANY:
				attributes.getJavaAttribute().add(createRelation(attr, jType));
				break;
			case MANY_TO_ONE:
				attributes.getJavaAttribute().add(createRelation(attr, jType));
				break;
			case ONE_TO_MANY:
				attributes.getJavaAttribute().add(createRelation(attr, jType));
				break;
			case ONE_TO_ONE:
				attributes.getJavaAttribute().add(createRelation(attr, jType));
				break;
			default:
				break;
			}
		}
		return jType;

	}

	public boolean isPrimitiveType(Class<?> javatype) {

		if (javatype.equals(Serializable.class)) {
			return false;
		}
		if (javatype.equals(String.class)) {
			return true;
		}
		if (Number.class.isAssignableFrom(javatype)) {
			return true;
		}
		if (javatype.equals(Calendar.class)) {
			return true;
		}
		if (Date.class.isAssignableFrom(javatype)) {
			return true;
		}
		if (javatype.equals(boolean.class)) {
			return true;
		}
		return false;
	}

	public JAXBElement<? extends JavaAttribute> createRelation(Attribute<?, ?> attr, JavaType jType) {
		String mappedBy = JpaMapHelper.getMappedBy(jType.getName(), attr.getName());
		if (mappedBy != null) {
			// show ID instead of skip.
			return createIDRefRelation(attr, jType, mappedBy);
		} else {
			return createIDRefRelation(attr, jType, mappedBy);
		}
	}
	//why always go to super type: https://www.eclipse.org/forums/index.php/m/1412251/#msg_1412251
	protected Class<?> getRelatedType(Attribute<?, ?> attr) {
		Class<?> clz;
		if (attr.isCollection()) {
			PluralAttribute<?, ?, ?> cattr = (PluralAttribute<?, ?, ?>) attr;
			clz = cattr.getElementType().getJavaType();
		} else {
			clz = attr.getJavaType();
		}
		while (clz.getSuperclass() != FleximsDynamicEntityImpl.class) {
			clz = clz.getSuperclass();
		}
		return clz;

	}

	protected JAXBElement<? extends JavaAttribute> createIDRefRelation(Attribute<?, ?> attr, JavaType jType, String mappedBy) {
		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		prop.setJavaAttribute(attr.getName());
		Class<?> clz = getRelatedType(attr);
		if (attr.isCollection()) {
			PluralAttribute<?, ?, ?> cattr = (PluralAttribute<?, ?, ?>) attr;
			setContainerType(prop, cattr);
		}
		prop.setType(clz.getName());
		prop.setXmlIdref(true);
		// we are owing relationship,
		/*
		 * if (mappedBy == null) { RelationshipAccessor relation =
		 * JpaMapHelper.findPropertyBbyMappedBy(clz.getSimpleName(),
		 * attr.getName()); if (relation != null) {
		 * XmlElement.XmlInverseReference rref = new
		 * XmlElement.XmlInverseReference();
		 * rref.setMappedBy(relation.getName());
		 * prop.setXmlInverseReference(rref); }
		 * 
		 * }
		 */

		return of.createXmlElement(prop);
	}

	public JAXBElement<? extends JavaAttribute> createEmbeded(Attribute<?, ?> attr) {
		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		prop.setJavaAttribute(attr.getName());

		Class<?> clz = attr.getJavaType();
		prop.setType(clz.getName());

		return of.createXmlElement(prop);
	}

	public JAXBElement<? extends JavaAttribute> createObjectProperty(Attribute<?, ?> attr, JavaType jType) {
		// XmlAnyElement prop=new XmlAnyElement();
		//
		// prop.setJavaAttribute(attr.getName());
		// prop.setLax(true);
		// return of.createXmlAnyElement(prop);
		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		prop.setJavaAttribute(attr.getName());
		prop.setType(attr.getJavaType().getName());
		return of.createXmlElement(prop);

		// XmlJavaTypeAdapter adaptor=of.createXmlJavaTypeAdapter();
		// adaptor.setValue("com.flexdms.flexims.typedb.eclipselink.ObjectAdapter");
		// prop.setXmlJavaTypeAdapter(adaptor);

	}

	public JAXBElement<? extends JavaAttribute> createCollectionhPrimitiveProperty(PluralAttribute<?, ?, ?> attr) {
		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		prop.setJavaAttribute(attr.getName());

		Class<?> javaType = attr.getElementType().getJavaType();
		prop.setType(javaType.getName());
		setContainerType(prop, attr);
		return of.createXmlElement(prop);
	}

	protected void setContainerType(XmlElement prop, PluralAttribute<?, ?, ?> attr) {
		prop.setContainerType(decideContainerType(attr));
	}

	protected String decideContainerType(PluralAttribute<?, ?, ?> attr) {
		switch (attr.getCollectionType()) {
		case LIST:
			return List.class.getName();
		case SET:
			return Set.class.getName();
		case MAP:
			return Map.class.getName();
		default:
			return Collection.class.getName();
		}
	}

	public JAXBElement<? extends JavaAttribute> createSinglePrimitiveProperty(Attribute<?, ?> attr) {
		XmlElement prop = new XmlElement();
		prop.setName(attr.getName());
		prop.setJavaAttribute(attr.getName());
		SingularAttribute<?, ?> singularAttribute = (SingularAttribute<?, ?>) attr;
		if (singularAttribute.isId()) {
			prop.setXmlId(true);
		}
		prop.setType(attr.getJavaType().getName());

		return of.createXmlElement(prop);
	}

}
