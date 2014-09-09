package com.flexdms.flexims;


import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.eclipse.persistence.internal.jpa.metadata.xml.XMLEntityMappings;

import com.flexdms.flexims.jpa.eclipselink.DynamicMetaSource;
import com.flexdms.flexims.jpa.eclipselink.MapPersister;
import com.flexdms.flexims.util.ThreadContext;

/**
 * Initialize Persistence Unit and broadcast CDI AppInitializeContext event. Any
 * modules needs initialization should observe this event. E.g
 * {@link AppDataLoader}
 * 
 * @author jason.zhang
 * 
 */
public class AppInitializer {

	public static class AppInitalizeContext {
		public EntityManager em;

	}

	public void initializeApp(@Observes @Initialized ServletContext servletContext) {

		MapPersister mapPersister = App.getInstance(MapPersister.class);
		XMLEntityMappings mappings = mapPersister.load();
		if (mappings != null) {
			DynamicMetaSource.mergeMap(mappings, DynamicMetaSource.getEntityMaps());
		}

		AppInitalizeContext appInitalizeContext = new AppInitalizeContext();
		EntityManagerFactoryProvider entityManagerFactoryProvider = App.getPersistenceUnit();
		// initialize Persister locader
		entityManagerFactoryProvider.getEMF();

		appInitalizeContext.em = App.getEM();

		try {
			App.fireEvent(appInitalizeContext);
		} finally {
			ThreadContext.remove(App.EM_KEY);
			appInitalizeContext.em.close();
		}

	}

}
