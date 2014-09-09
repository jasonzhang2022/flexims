package com.flexdms.flexims;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.EternalExpiryPolicy;
import javax.cache.spi.CachingProvider;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
public class LocalCache implements AppCache, Serializable {

	public static final String FLEXIMS_CACHE_KEY = "com.flexdms.flexims.app";

	private static final long serialVersionUID = 1L;

	protected Cache<String, Object> cache;

	@PostConstruct
	public void init() {
		CachingProvider provider = Caching.getCachingProvider();
		CacheManager manager = provider.getCacheManager();
		MutableConfiguration<String, Object> config = new MutableConfiguration<String, Object>();

		config.setTypes(String.class, Object.class).setStoreByValue(false).setStatisticsEnabled(true)
				.setExpiryPolicyFactory(FactoryBuilder.factoryOf(new EternalExpiryPolicy<String, Object>()));
		cache = manager.configureCache(FLEXIMS_CACHE_KEY, config);
	}

	@PreDestroy
	public void close() {
		cache.close();
	}

	public Object get(String key) {
		return cache.get(key);
	}

	@Override
	public void put(String key, Object obj) {
		cache.put(key, obj);

	}

}
