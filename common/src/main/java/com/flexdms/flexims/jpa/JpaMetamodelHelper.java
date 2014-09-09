package com.flexdms.flexims.jpa;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.TemporalType;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.PluralAttribute;

import com.flexdms.flexims.App;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

/**
 * Convenient methods to retrieve information from JPA Metamodel.
 * 
 * @author jason.zhang
 * 
 */
public final class JpaMetamodelHelper {

	private JpaMetamodelHelper() {

	}

	public static Metamodel getMetamodel() {
		return App.getPersistenceUnit().getEMF().getMetamodel();
	}

	public static List<Attribute<?, ?>> findReferringProps(String type, boolean excludeSelf) {
		Metamodel metamodel = getMetamodel();
		List<Attribute<?, ?>> attrs = new LinkedList<>();
		for (ManagedType<?> entityType : metamodel.getManagedTypes()) {
			if (excludeSelf && entityType.getJavaType().getSimpleName().equals(type)) {
				continue;
			}
			for (Attribute<?, ?> attr : entityType.getAttributes()) {
				if (getAttributeType(attr).getSimpleName().equals(type)) {
					attrs.add(attr);
				}
			}
		}
		return attrs;
	}

	public static ManagedType<?> getManagedType(Metamodel metamodel, String name) {
		for (ManagedType<?> entityType : metamodel.getManagedTypes()) {
			if (entityType.getJavaType().getSimpleName().equalsIgnoreCase(name)) {
				return entityType;
			}
		}

		return null;
	}

	public static Class<?> getSuperType(String name) {
		ManagedType<?> managedType = getManagedType(getMetamodel(), name);
		Class<?> class1 = managedType.getJavaType().getSuperclass();
		if (class1 == FleximsDynamicEntityImpl.class) {
			return null;
		}
		if (class1 == Object.class) {
			return null;
		}
		return class1;
	}

	public static List<Class<?>> collectChildClasses(Class<?> clz) {
		List<Class<?>> clsz = new ArrayList<>(3);
		Metamodel metamodel = getMetamodel();

		for (ManagedType<?> entityType : metamodel.getManagedTypes()) {
			if (clz.isAssignableFrom(entityType.getJavaType())) {
				clsz.add(entityType.getJavaType());
			}
		}
		return clsz;
	}

	@SuppressWarnings("rawtypes")
	public static Attribute<?, ?> getAttribute(ManagedType<?> t, String name) {
		for (Attribute<?, ?> attr : t.getAttributes()) {
			if (attr.getName().equalsIgnoreCase(name)) {
				return attr;
			}
		}
		if (t instanceof IdentifiableType) {
			IdentifiableType superType = ((IdentifiableType) t).getSupertype();
			if (superType != null) {
				return getAttribute(superType, name);
			}
		}
		return null;
	}

	public static Attribute<?, ?> getAttribute(String typename, String propname) {
		return getAttribute(getManagedType(getMetamodel(), typename), propname);
	}

	public static Class<?> getAttributeType(Attribute<?, ?> attr) {
		Class<?> javatype = attr.getJavaType();
		if (attr.isCollection()) {
			PluralAttribute<?, ?, ?> cattr = (PluralAttribute<?, ?, ?>) attr;
			javatype = cattr.getElementType().getJavaType();
		}
		return javatype;
	}

	public static boolean isPrimitiveType(Attribute<?, ?> attr) {
		Class<?> javatype = getAttributeType(attr);
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

	public static boolean isBasic(Attribute<?, ?> attr) {
		return attr.getPersistentAttributeType() == PersistentAttributeType.BASIC;
	}

	public static boolean isString(Attribute<?, ?> attr) {
		Class<?> javatype = getAttributeType(attr);
		if (javatype.equals(String.class)) {
			return true;
		}
		return false;
	}

	public static boolean isElementCollection(Attribute<?, ?> attr) {
		return attr.getPersistentAttributeType() == PersistentAttributeType.ELEMENT_COLLECTION;
	}

	public static boolean isEmedded(Attribute<?, ?> attr) {
		return attr.getPersistentAttributeType() == PersistentAttributeType.EMBEDDED;

	}

	public static boolean isRelation(Attribute<?, ?> attr) {
		switch (attr.getPersistentAttributeType()) {
		case MANY_TO_MANY:
		case MANY_TO_ONE:
		case ONE_TO_MANY:
		case ONE_TO_ONE:
			return true;
		default:
			return false;
		}
	}

	public static boolean isRelationOwner(Attribute<?, ?> attr) {
		return JpaMapHelper.getMappedBy(attr.getDeclaringType().getJavaType().getSimpleName(), attr.getName()) == null ? true : false;
	}

	// need test case for this
	public static PrimitiveType getPrimitiveType(Attribute<?, ?> attr) {
		Class<?> javatype = getAttributeType(attr);
		if (!isPrimitiveType(attr)) {
			return null;
		}
		Integer idx = JpaMapHelper.getProptypeIdx(attr.getDeclaringType().getJavaType().getSimpleName(), attr.getName());
		if (idx != null && idx < PrimitiveType.values().length) {
			return PrimitiveType.values()[idx];
		}

		if (javatype.equals(boolean.class) || javatype.equals(Boolean.class)) {
			return PrimitiveType.BOOLEAN;
		}
		if (javatype.equals(Integer.class) || javatype.equals(int.class)) {
			return PrimitiveType.INTEGER;
		}
		if (javatype.equals(Long.class) || javatype.equals(long.class)) {
			return PrimitiveType.LONG;
		}
		if (javatype.equals(Float.class) || javatype.equals(float.class)) {
			return PrimitiveType.FLOAT;
		}
		if (javatype.equals(Double.class) || javatype.equals(double.class)) {
			return PrimitiveType.DOUBLE;
		}
		if (javatype.equals(BigDecimal.class)) {
			return PrimitiveType.BIGDECIMAL;
		}

		if (Number.class.isAssignableFrom(javatype)) {
			return PrimitiveType.BIGDECIMAL;
		}
		if (javatype.equals(Calendar.class) || Date.class.isAssignableFrom(javatype)) {
			String temporalString = JpaMapHelper.getPropTemporal(attr.getDeclaringType().getJavaType().getSimpleName(), attr.getName());
			if (temporalString == null) {
				temporalString = TemporalType.DATE.name();
			}

			TemporalType temporalType = TemporalType.valueOf(temporalString);
			switch (temporalType) {
			case DATE:

				return PrimitiveType.DATE;
			case TIMESTAMP:
				return PrimitiveType.TIMESTAMP;
			default:
				return PrimitiveType.TIME;
			}

		}

		// guess type;
		if (javatype.equals(String.class)) {
			return PrimitiveType.SHORTSTRING;
		}
		return PrimitiveType.OBJECT;
	}

}
