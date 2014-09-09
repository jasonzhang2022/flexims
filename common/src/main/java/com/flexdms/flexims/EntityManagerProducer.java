package com.flexdms.flexims;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;

/**
 * Integrate static EntityManager with EntityManager Injection by CDI
 * 
 * @author jason.zhang
 * 
 */
public class EntityManagerProducer {

	/**
	 * Make this dependent scope When Proxyed EntityManager is passed to eclipse
	 * internal, eclipselink will give infinite loop
	 * 
	 * If the Entity Manager is requested scoped and proxied by CDI, it will has
	 * a loop.
	 * 
	 * Instead we return a dependent bean which is not proxied by CDI by
	 * default. However, since we use App.getEN(), we are using the same EM in
	 * one thread.
	 * 
	 * Since this dependent scope, All using class should not keep a reference
	 * since the lookped up reference could be stale
	 * 
	 * @return
	 */
	@Produces
	public EntityManager retrieveEntityManager() {
		return App.getEM();
	}

}
