package com.flexdms.flexims.config;

import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

import org.apache.deltaspike.core.spi.config.ConfigSource;

/**
 * Provide our configuration as a configuration source for DeltaSpike
 * Configuration system.
 * 
 * @author jason.zhang
 * 
 */
public class DBConfigSource implements ConfigSource {

	public static final int DEFAULT_ORDINAL = 500;

	@Override
	public int getOrdinal() {
		return DEFAULT_ORDINAL;
	}

	public DBConfigSource() {
		super();
	}

	@Override
	public String getPropertyValue(String key) {
		try {
			return Configs.getConfig(key);
		} catch (IllegalStateException e) {
			//CDI is not ready yet.
			return null;
		}
	}

	@Override
	public String getConfigName() {
		return "flexims/globalConfig";
	}

	@Override
	public boolean isScannable() {
		return false;
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}

}
