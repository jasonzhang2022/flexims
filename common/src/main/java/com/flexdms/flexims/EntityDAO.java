package com.flexdms.flexims;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.flexdms.flexims.jpa.JpaHelper;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

/**
 * Entry point to manage dynamic entity. User can also traditional JPA approach
 * besides this class.
 * 
 * @author jason.zhang
 * 
 */

@RequestScoped
public class EntityDAO {

	@Inject
	EntityManager em;

	@Transactional
	public FleximsDynamicEntityImpl saveEntity(FleximsDynamicEntityImpl entity) {
		if (entity.getId() != 0) {
			entity = em.merge(entity);
		} else {
			em.persist(entity);
		}
		em.flush(); // very important so EntityListener can add more
					// modification to this transaction
		return entity;

	}

	public FleximsDynamicEntityImpl loadEntity(String type, long id) {
		return (FleximsDynamicEntityImpl) em.find(JpaHelper.getEntityClass(em, type), id);
	}

	public FleximsDynamicEntityImpl loadEntity(Class<?> entityClass, long id) {
		return (FleximsDynamicEntityImpl) em.find(entityClass, id);
	}

	@Transactional
	public FleximsDynamicEntityImpl deleteEntity(String type, long id) {
		FleximsDynamicEntityImpl entityImpl = loadEntity(type, id);
		if (entityImpl != null) {
			em.remove(entityImpl);
			em.flush(); // very important so EntityListener can add more
						// modification to this transaction
			return entityImpl;
		}

		return null;

	}

}
