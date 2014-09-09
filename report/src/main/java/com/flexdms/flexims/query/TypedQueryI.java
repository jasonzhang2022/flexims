package com.flexdms.flexims.query;

import java.util.List;

import javax.persistence.EntityManager;
@SuppressWarnings("rawtypes")
public interface TypedQueryI {
	/**
	 * Build the underlying query support. Only after this call, other methods
	 * in query can be used.
	 * 
	 * @param types
	 * @param em
	 * @param queries
	 */
	void buildQuery(EntityManager em);

	/**
	 * Clean all the cache and prepare to start from fresh state Just like the
	 * TypedQuery is constructed, but keep the Parameter state.
	 */
	void cleanCache(EntityManager em);

	/**
	 * 
	 * This method can be used after buildQuery;
	 * 
	 * @return availabel parameters.
	 */
	List<Parameter> getParameters();

	/**
	 * Execute the query and set result;
	 * 
	 * @param em
	 */
	List fetchAllResult(EntityManager em);

	/**
	 * How many result do we expect
	 * 
	 * @param em
	 */
	int getResultCount(EntityManager em);

	/**
	 * return part of the result
	 * 
	 * @param offset
	 * @param length
	 */
	List fetchPartialResult(int offset, int length, EntityManager em);

	/**
	 * Return the last cached result. we have this to avoid query DB repeatedly.
	 * 
	 * @return last result returned or null
	 */
	List getResult();

	/**
	 * 
	 * @return whether the query is valid
	 */
	boolean isValid();

	void setQueryContext(QueryContext qContext);

	QueryContext getQueryContext();

}
