package com.flexdms.flexims.config;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.flexdms.flexims.AppCache;
import com.flexdms.flexims.AppInitializer.AppInitalizeContext;
import com.flexdms.flexims.jpa.eclipselink.FleximsDynamicEntityImpl;

/**
 * Load all configuration at startup
 * 
 * @author jason.zhang
 * 
 */
public class ConfigsInitializer {

	@Inject
	AppCache appCache;

	public void initConfig(@Observes AppInitalizeContext ctx) {
		Map<String, ConfigItem> items = new HashMap<>(100);
		for (Object obj : ctx.em.createQuery("select c from FxConfig c").getResultList()) {
			ConfigItem item = new ConfigItem((FleximsDynamicEntityImpl) obj);
			items.put(item.getName(), item);
		}
		appCache.put(Configs.CACHE_KEY, items);
	}
}
