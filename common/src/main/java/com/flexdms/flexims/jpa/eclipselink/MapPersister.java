package com.flexdms.flexims.jpa.eclipselink;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

/**
 * How to store the XML Mapping
 * @author jason
 *
 */
public interface MapPersister {

	void save(XMLEntityMappings map);

	XMLEntityMappings load();
}
