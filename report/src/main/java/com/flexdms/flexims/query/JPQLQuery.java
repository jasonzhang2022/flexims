package com.flexdms.flexims.query;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

@SuppressWarnings({ "rawtypes" })
@QuerySubtype(JPQLQuery.TYPE_NAME)
public class JPQLQuery extends TypedQuery {

	public static final Logger LOGGER = Logger.getLogger(ConditionQuery.class.getName());
	public static final String TYPE_NAME = "JPQL";

	public static final String PROP_NAME_QUERY_TEXT = "JPQLText";

	public JPQLQuery(FleximsDynamicEntityImpl entityImpl) {
		super(entityImpl);
	}

	@Override
	public void buildQuery(EntityManager em) {

	}

	@Override
	public void cleanCache(EntityManager em) {

	}

	@Override
	public List<Parameter> getParameters() {
		return null;
	}

	@Override
	public List fetchAllResult(EntityManager em) {
		return null;
	}

	@Override
	public int getResultCount(EntityManager em) {
		return 0;
	}

	@Override
	public List fetchPartialResult(int offset, int length, EntityManager em) {
		return null;
	}

	@Override
	public List getResult() {
		return null;
	}

}
