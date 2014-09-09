package com.flexdms.flexims.jpa.eclipselink;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.internal.dynamic.DynamicEntityImpl;
import org.eclipse.persistence.oxm.annotations.XmlVirtualAccessMethods;

/**
 * Parent Class for all our Dynamic Entity
 * 
 * @author jason.zhang
 * 
 */
@XmlTransient
@XmlVirtualAccessMethods(getMethod = "realGet", setMethod = "realSet")
public abstract class FleximsDynamicEntityImpl extends DynamicEntityImpl implements EntityProvider {

	public static final int INTIAL_META_ATTRS_NUM = 5;
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	public Map<String, Object> metaAttributes = new HashMap<String, Object>(INTIAL_META_ATTRS_NUM);

	public Map<String, Object> getMetaAttributes() {
		return metaAttributes;
	}

	public void setMetaAttributes(Map<String, Object> metaAttributes) {
		this.metaAttributes = metaAttributes;
	}

	public Object getMetaAttribute(String key) {
		return metaAttributes.get(key);
	}

	public void putMetaAttribute(String key, Object value) {
		metaAttributes.put(key, value);
	}

//	public Object realGet(String propertyName) {
//		return this.get(propertyName);
//	}
//
//	public void realSet(String propertyName, Object value) {
//		super.set(propertyName, value);
//	}

	public long getId() {
		Long idLong = super.get(ID_NAME);
		if (idLong == null) {
			return 0;
		} else {
			return idLong.longValue();
		}
	}

	public void setId(long id) {
		super.set(ID_NAME, id);
	}

	public static final String VERSION_NAME = "fxversion";
	public static final String ID_NAME = "id";

	public Timestamp getVersion() {
		return super.get(VERSION_NAME);
	}

	public void setVersion(Timestamp version) {
		super.set(VERSION_NAME, version);
	}

	public boolean hasProperty(String propertyName) {
		return fetchPropertiesManager().contains(propertyName);
	}

	public boolean isEmbedded() {
		return !fetchPropertiesManager().contains(ID_NAME);
	}

	public List<String> getPropertyNames() {
		return fetchPropertiesManager().getPropertyNames();
	}

	@Override
	public int hashCode() {
		if (!isEmbedded()) {
			Long id = super.get(ID_NAME);
			Timestamp version = super.get(VERSION_NAME);
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((version == null) ? 0 : version.hashCode());
			return result;
		}
		// //if we compare properties, we could trigger lazy loading
		return this.getClass().getName().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		FleximsDynamicEntityImpl other = (FleximsDynamicEntityImpl) obj;
		if (!isEmbedded()) {
			Long id = super.get(ID_NAME);
			Long otherid = other.get(ID_NAME);
			if (id == null || otherid != null) {
				return false; // new entity does not equal entity from database
			} else if (!id.equals(otherid)) {
				return false;
			}
			Timestamp version = super.get(VERSION_NAME);
			if (version == null || other.getVersion() == null) {
				return false; // new entity does not equal entity from database
			} else if (!version.equals(other.getVersion())) {
				return false;
			}
			return true;
		}

		// embedded case. Embedded does not have id and version
		// if we compare properties, we could trigger lazy loading
		return false;

	}

	@Override
	public FleximsDynamicEntityImpl getEntity() {
		return this;
	}
}
