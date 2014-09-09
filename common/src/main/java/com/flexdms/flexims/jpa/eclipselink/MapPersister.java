package com.flexdms.flexims.jpa.eclipselink;

import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

public interface MapPersister {

	void save(XMLEntityMappings map);

	XMLEntityMappings load();
}
