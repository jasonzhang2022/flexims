/**
 * Provide System Initialization event and Persistence Unit Management.
 * <ul>
 *  <li> Any code can use static method {@link com.flexdms.flexims.App#getEM() } to retrieve EntityManager </li>
 *  <li> EntityManager can also be injected @inject EntityManager em. The bean with this injection should be RequestScope so it does not hold
 *  a stale EntityManager</li>
 *  <li>Any modules needs to perform initialization code should @observes {@link com.flexdms.flexims.AppInitializer.AppInitalizeContext} </li>
 *  <li> {@link com.flexdms.flexims.AppDataLoader} can load your sql file and entity xml file at system start up time </li>
 * </ul>
 *
 */
package com.flexdms.flexims;