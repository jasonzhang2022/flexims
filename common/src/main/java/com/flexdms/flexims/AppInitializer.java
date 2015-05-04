package com.flexdms.flexims;


import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.deltaspike.core.api.lifecycle.Initialized;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.api.provider.DependentProvider;
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
@Dependent
public class AppInitializer {

	public static class AppInitalizeContext {
		public EntityManager em;

	}

	/**
	 * Listen servlet context initialization event from deltaspike. 
	 * Not use injection but look up bean instance since we want to control the order of bean instantiation.
	 * @param servletContext
	 */
	public void initializeApp(@Observes @Initialized ServletContext servletContext) {

		DependentProvider<MapPersister> myBeanProvider = BeanProvider.getDependent(MapPersister.class);
		MapPersister mapPersister= myBeanProvider.get();
		
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
			myBeanProvider.destroy();
		}

	}

}
