package com.flexdms.flexims;

import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import com.flexdms.flexims.util.ThreadContext;

/**
 * A class to interface with CDI and traditional service lookup. And entry point
 * to access global resources such as EntityManage
 * 
 * @author jason.zhang
 * 
 */
public final class App {

	private App() {
		
	}
	public static final Logger LOGGER = java.util.logging.Logger.getLogger(App.class.getName());

	public static <T> T getInstance(final Class<T> cls) {
		return BeanProvider.getContextualReference(cls, false);
	}

	public static EntityManagerFactoryProvider getPersistenceUnit() {
		return getInstance(EntityManagerFactoryProvider.class);
	}

	public static DataSource getDataSource() {
		return getInstance(DataSource.class);
	}

	public static Connection getConnection() {
		return getInstance(Connection.class);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<Class<? extends T>> findSubClasses(final Class<T> clz) {
		Set<?> bs = BeanProvider.getBeanDefinitions(clz, false, true);
		List<Class<? extends T>> ls = new ArrayList<Class<? extends T>>(bs.size());
		for (Object obj : bs) {
			Bean b = (Bean) obj;
			Class c = b.getBeanClass();
			if (c != clz) {
				ls.add(c);
			}

		}
		return ls;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> List<Class<? extends T>> findSubClasses(Class<T> clz, Class<? extends Annotation> qualifier) {
		Set<?> bs = BeanProvider.getBeanDefinitions(clz, false, true);
		List<Class<? extends T>> ls = new ArrayList<Class<? extends T>>(bs.size());
		for (Object obj : bs) {
			Bean b = (Bean) obj;
			if (b.getQualifiers().contains(qualifier)) {
				Class c = b.getBeanClass();
				ls.add(c);
			}
		}
		return ls;
	}

	public static BeanManager getBeanManager() {
		return CDI.current().getBeanManager();
	}

	public static void fireEvent(Object event, Annotation... qualifers) {
		BeanManager beanManager = getBeanManager();
		beanManager.fireEvent(event, qualifers);
	}

	public static final String EM_KEY = "_entityManager";

	public static EntityManager getEM() {
		EntityManager eManager = (EntityManager) ThreadContext.get("_entityManager");

		if (eManager == null || !eManager.isOpen()) {
			eManager = getPersistenceUnit().createEM();
			ThreadContext.put(EM_KEY, eManager);
		}
		return eManager;
	}

}
