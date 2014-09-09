package com.flexdms.flexims.jpa.textstore;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.eclipse.persistence.config.PersistenceUnitProperties;

@ApplicationScoped
public class TextStoreEMF {
	EntityManagerFactory factory;
	public static final String RESOURCE_NAME = "textstore";

	@PostConstruct
	public void init() throws SQLException, IOException {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(PersistenceUnitProperties.ECLIPSELINK_PERSISTENCE_XML, "META-INF/persistence_textstore.xml");
		factory = Persistence.createEntityManagerFactory(RESOURCE_NAME, properties);
	}

	public ClobData load(String name) {
		EntityManager em = factory.createEntityManager();
		ClobData clobData = em.find(ClobData.class, name);
		em.close();
		return clobData;
	}

	public EntityManager createEntityManager() {
		return factory.createEntityManager();
	}

	public ClobData delete(String name) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		ClobData clobData = em.find(ClobData.class, name);
		if (clobData != null) {
			em.remove(clobData);
		}
		em.getTransaction().commit();
		em.close();
		return clobData;
	}

	public void save(ClobData data) {
		EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		ClobData clobData = em.find(ClobData.class, data.getName());
		if (clobData != null) {
			clobData.setData(data.getData());
		} else {
			em.persist(data);
		}

		em.getTransaction().commit();
		em.close();
	}

	@SuppressWarnings("unchecked")
	public List<ClobData> loadClobByPrefix(String prefix) {
		EntityManager em = factory.createEntityManager();
		javax.persistence.Query query = em.createNamedQuery(ClobData.PREFIX_QUERY);
		query.setParameter("name", prefix + "%");
		return query.getResultList();

	}
}
