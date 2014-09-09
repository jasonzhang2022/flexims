package com.flexdms.flexims.unit.query;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.transaction.Transactional;

@Transactional
@Interceptor
public class TransactionInterceptor {

	@Inject
	EntityManager em;

	@AroundInvoke
	public Object startTransaction(InvocationContext ctx) throws Exception {

		EntityTransaction tx = em.getTransaction();
		if (tx.isActive()) {
			return ctx.proceed();
		}

		tx.begin();
		Object result = null;
		try {
			result = ctx.proceed();
			if (tx.isActive()) {
				tx.commit();
			} else if (tx.getRollbackOnly()) {
				tx.rollback();
			}
			return result;
		} catch (Exception e) {
			try {
				tx.rollback();
			} catch (Exception e1) {
				// ignore nested transaction exception e;
			}
			throw e;

		}

	}
}
