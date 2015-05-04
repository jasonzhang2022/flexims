package com.flexdms.flexims.accesscontrol;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
@Dependent
public class SecurtiyEMProducer {

	@Produces
	@RequestScoped
	@FxsecurityEM
	public EntityManager getEM() {
		return ACLHelper.getSecurityEM();
	}

	public void closeEM(@Disposes @FxsecurityEM EntityManager em) {
		em.close();
	}
}
