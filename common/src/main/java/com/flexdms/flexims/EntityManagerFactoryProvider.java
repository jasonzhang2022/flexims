package com.flexdms.flexims;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.persistence.dynamic.DynamicClassLoader;

/**
 * How Persistence Unit is managed in a deployment environment. Servlet-only
 * container is quite different from Full-JEE container.
 * 
 * @author jason.zhang
 * 
 */
public interface EntityManagerFactoryProvider {
	EntityManagerFactory getEMF();

	EntityManager createEM();

	String getSchema() throws Exception;

	void refresh(boolean changeSuper);

	DynamicClassLoader getEMFClassLoader();
}
